package com.pidafacil.pidafacil.components;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.pidafacil.pidafacil.R;
import com.pidafacil.pidafacil.activities.ShoppingCartActivity;
import com.pidafacil.pidafacil.helper.RealmHelper;
import com.pidafacil.pidafacil.helper.UIHelper;
import com.pidafacil.pidafacil.model.Ingredient;
import com.pidafacil.pidafacil.model.Option;
import com.pidafacil.pidafacil.model.Product;
import com.pidafacil.pidafacil.model.RestaurantRequest;
import com.pidafacil.pidafacil.singleton.UD;
import com.pidafacil.pidafacil.util.Parser;
import com.squareup.picasso.Picasso;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by victor on 03-13-15.
 */
public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestHolder>{
    public RealmList<Product> beans;
    private ShoppingCartActivity activity;
    public RequestAdapter(RealmList<Product> beans, ShoppingCartActivity activity){
        this.beans = beans;
        this.activity = activity;
    }

    @Override
    public int getItemCount() {
        return beans.size();
    }

    @Override
    public void onBindViewHolder(RequestHolder holder, int i) {
        Product bean = beans.get(i);
        holder.textView.setText(bean.getNombre());
        holder.spinnerQuantity.setSelection(bean.getQuantity()-1);
        double t = bean.getValue() * bean.getQuantity();
        holder.textTotal.setText("$"+String.valueOf(Parser.decimalFormat(t)));
        holder.position_element.setText(String.valueOf(i));
        holder.spinnerQuantity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                ViewParent parentElement = v.getParent();
                LinearLayout linearLayout = (LinearLayout) (parentElement.getParent());
                TextView positionElement = (TextView) linearLayout.findViewById(R.id.position_element);
                Realm realm = Realm.getInstance(v.getContext());
                RealmResults<RestaurantRequest> l = realm.allObjects(RestaurantRequest.class);
                realm.beginTransaction();
                RestaurantRequest request = l.first();
                Spinner spinner = ((Spinner)
                        linearLayout.findViewById(R.id._spinner_quantity));

                Integer quantity = Integer.valueOf(spinner.getSelectedItem().toString());
                Product product = request.getProducts().get(Integer.valueOf(
                        positionElement.getText().toString()));
                product.setQuantity(quantity);

                double t = quantity * product.getValue();

                ((TextView) linearLayout.getChildAt(2))
                        .setText("$" + Parser.decimalFormatString(t));
                activity.total(request.getProducts());
                realm.commitTransaction();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        holder.textDel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewParent parent = v.getParent();
                LinearLayout linearLayout = (LinearLayout) parent.getParent();
                TextView positionElement = (TextView) linearLayout.findViewById(R.id.position_element);
                UD.getInstance().put("element_", positionElement);
                UD.getInstance().put("view__", v);
                AlertDialog.Builder alertDialogBuilder = UIHelper.alert(activity,
                        "Eliminar producto",
                        "¿Estas seguro que deseas eliminar este producto?")
                        .setCancelable(true)
                        .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                TextView positionElement = (TextView) UD.getInstance().get("element_");
                                View v = (View) UD.getInstance().get("view__");

                                Realm realm = Realm.getInstance(v.getContext());
                                RealmResults<RestaurantRequest> l = realm.allObjects(RestaurantRequest.class);
                                boolean hasElements = true;

                                realm.beginTransaction();
                                for(int x = 0; x < l.size(); x++){
                                    RestaurantRequest request = l.get(x);
                                    Product p = request.getProducts().get(Integer.valueOf(positionElement.getText().toString()));
                                    RealmList<Ingredient> ingr = p.getIngredients();
                                    RealmList<Option> op = p.getOptions();

                                    for(int i = 0 ; i<ingr.size(); i++)
                                        ingr.get(i).removeFromRealm();
                                    for(int i = 0 ; i<op.size(); i++)
                                        op.get(i).removeFromRealm();

                                    p.removeFromRealm();
                                    hasElements = request.getProducts().size() > 0;
                                }
                                realm.commitTransaction();

                                if(!hasElements){
                                    RealmResults<RestaurantRequest> results = realm.allObjects(RestaurantRequest.class);
                                    new RealmHelper().clearRequest(realm, results);
                                    activity.notData();
                                }else{
                                    activity.reloadProducts();
                                }
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                alertDialogBuilder.show();

            }
        });


    }

    @Override
    public RequestHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.recycler_item_shoppingcart_product_detail, viewGroup, false);
        return new RequestHolder(itemView);
    }

    public class RequestHolder extends RecyclerView.ViewHolder{
        protected TextView textView;
        protected Spinner spinnerQuantity;
        protected TextView textTotal;
        protected ImageView textDel;
        protected ImageView textInfo;
        protected TextView position_element;

        public RequestHolder(View view){
            super(view);
            this.textView = (TextView) view.findViewById(R.id.text_prod_name);
            this.spinnerQuantity = (Spinner) view.findViewById(R.id._spinner_quantity);

            ArrayAdapter adapter = ArrayAdapter.createFromResource(view.getContext(),
                    R.array.products_quantity, R.layout.spinner_dropdown_item_custom_number);
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_custom_item);
            this.spinnerQuantity.setAdapter(adapter);
            this.textTotal = (TextView) view.findViewById(R.id.text_prod_total_price);
            this.textDel = (ImageView) view.findViewById(R.id.text_delete_this);
            this.textInfo = (ImageView) view.findViewById(R.id.text_info_this);
            this.position_element = (TextView) view.findViewById(R.id.position_element);

            this.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    info(v);
                }
            });
            this.textInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    info(v);
                }
            });

        }

        void info(View v){
            ViewParent parentElement = v.getParent();
            LinearLayout linearLayout = (LinearLayout) parentElement.getParent();
            TextView positionElement = (TextView) linearLayout.findViewById(R.id.position_element);
            Realm realm = Realm.getInstance(v.getContext());
            RealmResults<RestaurantRequest> l = realm.allObjects(RestaurantRequest.class);
            RestaurantRequest request = l.first();
            Product product = request.getProducts().get(Integer.valueOf(
                    positionElement.getText().toString()));
            Dialog d = ((Dialog) UD.getInstance().get("D_SHOW"));
            ImageView image = (ImageView) d.findViewById(R.id.img_car_product);
            TextView name = (TextView) d.findViewById(R.id.txt_d_prod_name);
            TextView unitPrice = (TextView) d.findViewById(R.id.txt_d_prod_unitPrice);
            TextView description = (TextView) d.findViewById(R.id.txt_d_details);
            TextView ingredients = (TextView) d.findViewById(R.id.txt_d_ingr);
            TextView options = (TextView) d.findViewById(R.id.txt_d_opciones);
            TextView comment = (TextView) d.findViewById(R.id.txt_d_comment);

            name.setText(product.getNombre());
            description.setText(product.getDetails());

            StringBuilder str = new StringBuilder("");

            if(product.getIngredients().size() <= 0){
                ingredients.setVisibility(View.GONE);
                d.findViewById(R.id.txt_s_ingr).setVisibility(View.GONE);
            }else{
                d.findViewById(R.id.txt_s_ingr).setVisibility(View.VISIBLE);
                for(Ingredient ing : product.getIngredients()){
                    if(ing.getSelected() == 1)
                        str.append(ing.getName())
                                .append(",");
                }

                String substr = str.substring(0, str.length()-1);
                substr.concat("\n");
                ingredients.setText(substr);
            }

            if(product.getOptions().size() <= 0){
                d.findViewById(R.id.txt_s_opciones).setVisibility(View.GONE);
                options.setVisibility(View.GONE);
            }else{
                d.findViewById(R.id.txt_s_opciones).setVisibility(View.VISIBLE);
                str = new StringBuilder("");
                for(Option op : product.getOptions()){
                    str.append(op.getName()).append(",");
                }

                String substr = str.substring(0, str.length()-1);
                substr.concat("\n");
                options.setText(substr);
            }

            unitPrice.setText("$" + Parser.decimalFormatString(product.getValue()));
            if((product.getComment() != null) && (!product.getComment().equals(""))){
                d.findViewById(R.id.txt_s_comment).setVisibility(View.VISIBLE);
                comment.setText(product.getComment());
            }else{
                d.findViewById(R.id.txt_s_comment).setVisibility(View.GONE);
                comment.setText("");
            }

            if(product.getImageUri()!=null && !product.getImageUri().isEmpty()){
                Picasso.with(UD.getInstance().getContext())
                        .load(product.getImageUri()).into(image);
            }
            d.show();
        }

    }
}
