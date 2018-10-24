package com.pidafacil.pidafacil.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Html;

import com.pidafacil.pidafacil.R;

/**
 * Created by victor on 05-07-15.
 */
public class UIHelper {

    public static AlertDialog.Builder alert(Context ctx, String title, String message ){
        final AlertDialog.Builder alertDialogBuilder;
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        // Determinar la version de Android para mostrar un estilo de dialogo adecuado
        if (currentApiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP){
            alertDialogBuilder =
                    new AlertDialog.Builder(new ContextThemeWrapper(ctx, android.R.style.Theme_Material_Dialog));
        } else{
            alertDialogBuilder =
                    new AlertDialog.Builder(new ContextThemeWrapper(ctx, android.R.style.Theme_Holo_Dialog));
        }

        if(title!=null)
            alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        return alertDialogBuilder;
    }

    public static AlertDialog.Builder aceptAlert(Context ctx, String title, String message ){
        final AlertDialog.Builder alertDialogBuilder;
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        // Determinar la version de Android para mostrar un estilo de dialogo adecuado
        if (currentApiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP){
            alertDialogBuilder =
                    new AlertDialog.Builder(new ContextThemeWrapper(ctx, android.R.style.Theme_Material_Dialog));
        } else{
            alertDialogBuilder =
                    new AlertDialog.Builder(new ContextThemeWrapper(ctx, android.R.style.Theme_Holo_Dialog));
        }
        if(title!=null)
            alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return alertDialogBuilder;
    }
}


