package com.pidafacil.pidafacil.task;

import android.os.AsyncTask;

import com.pidafacil.pidafacil.beans.SimpleParam;
import com.pidafacil.pidafacil.rest.Restclient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by victor on 04-13-15.
 * Realiza procesos que no devuelven valor, hay que programar
 * el metodo execute de la interfaz dentro de la clase. Tambien
 * se puede enviar una actividad que realize la ejecucion final.
 */
public class VoidTask extends AsyncTask<String, Void, Void> {

    VoidAsyncMethod voidAsyncMethod;
    List<SimpleParam> params;
    String controller;
    StringBuilder response;
    PostExecute postExecute;

    public void setVoidAsyncMethod(VoidAsyncMethod voidAsyncMethod) {
        this.voidAsyncMethod = voidAsyncMethod;
    }

    public void setPostExecute(PostExecute postExecute) {
        this.postExecute = postExecute;
    }

    /**
     * Constructor para consultas REST desde AsyncTask
     * @param controller path del controlador /controller
     * @param voidAsyncMethod Metodo final que ejecutara el AsyncTask
     * @param params Lista de parametros tipo llave valor
     */
    public VoidTask(String controller, VoidAsyncMethod voidAsyncMethod, List<SimpleParam> params) {
        if(params == null)
            params = new ArrayList<SimpleParam>();

        this.voidAsyncMethod = voidAsyncMethod;
        this.params = params;
        this.controller = controller;
    }

    public VoidTask(String controller, PostExecute postExecute) {
        if(params == null)
            params = new ArrayList<SimpleParam>();

        this.controller = controller;
        this.postExecute = postExecute;
    }

    public VoidTask(String controller) {
        this.controller = controller;
        if(params == null)
            params = new ArrayList<SimpleParam>();
    }

    @Override
    protected Void doInBackground(String... params) {

        try{
            Restclient rest = new Restclient(controller);

            for(SimpleParam param : this.params)
                rest.addParam(param.name, param.value);

            rest.prepareParams();
            response = rest.execute();

            if(voidAsyncMethod != null)
                voidAsyncMethod.execute(response);

        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public interface VoidAsyncMethod {
        public void execute(StringBuilder result);
    }

    public interface PostExecute{
        public void execute(StringBuilder response);
    }

    public void clearParams() {
        params.clear();
    }

    public boolean addParam(String name, String value) {
        return params.add(new SimpleParam(name, value));
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(postExecute != null)
            postExecute.execute(this.response);
    }

}

