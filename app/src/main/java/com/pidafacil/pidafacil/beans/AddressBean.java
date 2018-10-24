package com.pidafacil.pidafacil.beans;

import com.pidafacil.pidafacil.util.ParseJsonArray;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by victor on 03-23-15.
 */
public class AddressBean implements ParseJsonArray {
    private int id;
    private String name;
    private String address1;
    private String address2;
    private String city;
    private String reference;
    private Integer idZone;
    private String time;

    public AddressBean() {

    }

    public AddressBean(int id, String name, String address1, String address2, String city, String reference, String time) {
        this.id = id;
        this.name = name;
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.reference = reference;
        this.time = time;
    }

    public AddressBean(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    @Override
    public List<AddressBean> parseArray(JSONObject jsonObject) {
        List<AddressBean> beans = new ArrayList<AddressBean>();
        try {
            JSONArray jsonArr = jsonObject.getJSONArray("data");

            for(int i = 0; i < jsonArr.length(); i++){

                JSONObject o = (JSONObject) jsonArr.get(i);
                beans.add(new AddressBean(
                        o.getInt("address_id"),
                        o.getString("address_name"),
                        o.getString("address_1"),
                        o.getString("address_2"),
                        o.getString("city"),
                        o.getString("reference"),
                        o.getString("time")));
            }
        } catch (JSONException e) {
            System.out.println("JSON-ERROR:".concat(e.getMessage()));
        }

        return beans;
    }

    public Integer getIdZone() {
        return idZone;
    }

    public void setIdZone(Integer idZone) {
        this.idZone = idZone;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

