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
import com.pidafacil.pidafacil.singleton.UD;
import com.pidafacil.pidafacil.task.ExecutionMethod;
import com.pidafacil.pidafacil.task.VoidActivityTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by developer on 11/10/15.
 */


public class WhatsAppFragment extends Fragment implements ExecutionMethod{



    protected TextView num_atencion_cliente;
    private String id;
    public String mex;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_whatsapp, container, false);
        this.num_atencion_cliente = (TextView) view.findViewById(R.id.txtNumWhatsapp);
        this.id = (String) UD.getInstance().get(UD.GENERAL_CONF_);
        VoidActivityTask task = new VoidActivityTask("/generalOption", this, 1) {
            @Override
            public void execute(StringBuilder result) {
                Log.d("INFO", "CONFIGURACIONES GENERALES PF " + result);
                this.resultList = new ArrayList();
                try {
                    GeneralOptionsBean b = new GeneralOptionsBean()
                            .parseObject(new JSONObject(result.toString()));
                    this.resultList.add(b);
                } catch (JSONException e) { }
            }
        };
        task.addParam("id_conf_general_options", id);
        task.execute();
        UD.VIEW_ = 4;
        return view;
    }



    @Override
    public void executeResult(List list, int operationCode) {
        if(operationCode == 1) {
            if (!list.isEmpty() && list != null && list.size()!=0 ) {
                if (list.size() > 0) {
                    GeneralOptionsBean bean = (GeneralOptionsBean) list.iterator().next();
                    num_atencion_cliente.setText(bean.getNum_atencion_cliente());
                    mex = UD.getInstance().get("MEX").toString();
                    if(!mex.isEmpty()) Log.d("MEX", ""+mex);
                }
            }
        }
    }

}
