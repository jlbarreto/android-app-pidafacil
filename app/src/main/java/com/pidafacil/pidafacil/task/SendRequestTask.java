package com.pidafacil.pidafacil.task;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.pidafacil.pidafacil.activities.CompleteRequestActivity;
import com.pidafacil.pidafacil.model.Ingredient;
import com.pidafacil.pidafacil.model.Option;
import com.pidafacil.pidafacil.model.Product;
import com.pidafacil.pidafacil.model.RestaurantRequest;
import com.pidafacil.pidafacil.rest.Restclient;
import com.pidafacil.pidafacil.singleton.UD;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by victor on 03-16-15.
 */
public class SendRequestTask extends AsyncTask<String, Void, StringBuilder> {

    private CompleteRequestActivity activity;
    private int opeCode;
    private boolean status = false;

    private List results;

    public SendRequestTask(CompleteRequestActivity activity, int opeCode) {
        this.activity = activity;
        this.opeCode = opeCode;
    }

    @Override
    protected StringBuilder doInBackground(String... params) {
        StringBuilder str = null;

        try{
            Restclient rest = new Restclient("/order/make");
            //Restclient rest = new Restclient("/order/create");
            str = rest.execute(params[0]);
            org.json.JSONObject data = new JSONObject(str.toString());
            if(data.getString("status").equals("true")){
                status = true;
                results = new ArrayList();
                results.add(true);

                Realm r = Realm.getInstance(UD.getInstance().getContext());
                RestaurantRequest request = r.allObjects(RestaurantRequest.class).first();
                r.beginTransaction();
                RealmList<Product> p = request.getProducts();
                for(int i = 0; i < p.size(); i++) {
                    RealmList<Ingredient> ingr = p.get(i).getIngredients();
                    for (int j = 0; j < ingr.size(); j++) {
                        ingr.get(j).removeFromRealm();
                    }

                    RealmList<Option> o = p.get(i).getOptions();
                    for(int j = 0; j < o.size(); j++){
                        o.get(j).removeFromRealm();
                    }

                    p.get(i).removeFromRealm();
                }

                request.removeFromRealm();
                r.commitTransaction();
            }else{
                results = new ArrayList();
                results.add(false);
                results.add(data.getString("data"));
            }

            Log.d("INFO: "," WS result - "+str.toString());
        }catch(Exception e){
            e.printStackTrace();
        }

        return str;
    }

    @Override
    protected void onPostExecute(StringBuilder stringBuilder) {
        activity.executeResult(results, 2);
    }
}

