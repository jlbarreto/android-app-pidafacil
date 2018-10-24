package com.pidafacil.pidafacil.helper;

import android.util.Log;

import com.pidafacil.pidafacil.beans.MunicipalityBean;
import com.pidafacil.pidafacil.beans.StateBean;
import com.pidafacil.pidafacil.beans.TypeServiceBean;
import com.pidafacil.pidafacil.beans.ZoneBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by victor on 07-30-15.
 */
public class JsonParser {

    private StringBuilder response;

    public StringBuilder getResponse() {
        return response;
    }

    public void setResponse(StringBuilder response) {
        this.response = response;
    }

    public List<StateBean> parseStates() throws JSONException {
        JSONObject resp = new JSONObject(response.toString());
        if(resp.getString("status").equals("true")){
            org.json.JSONArray data = resp.getJSONArray("data");
            List<StateBean> beans = new ArrayList<>();
            for(int i=0;i<data.length();i++){
                org.json.JSONObject addr = data.getJSONObject(i);
                org.json.JSONArray muns = addr.getJSONArray("municipalities");
                StateBean state = new StateBean(addr.getInt("state_id"),
                        addr.getString("state"), addr.getInt("country_id"));
                for(int j=0;j<muns.length();j++){
                    org.json.JSONObject mun = muns.getJSONObject(j);
                    org.json.JSONArray zones = mun.getJSONArray("zones");
                    MunicipalityBean municipality = new MunicipalityBean(
                            mun.getInt("municipality_id"), mun.getString("municipality"));
                    for(int k=0;k<zones.length();k++){
                        org.json.JSONObject zone = zones.getJSONObject(k);
                        if(zone!=null){
                            ZoneBean zoneBean = new ZoneBean(zone.getInt("zone_id"),
                                    zone.getString("zone"), null);
                            municipality.add(zoneBean);
                        }
                    }
                    state.add(municipality);
                }
                beans.add(state);
            }
            return beans;
        }else{
            return null;
        }
    }

    public List<TypeServiceBean> parseServiceTypes() throws JSONException {
        Log.d("PARSIN-WSRESULT", " Services " + response.toString());
        org.json.JSONObject data = new org.json.JSONObject(response.toString());
        List<TypeServiceBean> beans = null;
        if (data.getString("status").equals("true")) {
            beans = new ArrayList<>();
            JSONArray datarr = data.getJSONArray("data");
            for(int i=0;i<datarr.length();i++){
                beans.add(new TypeServiceBean(datarr.getJSONObject(i).getInt("service_type_id"),
                        datarr.getJSONObject(i).getString("service_type")));
            }
            return beans;
        }else{
            return beans;
        }
    }

    public List<StateBean> parseZones() throws JSONException {
        Log.d("PARSIN-WSRESULT", " Services " + response.toString());
        org.json.JSONObject data = new org.json.JSONObject(response.toString());
        List<StateBean> beans = null;
        if (data.getString("status").equals("true")) {
            beans = new ArrayList<>();
            JSONArray datarr = data.getJSONArray("data");
            for(int i=0;i<datarr.length();i++){
                beans.add(new StateBean(datarr.getJSONObject(i).getInt("zone_id"),
                                        datarr.getJSONObject(i).getString("zone"),
                                        0));
            }
            return beans;
        }else{
            return beans;
        }
    }

}