package com.pidafacil.pidafacil.helper;

import android.util.Log;

import com.pidafacil.pidafacil.model.Ingredient;
import com.pidafacil.pidafacil.model.Option;
import com.pidafacil.pidafacil.model.Product;
import com.pidafacil.pidafacil.model.RestaurantRequest;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.exceptions.RealmException;

/**
 * Created by victor on 04-17-15.
 */
public class RealmHelper {

    public boolean clearRequest(Realm r, List<RestaurantRequest> l){
        try{
            if(l!=null)
                if(l.size()>0){
                    r.beginTransaction();
                    for(int i=0; i<l.size(); i++){
                        RestaurantRequest req = l.get(i);
                        RealmList<Product> p = req.getProducts();

                        for(int x=0;x<p.size();x++){
                            RealmList<Ingredient> in = p.get(x).getIngredients();
                            for(int y=0; y<in.size();y++)
                                in.get(y).removeFromRealm();

                            RealmList<Option> op = p.get(x).getOptions();
                            for(int y=0; y<op.size();y++)
                                op.get(y).removeFromRealm();
                            p.get(x).removeFromRealm();
                        }
                        req.removeFromRealm();
                    }
                    r.commitTransaction();
                    return true;
                }
            return false;
        }catch (RealmException re){
            r.cancelTransaction();
            Log.d("Realm-Excep",re.getMessage());
            return false;
        }
    }

}
