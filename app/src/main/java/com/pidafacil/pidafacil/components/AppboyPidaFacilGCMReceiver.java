package com.pidafacil.pidafacil.components;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.appboy.Constants;
import com.appboy.support.AppboyLogger;

/**
 * Created by victor on 07-14-15.
 * Este es el receptor de notificaciones. Se pueden filtrar en onReceive()
 * y realizar las acciones segun los filtros del action.
 */
public class AppboyPidaFacilGCMReceiver extends BroadcastReceiver{
    private static final String TAG = "BRCRECEIVER";

    @Override
    public void onReceive(Context context, Intent intent) {
        AppboyLogger.i(TAG, String.format("Received broadcast message. Message: %s", intent.toString()));
        String action = intent.getAction();
        Log.d(TAG,action);
        Bundle extras = intent.getExtras().getBundle("extra");
        if(extras!=null){
            Log.d(TAG,"Params size " + extras.size());
//            Log.d(TAG,"Params toString " + extras.toString());
        }

        if(action.equals("com.pidafacil.pidafacil.intent.APPBOY_PUSH_RECEIVED")){
            Log.d(TAG,"NOTIFICATION RECEIVED");
        }else if(action.equals("com.pidafacil.pidafacil.intent.APPBOY_NOTIFICATION_OPENED")){
            Log.d(TAG,"NOTIFICATION OPENED");
            if (intent.getStringExtra(Constants.APPBOY_PUSH_CUSTOM_URI_KEY) != null) {
                Log.d(TAG, "DeepLink " + intent.getStringExtra(Constants.APPBOY_PUSH_DEEP_LINK_KEY).toString());
                Intent uriIntent = new Intent(Intent.ACTION_VIEW);
                uriIntent.setData(Uri.parse(intent.getStringExtra(Constants.APPBOY_PUSH_DEEP_LINK_KEY)));
                uriIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(uriIntent);
            }else{
                Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.pidafacil.pidafacil");
                context.startActivity(launchIntent);
            }
        }

    }
}