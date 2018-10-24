package com.pidafacil.pidafacil.beans;

import org.json.JSONException;
import org.json.JSONObject;

import com.pidafacil.pidafacil.singleton.UD;
import com.pidafacil.pidafacil.util.ParseJsonObject;

/**
 * Created by developer on 2/2/16.
 */


public class GeneralOptionsBean implements ParseJsonObject {
    private String num_atencion_cliente ;
    private String email_atencion_cliente;
    private String direccion_pidafacil;
    private String encriptar_amex;


    public GeneralOptionsBean() {
    }

    public String getNum_atencion_cliente() {
        return num_atencion_cliente;
    }

    public void setNum_atencion_cliente(String num_atencion_cliente) {
        this.num_atencion_cliente = num_atencion_cliente;
    }

    public String getEmail_atencion_cliente() {
        return email_atencion_cliente;
    }

    public void setEmail_atencion_cliente(String email_atencion_cliente) {
        this.email_atencion_cliente = email_atencion_cliente;
    }

    public String getDireccion_pidafacil() {
        return direccion_pidafacil;
    }

    public void setDireccion_pidafacil(String direccion_pidafacil) {
        this.direccion_pidafacil = direccion_pidafacil;
    }

    public String getEncriptar_amex() {
        return encriptar_amex;
    }

    public void setEncriptar_amex(String encriptar_amex) {
        this.encriptar_amex = encriptar_amex;
    }

    public GeneralOptionsBean parseObject(JSONObject jsonObject) {
        // TODO Auto-generated method stub
        GeneralOptionsBean bean = new GeneralOptionsBean();
        try {
            jsonObject = jsonObject.getJSONObject("data");
            bean.setNum_atencion_cliente(jsonObject.getString("num_atencion_cliente"));
            bean.setDireccion_pidafacil(jsonObject.getString("direccion_pidafacil"));
            bean.setEmail_atencion_cliente(jsonObject.getString("email_atencion_cliente"));
            bean.setEncriptar_amex(jsonObject.getString("encriptar_amex"));
            UD.getInstance().put("MEX", jsonObject.getString("encriptar_amex"));

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            System.out.println("Error al leer conf_general_options "+e.getMessage());
        }
        return bean;
    }


}
