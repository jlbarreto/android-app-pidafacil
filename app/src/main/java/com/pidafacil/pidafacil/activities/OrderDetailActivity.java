package com.pidafacil.pidafacil.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.pidafacil.pidafacil.R;
import com.pidafacil.pidafacil.beans.SimpleProductBean;
import com.pidafacil.pidafacil.components.CustomLinearLayoutManager;
import com.pidafacil.pidafacil.components.ProductsOrderAdapter;
import com.pidafacil.pidafacil.helper.JsonHelper;
import com.pidafacil.pidafacil.helper.RealmHelper;
import com.pidafacil.pidafacil.helper.UIHelper;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

public class OrderDetailActivity extends ActionBarActivity {
    private UD U = UD.getInstance();
    private RecyclerView products;
    @Bind(R.id.loading_) CircularProgressView loading;
    @Bind(R.id.txt_p_name) TextView name;
    @Bind(R.id.txt_p_price) TextView price;
    @Bind(R.id.txt_type_service) TextView type;
    @Bind(R.id.button_add_to_car) Button buttoncar;
    Realm r;

    public static final String ORDER_DETAIL = "order_detail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        ButterKnife.bind(this);
        products = (RecyclerView) findViewById(R.id.rec_order_products);
        products.setLayoutManager(new CustomLinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL, false));
        configure();
    }

    void configure(){
        r = Realm.getInstance(getApplicationContext());
        Bundle extras = getIntent().getExtras();
        setTitle("Orden #" + extras.getString(ORDER_DETAIL));
        VoidTask task = new VoidTask("/order/get");
         task.addParam("order_id", UD.getInstance().get("order_id").toString());
         task.setPostExecute(new VoidTask.PostExecute() {
             @Override
             public void execute(StringBuilder response) {
                 Log.d("orderG:WS-RESULT", response.toString());
                 try {
                     JSONObject data = new JSONObject(response.toString());
                     U.put("data__", data);
                     org.json.JSONObject d = data.getJSONObject("data");
                     name.setText(d.getString("restaurant_name"));
                     price.setText(d.getString("order_total"));
                     type.setText(typeService(d.getString("service_type_id")));
                     List<SimpleProductBean> beans = JsonHelper.parseSimpleProducts(d);
                     products.setAdapter(new ProductsOrderAdapter(beans));
                     loading.setVisibility(View.GONE);
                     buttoncar.setVisibility(View.VISIBLE);
                 } catch (JSONException e) {
                     Log.d("INFO", e.getMessage());
                 }
             }
         });
        task.execute();
    }

    String typeService(String type){

        if(type.equals("2")) {
            return "Servicio para llevar";
        } else {
            return "Servicio a domicilio";
        }

    }

    @OnClick(R.id.button_add_to_car)
    void addToCar(View v){
        RealmResults requests = r.allObjects(RestaurantRequest.class);
        boolean hasElements = requests != null;
        if(hasElements)
            hasElements = (requests.size() > 0 );

        if(hasElements){
            AlertDialog.Builder builder = UIHelper.alert(OrderDetailActivity.this, null,
                    "Tienes una orden pendiente de procesar. \n Si agregas los productos eliminarías la orden anterior.");
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    addProduct();
                }
            });

            AlertDialog d = builder.create();
            d.requestWindowFeature(Window.FEATURE_NO_TITLE);
            d.show();
        } else {
            AlertDialog.Builder builder = UIHelper.alert(OrderDetailActivity.this, null,
                    "¿Deseas agregar estos productos al carrito?");
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    addProduct();
                }
            });

            AlertDialog d = builder.create();
            d.requestWindowFeature(Window.FEATURE_NO_TITLE);
            d.show();
        }
    }

    void addProduct(){
        org.json.JSONObject data = (org.json.JSONObject) UD.getInstance().get("data__");

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
        AlertDialog.Builder b = UIHelper.alert(OrderDetailActivity.this, null, "Nuevo producto agregado al carrito.");
        b.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startActivity(new Intent(getApplicationContext(), ShoppingCartActivity.class));
            }
        });
        AlertDialog a = b.create();
        a.requestWindowFeature(Window.FEATURE_NO_TITLE);
        a.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_order_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.home)
            NavUtils.navigateUpFromSameTask(this);

        return super.onOptionsItemSelected(item);
    }
}
