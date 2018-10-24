package com.pidafacil.pidafacil.components;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appboy.Appboy;
import com.pidafacil.pidafacil.beans.SectionBean;
import com.pidafacil.pidafacil.R;
import com.pidafacil.pidafacil.fragments.RestaurantInfoFragment;
import com.pidafacil.pidafacil.singleton.UD;

import java.util.List;

/**
 * Created by victor on 02-13-15.
 */
public class RestaurantSectionAdapter extends RecyclerView.Adapter<RestaurantSectionAdapter.MenuHolder>{
    public List<SectionBean> beans;
    public RestaurantSectionAdapter(List<SectionBean> beans){
        this.beans = beans;
    }
    private String seccionName="";
    private  String idSeccion= "";

    @Override
    public int getItemCount() {
        return beans.size();
    }

    @Override
    public void onBindViewHolder(MenuHolder holder, int i) {
        SectionBean bean = beans.get(i);
        holder.textView.setText(bean.getSection());
        holder.textViewId.setText(String.valueOf(bean.getSessionId()));
    }

    @Override
    public MenuHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.recycler_item_section_row, viewGroup, false);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout view = (LinearLayout) v;
                TextView textViewId = (TextView) view.getChildAt(0);
                TextView name = (TextView) view.findViewById(R.id.sec_option);
                String value = textViewId.getText().toString();
                UD.getInstance().put(UD.SECTION_, value);
                UD.getInstance().put(UD.SECTION_SELECTED_NAME_, name.getText());
                seccionName= name.getText().toString();
                idSeccion=value;
                Log.d("section", "section" +  name.getText() + "" +value);
                sendAppboyCustomAttributesRevenue();
                RestaurantInfoFragment fragment = (RestaurantInfoFragment) UD.getInstance().get("restaurant_info_fragment");
                fragment.goToProducts();
            }
        });

        return new MenuHolder(itemView);
    }

    public class MenuHolder extends RecyclerView.ViewHolder{

        protected TextView textView;
        protected TextView textViewId;
        protected ImageView logo;

        public MenuHolder(View view){
            super(view);
            this.textView = (TextView) view.findViewById(R.id.sec_option);
            this.textViewId = (TextView) view.findViewById(R.id.sec_id);
        }

    }
    private void sendAppboyCustomAttributesRevenue(){
        Appboy.getInstance(UD.getInstance().getContext()).getCurrentUser().addToCustomAttributeArray(
                "Seccion_Visitada:", "ID:" + idSeccion + "\nNombre: " +seccionName
        );
    }
}
