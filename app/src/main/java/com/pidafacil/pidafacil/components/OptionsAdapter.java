package com.pidafacil.pidafacil.components;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.pidafacil.pidafacil.R;
import com.pidafacil.pidafacil.singleton.UD;
import com.pidafacil.pidafacil.beans.ConditionBean;
import com.pidafacil.pidafacil.beans.OptionBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by victor on 03-03-15.
 * Adaptador de Opciones.
 */
public class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.OptionHolder>{


    public List<ConditionBean> beans;

    public OptionsAdapter(List<ConditionBean> beans){
        this.beans = beans;
    }

    @Override
    public int getItemCount() {
        return beans.size();
    }

    @Override
    public void onBindViewHolder(OptionHolder holder, int i) {
        ConditionBean bean = beans.get(i);
        List<String> list = new ArrayList<String>();
        list.add("Seleccionar");
        holder.textView.setText(bean.getDescription() + ":");
        List<OptionBean> l = bean.getOptions();
        for (OptionBean b : l ){
            list.add(b.getDescription());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(UD.getInstance().getContext(),R.layout.spinner_custom_item,list);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_custom_item);
        holder.spinner.setAdapter(adapter);
    }

    @Override
    public OptionHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.recycler_item_product_option, viewGroup, false);
        return new OptionHolder(itemView);
    }

    public class OptionHolder extends RecyclerView.ViewHolder{

        protected TextView textView;
        protected Spinner spinner;

        public OptionHolder(View view){
            super(view);
            this.textView = (TextView) view.findViewById(R.id.option_name);
            this.spinner = (Spinner) view.findViewById(R.id.options_list);
        }

    }

}
