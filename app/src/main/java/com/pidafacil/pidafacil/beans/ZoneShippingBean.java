package com.pidafacil.pidafacil.beans;

/**
 * Created by developer on 2/9/16.
 */

import com.pidafacil.pidafacil.util.ParseJsonArray;
import com.pidafacil.pidafacil.util.ParseJsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by developer on 2/2/16.
 */


public class ZoneShippingBean implements ParseJsonArray {
    private String nombre_zona ;
    private String costo_zona;
    private String tipo_zona;
    private String tiempo_entrega;


    public ZoneShippingBean(String Vnombre_zona , String Vcosto_zona, String Vtipo_zona, String Vtiempo_entrega) {
        this.nombre_zona =Vnombre_zona;
        this.costo_zona=Vcosto_zona;
        this.tipo_zona = Vtipo_zona;
        this.tiempo_entrega = Vtiempo_entrega;
    }

    public ZoneShippingBean(){}

    public String getCosto_zona() {
        return costo_zona;
    }

    public void setCosto_zona(String costo_zona) {
        this.costo_zona = costo_zona;
    }

    public String getNombre_zona() {
        return nombre_zona;
    }

    public void setNombre_zona(String nombre_zona) {
        this.nombre_zona = nombre_zona;
    }

    public String getTipo_zona() {
        return tipo_zona;
    }

    public void setTipo_zona(String tipo_zona) {
        this.tipo_zona = tipo_zona;
    }

    public String getTiempo_entrega() {
        return tiempo_entrega;
    }

    public void setTiempo_entrega(String tiempo_entrega) {
        this.tiempo_entrega = tiempo_entrega;
    }

    public ZoneShippingBean parseObject(JSONObject jsonObject) {
        // TODO Auto-generated method stub
        ZoneShippingBean bean = new ZoneShippingBean();
        try {
            jsonObject = jsonObject.getJSONObject("data");
            bean.setNombre_zona(jsonObject.getString("Nombre"));
            bean.setCosto_zona(jsonObject.getString("Costo"));
            bean.setTipo_zona(jsonObject.getString("zona"));


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            System.out.println("Error al leer las ZONAS: "+e.getMessage());
        }
        return bean;
    }


    @Override
    public List<ZoneShippingBean> parseArray(JSONObject jsonObject) {
        List<ZoneShippingBean> beans = new ArrayList<ZoneShippingBean>();
        try {
            JSONArray jsonArr = jsonObject.getJSONArray("data");

            for(int i = 0; i < jsonArr.length(); i++){

                JSONObject o = (JSONObject) jsonArr.get(i);
                beans.add(new ZoneShippingBean(
                        o.getString("Nombre"),
                        o.getString("Costo"),
                        o.getString("zona"),
                        o.getString("time")));
            }
        } catch (JSONException e) {
            System.out.println("JSON-ERROR:".concat(e.getMessage()));
        }

        return beans;
    }





}
