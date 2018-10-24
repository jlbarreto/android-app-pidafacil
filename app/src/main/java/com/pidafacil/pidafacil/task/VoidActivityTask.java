package com.pidafacil.pidafacil.task;

import android.os.AsyncTask;

import com.pidafacil.pidafacil.beans.SimpleParam;
import com.pidafacil.pidafacil.rest.Restclient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by victor on 04-15-15.
 */
public abstract class VoidActivityTask extends AsyncTask<String, Void, Void> {
    private List<SimpleParam> params;
    private String controller;
    public ExecutionMethod activity;
    public List resultList = null;
    int operationCode = -1;

    public VoidActivityTask(String controller, ExecutionMethod activity, int operationCode) {
        this.controller = controller;
        this.activity = activity;
        this.operationCode = operationCode;
        this.params = new ArrayList<>();
    }

    public VoidActivityTask(String controller, List<SimpleParam> params, ExecutionMethod activity, int operationCode) {
        if(params == null)
            params = new ArrayList<SimpleParam>();

        this.activity = activity;
        this.params = params;
        this.controller = controller;
        this.operationCode = operationCode;
    }

    @Override
    protected Void doInBackground(String... params) {

        try{
            Restclient rest = new Restclient(controller);

            for(SimpleParam param : this.params)
                rest.addParam(param.name, param.value);

            rest.prepareParams();
            StringBuilder buff = rest.execute();
            this.execute(buff);

        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public abstract void execute(StringBuilder result);

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        activity.executeResult(resultList, operationCode);
    }

    public void clearParams() {
        params.clear();
    }

    public boolean addParam(String name, String value) {
        return params.add(new SimpleParam(name, value));
    }

    public void addResult(Object o){
        if(resultList == null)
            resultList = new ArrayList();
        resultList.add(o);
    }
}
