package com.pidafacil.pidafacil.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by victor on 05-18-15.
 */
public class NetworkValidator {

    public static boolean hasConnection(Activity activity){
        ConnectivityManager cm =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if(netInfo == null)
            return false;
        return netInfo.isConnected();
    }

}
