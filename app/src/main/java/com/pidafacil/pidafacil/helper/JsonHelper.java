package com.pidafacil.pidafacil.helper;

import com.pidafacil.pidafacil.beans.CardBean;
import com.pidafacil.pidafacil.beans.CashBean;
import com.pidafacil.pidafacil.beans.SimpleProductBean;
import com.pidafacil.pidafacil.beans.TmoneyBean;
import com.pidafacil.pidafacil.model.Ingredient;
import com.pidafacil.pidafacil.model.Option;
import com.pidafacil.pidafacil.model.Product;
import com.pidafacil.pidafacil.model.RestaurantRequest;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by victor on 03-06-15.
 */
public class JsonHelper {

    public static JSONObject parseProduct(Product bean){

        JSONObject product = new JSONObject();
        JSONArray arr = new JSONArray();

        product.put("quantity",bean.getQuantity());
        product.put("product_id",bean.getId());
        product.put("comment",bean.getComment());

        for(Ingredient b : bean.getIngredients()){
            JSONObject o = new JSONObject();
            try{
                o.put("ingredient_id",b.getId());
                o.put("selected",b.getSelected());
            }catch (Exception e){
                System.out.println("JSONException"+e.getMessage());
                continue;
            }
            arr.add(o);
        }

        product.put("ingredients",arr);
        arr = new JSONArray();
        for(Option b : bean.getOptions()){
            JSONObject o = new JSONObject();
            try{
                o.put("condition_id",b.getConditionId());
                o.put("condition_option_id",b.getConditionOptionId());
            }catch (Exception e){
                System.out.println("JSONException"+e.getMessage());
                continue;
            }
            arr.add(o);
        }
        product.put("options", arr);
        return product;
    }

    /*
    * Preparando JSONObject para enviar al servicio web.
    * address_id : id de la direccion seleccionada por el user
    * service_type_id : { 1 = domicilio, 2 = para llevar }
    * payment_method_id : { 1 = Tarjeta, 2 = Efectivo }
    * user_id : id del usuario guardado en sesion.
    * */
    public static JSONObject parseRequest(RestaurantRequest bean0,
                                          int loginId,
                                          Object payMethod,
                                          String[] hours,
                                          String customer,
                                          String customerPhone) {
        JSONObject object = new JSONObject();
        JSONArray array = new JSONArray();

        object.put("restaurant_id", bean0.getId());
        object.put("service_type_id", bean0.getServiceType());
        object.put("payment_method_id", bean0.getPayMethod());
        object.put("user_id",loginId);
        object.put("customer", customer);
        object.put("customer_phone", customerPhone);
        object.put("source_device", "Android");

        if(bean0.getServiceType() == 2){
            object.put("res_address_id", bean0.getAddressId());
        } else {
            object.put("address_id", bean0.getAddressId());
        }

        /**
         * Metodos de pago y detalles del pedido
         * */
        if(hours.length>1){
            object.put("pickup_hour",hours[0]);
            object.put("pickup_min",hours[1]);
        }

        if(payMethod instanceof CardBean){
            object.put("credit_name",((CardBean) payMethod).getName());
            object.put("credit_card",((CardBean) payMethod).getNumber());
            object.put("credit_expmonth",((CardBean) payMethod).getMonth());
            object.put("credit_expyear",((CardBean) payMethod).getYear());
            object.put("secure_code",((CardBean) payMethod).getCode());
            object.put("pay_bill","");
            object.put("bin",((CardBean) payMethod).getBin());
        }else if(payMethod instanceof CashBean){
            object.put("pay_bill",((CashBean) payMethod).getAmmount());
            object.put("credit_name","");
            object.put("credit_card","");
            object.put("credit_expmonth","");
            object.put("secure_code","");
            object.put("credit_expyear","");
        }else if (payMethod instanceof TmoneyBean){
            object.put("num_tigo_money",((TmoneyBean) payMethod).getNum_tigo_money());
            object.put("billetera_user",((TmoneyBean) payMethod).getBilletera_user());
            object.put("credit_name","");
            object.put("credit_card","");
            object.put("credit_expmonth","");
            object.put("secure_code","");
            object.put("credit_expyear","");

        }

        for (Product bean : bean0.getProducts()) {
            JSONObject product = JsonHelper.parseProduct(bean);
            array.add(product);
        }

        object.put("products",array);
        return object;
    }

    public static String jsonArrayToString(JSONArray array){
        return array.toJSONString();
    }
    public static String listToJsonString(List<?> list){
        return JSONArray.toJSONString(list);
    }

    public static List<SimpleProductBean> parseSimpleProducts(org.json.JSONObject data) throws JSONException {
        org.json.JSONArray prods = data.getJSONArray("products");
        List<SimpleProductBean> l = new ArrayList<SimpleProductBean>();

        for(int i=0; i<prods.length(); i++){
            org.json.JSONObject p = prods.getJSONObject(i);
            String name = p.getString("product");
            String price = p.getString("total_price");
            int q = p.getInt("quantity");

            if(q>1)
                name = String.format("%dx %s",q,name);
            price = String.format("$%s",price);

            l.add(new SimpleProductBean(name,price));
        }

        return l;
    }

}
