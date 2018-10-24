package com.pidafacil.pidafacil.helper;

import android.util.Log;

import com.pidafacil.pidafacil.beans.AddressBean;
import com.pidafacil.pidafacil.beans.GeneralOptionsBean;
import com.pidafacil.pidafacil.beans.OrderBean;
import com.pidafacil.pidafacil.beans.ProductBean;
import com.pidafacil.pidafacil.beans.RestaurantBean;
import com.pidafacil.pidafacil.task.ExecutionMethod;
import com.pidafacil.pidafacil.task.VoidActivityTask;
import com.pidafacil.pidafacil.util.Resource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by victor on 05-06-15.
 */
public class WebServiceHelper {

    public static VoidActivityTask restaurantsQuery(ExecutionMethod method, int opeCode){
        VoidActivityTask task = new VoidActivityTask("/restaurant/promos", method, opeCode) {
            @Override
            public void execute(StringBuilder result) {
                try {
                    org.json.JSONObject d = new org.json.JSONObject(result.toString());
                    if(d.getString("status").equals("true")){
                        this.resultList = new RestaurantBean().parseArray(d);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        return task;
    }

    public static VoidActivityTask getDetailQuery(ExecutionMethod method, int opeCode) {
        return  new VoidActivityTask("/product/get", method, opeCode) {
            @Override
            public void execute(StringBuilder result) {
                this.resultList = new ArrayList();
                ProductBean bean = null;
                try {
                    org.json.JSONObject o = new JSONObject(result.toString());
                    if (o.getString("status").equals("true")) {
                        bean = (new ProductBean().parseObject(o));
                    }
                } catch (JSONException e) {
                    Log.d("productG:PARSE-ERR", e.getMessage());
                }

                if (bean != null)
                    this.resultList.add(bean);
            }
        };
    }

    public static VoidActivityTask getPromoQuery(ExecutionMethod method, int opeCode){
        return new VoidActivityTask("/product/promos", method, opeCode) {
            @Override
            public void execute(StringBuilder result) {
                try {
                    JSONObject data = new JSONObject(result.toString());
                    if(data.getString("status").equals("true")){
                        resultList = new ProductBean()
                                .parseArray(new JSONObject(result.toString()));
                    }
                    if( resultList != null)
                        for(int i = 0 ; i < resultList.size() ; i++ ){
                            ProductBean bean = (ProductBean) resultList.get(i);
                            if(!bean.getImageUri().isEmpty()) {
                                bean.setImg(Resource.getImageBytes(bean.getImageUri()));
                                resultList.set(i,bean);
                            }
                        }
                } catch (JSONException e) {
                    Log.d("promos:ERR-PARSE", e.getMessage());
                }
            }
        };
    }

    public static VoidActivityTask getProductQuery(ExecutionMethod method, int opeCode){
        return new VoidActivityTask("/restaurant/products", method, opeCode) {
            @Override
            public void execute(StringBuilder result) {
                try {
                    JSONObject data = new JSONObject(result.toString());
                    if(data.getString("status").equals("true")){
                        resultList = new ProductBean()
                                .parseArray(new JSONObject(result.toString()));
                    }
                } catch (JSONException e) {
                    Log.d("products:ERR-PARSE", e.getMessage());
                }
            }
        };
    }

    public static VoidActivityTask orderQuery(ExecutionMethod method, int opeCode){
        return new VoidActivityTask("/user/orders", method, opeCode) {
            @Override
            public void execute(StringBuilder result) {
                try{
                    org.json.JSONObject o = new org.json.JSONObject(result.toString());
                    String status = o.getString("status");
                    if(status.equals("true")){
                        JSONArray arr = o.getJSONArray("data");
                        for(int i = 0 ; i < arr.length() ; i++){
                            org.json.JSONObject json = arr.getJSONObject(i);
                            OrderBean order = new OrderBean(json.getInt("order_id"),json.getString("name"),json.getString("order_total"),json.getString("created_at"));
                            order.setStatus(json.getJSONObject("status_logs").getString("order_status"));
                            order.setOrderCode(json.getString("order_cod"));
                            this.addResult(order);
                        }
                    }
                }catch (JSONException e){
                    Log.d("JSON-ERR",e.getMessage());
                }
            }
        };
    }

    public static VoidActivityTask emailCheck(ExecutionMethod method, int opeCode) {
        return new VoidActivityTask("/email-check", method, opeCode) {
            @Override
            public void execute(StringBuilder result) {
                try {
                    JSONObject object = new JSONObject(result.toString());
                    resultList = new ArrayList();
                    resultList.add(object.getString("status"));
                    resultList.add(object.getString("data"));
                } catch (JSONException e) {
                    Log.d("WS-ERR",e.getMessage());
                }
            }
        };
    }

    public static VoidActivityTask addressRestaurant(ExecutionMethod method, int opeCode) {
        return
                new VoidActivityTask("/restaurant/address", null, method, opeCode ) {
                    @Override
                    public void execute(StringBuilder result) {
                        try {
                            org.json.JSONObject data = new org.json.JSONObject(result.toString());
                            JSONArray arr = data.getJSONArray("data");
                            if(arr.length()>0){
                                this.resultList = new ArrayList();
                                for(int i = 0 ; i < arr.length(); i++){
                                    AddressBean bean = new AddressBean();
                                    bean.setId(arr.getJSONObject(i).getInt("restaurant_id"));
                                    bean.setName(arr.getJSONObject(i).getString("address"));
                                    this.resultList.add(bean);
                                }
                            }
                        } catch (JSONException e) {
                            Log.d("WS-ERR",e.getMessage());
                        }
                    }
                };
    }

    public static VoidActivityTask userAddress(ExecutionMethod method, int opeCode) {
        return new VoidActivityTask("/user/address", null, method, opeCode) {
            @Override
            public void execute(StringBuilder result) {
                try {
                    org.json.JSONObject data = new org.json.JSONObject(result.toString());
                    JSONArray arr = data.getJSONArray("data");
                    if (arr.length() > 0) {
                        this.resultList = new ArrayList();
                        for (int i = 0; i < arr.length(); i++) {
                            AddressBean bean = new AddressBean();
                            bean.setId(arr.getJSONObject(i).getInt("address_id"));
                            bean.setName(arr.getJSONObject(i).getString("address_name"));
                            bean.setIdZone(Integer.valueOf(arr.getJSONObject(i).getString("zone_id")));
                            bean.setTime(arr.getJSONObject(i).getString("time"));
                            this.resultList.add(bean);
                        }
                    }
                } catch (JSONException e) {
                    Log.d("USER-ADDRES", e.getMessage());
                }
            }
        };
    }


}
