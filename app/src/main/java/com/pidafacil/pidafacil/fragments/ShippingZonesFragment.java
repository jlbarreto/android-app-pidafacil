package com.pidafacil.pidafacil.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pidafacil.pidafacil.R;
import com.pidafacil.pidafacil.beans.GeneralOptionsBean;
import com.pidafacil.pidafacil.beans.StateBean;
import com.pidafacil.pidafacil.beans.ZoneShippingBean;
import com.pidafacil.pidafacil.helper.JsonParser;
import com.pidafacil.pidafacil.singleton.UD;
import com.pidafacil.pidafacil.task.ExecutionMethod;
import com.pidafacil.pidafacil.task.VoidActivityTask;
import com.pidafacil.pidafacil.task.VoidTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Ramon Zuniga on 9/5/15.
 */
public class ShippingZonesFragment extends Fragment implements ExecutionMethod {

    protected TextView nombre_zona ;
    protected TextView  costo_zona;
    protected TextView  tipo_zona;
    public static List ShippingZones = new ArrayList();
    public static List addressBeans = new ArrayList();
    private String id;
    private StringBuilder response;
    public List beans;

    public List resultList = null;
    private UD U = UD.getInstance();
    private int defaultZone = 0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_shipping_zones, container, false);

        View view = inflater.inflate(R.layout.fragment_shipping_zones, container, false);
        this.nombre_zona = (TextView) view.findViewById(R.id.txt_zone_1);
        this.id = (String) UD.getInstance().get(UD.ZONE_SHIPPING_);

        VoidActivityTask task = new VoidActivityTask("/addresses/zonesShipping", this, 1) {
            @Override
            public void execute(StringBuilder result) {
                Log.d("INFO", "ZONAS COSTO ENVÍO " + result);
                this.resultList = new ArrayList();
                try {
                    /*ZoneShippingBean b = new ZoneShippingBean()
                            .parseObject(new JSONObject(result.toString()));
                    this.resultList.add(b);/
                    JsonParser parser = new JsonParser();
                    parser.setResponse(result);
                    beans = parser.parseZones();
                    U.put("cache_zone", beans);
                    parser.setResponse(result);*/
                    org.json.JSONObject o = new org.json.JSONObject(result.toString());
                    String status = o.getString("status");
                    if(status.equals("true")){
                        this.resultList = new ArrayList();
                        JSONArray arr = o.getJSONArray("data");
                        for(int i = 0 ; i < arr.length() ; i++){
                            /*org.json.JSONObject json = arr.getJSONObject(i);
                            ZoneShippingBean zone = new ZoneShippingBean(json.getString("Nombre"),json.getString("Costo"),json.getString("zona"));
                            zone.setNombre_zona(json.getString("Nombre"));
                            zone.setCosto_zona(json.getString("Costo"));
                            zone.setTipo_zona(json.getString("zona"));
                            this.addResult(zone);*/
                            List addr = ShippingZonesFragment.ShippingZones;
                            ZoneShippingBean bean = new ZoneShippingBean();
                            bean.setNombre_zona(arr.getJSONObject(i).getString("Nombre"));
                            bean.setCosto_zona(arr.getJSONObject(i).getString("Costo"));
                            bean.setTipo_zona(arr.getJSONObject(i).getString("zona"));
                            bean.setTiempo_entrega(arr.getJSONObject(i).getString("time"));
                            this.resultList.add(bean);


                        }
                    }




                } catch (JSONException e) { }
            }
        };
        //task.addParam("com_zones", id);
        task.execute();
        UD.VIEW_ = 4;
        return view;
    }



    @Override
    public void executeResult(List list, int operationCode) {
        if(operationCode == 1){
            if(list.size()>0){
                ShippingZones =list;
                Log.d("INFO", "ZONAS Shippgin zones " + ShippingZones.toString());
                //ZoneShippingBean bean = (ZoneShippingBean) beans.get(0);
                //String idStr = String.valueOf(((ZoneShippingBean) bean.getNombre_zona());
                String nombreZona ="";
                String costoZona ="";
                String tiempo_entrega ="";
                String ZonasEnvio = "";
                String aux ="";

                List addr = ShippingZonesFragment.ShippingZones;
                //Obteniendo los tipos de zonas
                for(int i=0; i<addr.size(); i++){
                    aux = aux + String.valueOf(((ZoneShippingBean) addr.get(i)).getTipo_zona());
                }
                String string = aux;

                char[] chars = string.toCharArray();
                Set<Character> charSet = new LinkedHashSet<Character>();
                for (char c : chars) {
                    charSet.add(c);
                }

                StringBuilder sb = new StringBuilder();
                for (Character character : charSet) {
                    sb.append(character);
                }
                System.out.println(sb.toString());



                for(int i=0; i<addr.size(); i++){
                   // aux = String.valueOf(((ZoneShippingBean) addr.get(i)).getTipo_zona());

                    nombreZona = String.valueOf(((ZoneShippingBean) addr.get(i)).getNombre_zona())+ " ";
                    costoZona = String.valueOf(((ZoneShippingBean) addr.get(i)).getCosto_zona())+ "\n";
                    tiempo_entrega = String.valueOf(((ZoneShippingBean) addr.get(i)).getTiempo_entrega())+ " ";
                    //ZoneShippingBean bean = (ZoneShippingBean) list.iterator().next();

                    ZonasEnvio = ZonasEnvio + nombreZona + "$" + costoZona + "" + tiempo_entrega +"\n\n";


                }
                nombre_zona.setText(ZonasEnvio);
                Log.d("INFO", "idStr: " + ZonasEnvio);


            }
        }
    }






}
