package com.pidafacil.pidafacil.components;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pidafacil.pidafacil.activities.OrderDetailActivity;
import com.pidafacil.pidafacil.R;
import com.pidafacil.pidafacil.beans.OrderBean;
import com.pidafacil.pidafacil.helper.RealmHelper;
import com.pidafacil.pidafacil.model.Ingredient;
import com.pidafacil.pidafacil.model.Option;
import com.pidafacil.pidafacil.model.Product;
import com.pidafacil.pidafacil.model.RestaurantRequest;
import com.pidafacil.pidafacil.singleton.UD;
import com.pidafacil.pidafacil.task.VoidTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by victor on 04-14-15.
 */
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderHolder> {
    private List beans;
    private Realm r;

    public OrderAdapter(List beans, Context ctx) {
        this.beans = beans;
        r = Realm.getInstance(ctx);
    }

    @Override
    public OrderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item_order, parent, false);
        return new OrderHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderHolder holder, int position) {
        final OrderBean bean = (OrderBean) beans.get(position);
        holder.textViewId.setText(bean.getId().toString());
        holder.textViewName.setText(bean.getName());
        holder.textViewPrice.setText(bean.getTotal());
        holder.textViewCreated.setText(bean.getCreateAt());
        holder.textStatus.setText(bean.getStatus());
        holder.textOrderCode.setText("Orden #"+bean.getOrderCode());
        holder.orderDetailLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = (View) v.getParent().getParent();
                TextView id = (TextView) view.findViewById(R.id.txt_id_);
                UD.getInstance().put("order_id", id.getText().toString());
                Context ctx = v.getContext();
                Intent activity = new Intent(ctx, OrderDetailActivity.class);
                activity.putExtra(OrderDetailActivity.ORDER_DETAIL, bean.getOrderCode());
                ctx.startActivity(activity);
            }
        });
    }

    // Funcionalidad de OrderDetail copiada
    // porque se debe repetir pedido
    private void addToCar(){
        RealmResults requests = r.allObjects(RestaurantRequest.class);
        boolean hasElements = requests != null;
        if(hasElements)
            hasElements = (requests.size() > 0 );

        if(hasElements){
            AlertDialog.Builder builder = (AlertDialog.Builder) UD.getInstance().get("order_dialog1");
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    VoidTask task = new VoidTask("/order/get");
                    task.addParam("order_id", UD.getInstance().get("order_id").toString());
                    task.setPostExecute(new VoidTask.PostExecute() {
                        @Override
                        public void execute(StringBuilder response) {
                            Log.d("AgregarOr:WS-RESULT",response.toString());
                            try {
                                JSONObject data = new JSONObject(response.toString());
                                UD.getInstance().put("data__", data);
                                addProduct();
                            } catch (JSONException e) {
                                Log.d("INFO", e.getMessage());
                            }
                        }
                    });
                    task.execute();
                }
            });

            AlertDialog d = builder.create();
            d.requestWindowFeature(Window.FEATURE_NO_TITLE);
            d.show();
        }else{
            AlertDialog.Builder builder = (AlertDialog.Builder) UD.getInstance().get("order_dialog2");
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    VoidTask task = new VoidTask("/order/get");
                    task.addParam("order_id", UD.getInstance().get("order_id").toString());
                    task.setPostExecute(new VoidTask.PostExecute() {
                        @Override
                        public void execute(StringBuilder response) {
                            Log.d("OrderG:WS-RESULT",response.toString());
                            try {
                                JSONObject data = new JSONObject(response.toString());
                                UD.getInstance().put("data__", data);
                                addProduct();
                            } catch (JSONException e) {
                                Log.d("INFO", e.getMessage());
                            }
                        }
                    });
                    task.execute();
                }
            });

            AlertDialog d = builder.create();
            d.requestWindowFeature(Window.FEATURE_NO_TITLE);
            d.show();
        }
    }

    void addProduct(){
        org.json.JSONObject data = (org.json.JSONObject) UD.getInstance().get("data__");
        Log.d("INFO", data.toString());

        try {
            data = data.getJSONObject("data");
            int id = data.getInt("restaurant_id");
            String name = data.getString("restaurant_name");

            RealmResults<RestaurantRequest> l = r.allObjects(RestaurantRequest.class);
            boolean ok = true;

            if(l != null)
                if(l.size()>0)
                    ok = new RealmHelper().clearRequest(r, l);

            if(ok){
                r.beginTransaction();
                RestaurantRequest request = r.createObject(RestaurantRequest.class);
                request.setId(id);
                request.setName(name);

                JSONArray arr = data.getJSONArray("products");
                for(int i = 0; i<arr.length(); i++){
                    org.json.JSONObject p = arr.getJSONObject(i);
                    Product p0 = r.createObject(Product.class);
                    p0.setId(p.getInt("product_id"));
                    p0.setNombre(p.getString("product"));
                    p0.setQuantity(p.getInt("quantity"));
                    p0.setValue(Float.parseFloat(p.getString("unit_price")));
                    p0.setDetails("");

                    JSONArray arr0 = p.getJSONArray("ingredients");
                    for(int j = 0; j<arr0.length(); j++){
                        org.json.JSONObject o = arr0.getJSONObject(j);
                        Ingredient in = r.createObject(Ingredient.class);
                        in.setId(o.getInt("ingredient_id"));
                        in.setName(o.getString("ingredient"));
                        in.setSelected(o.getInt("remove") == 0? 1: 0);
                        p0.getIngredients().add(in);
                    }

                    JSONArray arr1 = p.getJSONArray("conditions");
                    for(int j=0; j<arr1.length(); j++){
                        org.json.JSONObject o = arr1.getJSONObject(j);
                        Option opt = r.createObject(Option.class);
                        opt.setId("");
                        opt.setConditionId(o.getInt("condition_id"));
                        opt.setName(o.getString("condition_option"));
                        opt.setConditionOptionId(o.getInt("condition_option_id"));
                        p0.getOptions().add(opt);
                    }

                    request.getProducts().add(p0);
                }
                r.commitTransaction();
                success();
            }

        } catch (JSONException e) {
            Log.d("addProduct:PARSE-ERROR",e.getMessage());
            try{
                r.cancelTransaction();
            }catch (Exception exx){ }
        }
    }

    void success(){
        AlertDialog.Builder b = (AlertDialog.Builder) UD.getInstance().get("order_dialog");
        AlertDialog a = b.create();
        a.requestWindowFeature(Window.FEATURE_NO_TITLE);
        a.show();
    }

    @Override
    public int getItemCount() {
        return beans.size();
    }

    public class OrderHolder extends RecyclerView.ViewHolder{

        protected TextView textViewId;
        protected TextView textViewName;
        protected TextView textViewPrice;
        protected TextView textViewCreated;
        protected TextView textStatus;
        protected TextView textOrderCode;
        protected LinearLayout orderDetailLayout;

        public OrderHolder(View view){
            super(view);
            this.textViewId = (TextView) view.findViewById(R.id.txt_id_);
            this.textViewName = (TextView) view.findViewById(R.id.txt_order_name);
            this.textViewPrice = (TextView) view.findViewById(R.id.txt_order_total);
            this.textViewCreated = (TextView) view.findViewById(R.id.txt_createAt);
            this.textStatus = (TextView) view.findViewById(R.id.txt_status_);
            this.textOrderCode = (TextView) view.findViewById(R.id.txt_order_code);
            this.orderDetailLayout = (LinearLayout) view.findViewById(R.id.orderDetailLayout);
        }
    }
}
