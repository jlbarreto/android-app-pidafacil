package com.pidafacil.pidafacil.fragments.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appboy.Appboy;
import com.pidafacil.pidafacil.R;
import com.pidafacil.pidafacil.beans.RestaurantInfo;
import com.pidafacil.pidafacil.singleton.UD;
import com.pidafacil.pidafacil.task.ExecutionMethod;
import com.pidafacil.pidafacil.task.VoidActivityTask;
import com.pidafacil.pidafacil.util.Resource;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by victor on 06-05-15.
 */
public class RestaurantInformationFragmentTab extends Fragment implements ExecutionMethod{
    protected TextView textView;
    protected TextView textView2;
    protected ImageView imgResLogo;
    protected TextView horarios, descripcion, hubicacion;
    protected ImageView imgResLogo2;
    private String id;
    private String name;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurant_tab_info, container, false);
        this.textView = (TextView) view.findViewById(R.id.text_res_name);
        this.textView2 = (TextView) view.findViewById(R.id.text_res_slogan);
        this.imgResLogo = (ImageView) view.findViewById(R.id.img_res_logo__);
        this.imgResLogo2 = (ImageView) view.findViewById(R.id.img_res_logo2__);
        this.descripcion = (TextView) view.findViewById(R.id.text_res_description);
        this.horarios = (TextView) view.findViewById(R.id.text_res_horarios);
        this.hubicacion = (TextView) view.findViewById(R.id.text_res_hubicacion);
        this.id = (String) UD.getInstance().get(UD.RESTAURANT_);
        this.name = (String) UD.getInstance().get(UD.RESTAURANT_NAME_);
        UD.VIEW_ = 4;

        VoidActivityTask task = new VoidActivityTask("/restaurant/get", this, 1) {
            @Override
            public void execute(StringBuilder result) {
                Log.d("INFO","RESTAURANT DETAILS "+result);
                Log.d("INFO","UD "+UD.VIEW_);
                sendAppboyCustomAttributesRevenue();
                this.resultList = new ArrayList();
                try {
                    RestaurantInfo b = new RestaurantInfo()
                            .parseObject(new JSONObject(result.toString()));
                    this.resultList.add(b);
                } catch (JSONException e) { }
            }
        };
        task.addParam("restaurant_id", id);
        task.execute();

        return view;
    }

    @Override
    public void executeResult(List list, int operationCode) {
        if(operationCode == 1){
            if(list.size()>0){
                RestaurantInfo bean = (RestaurantInfo) list.iterator().next();
                textView.setText(bean.getName());
                if(name == null)
                    getActivity().setTitle(bean.getName());

                try{
                    Picasso.with(getActivity().getApplicationContext())
                            .load(Resource.RESOURCE_URI + bean.getImgUri()).into(imgResLogo);
                    Picasso.with(getActivity().getApplicationContext())
                            .load(Resource.RESOURCE_URI + bean.getImgUriLogo()).into(imgResLogo2);
                }catch (NullPointerException e1){ }

                descripcion.setText(bean.getDescription());

                if(bean.getHours().size()>0){
                    StringBuilder builder = new StringBuilder("Horarios: ").append("\n");
                    for(int x = 0; x < bean.getHours().size(); x++){
                        RestaurantInfo.Hours h = bean.getHours().get(x);
                        builder.append(dayParser(h.getDay()))
                                .append(" ").append(h.getOpening())
                                .append(" - ").append(h.getClosing())
                                .append(" ").append(h.getTypeService() == 2 ?
                                " Para llevar" : "Domicilio" ).append("\n");
                    }
                    horarios.setText(builder.toString());
                }

                if(!bean.getHubicacion().equals("null")){
                    StringBuilder str = new StringBuilder("");
                    try {
                        JSONArray arr = new JSONArray(bean.getHubicacion());
                        for(int x = 0; x < arr.length(); x++){
                            str.append(arr.getJSONObject(x).getString("address")).append("\n");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    hubicacion.setText(str.toString());
                }
            }
        }
    }

    String dayParser(int day){
        return (day == 1 ? "Domingo" : day == 2 ? "Lunes": day == 3 ? "Martes": day == 4 ? "Miercoles": day == 5 ? "Jueves": day == 6 ? "Viernes": "Sabado");
    }


    private void sendAppboyCustomAttributesRevenue(){
        Appboy.getInstance(UD.getInstance().getContext()).getCurrentUser().addToCustomAttributeArray(
                "Restaurante_Visitado:", "ID:" + id + "\nNombre: " +name
        );
    }

}
