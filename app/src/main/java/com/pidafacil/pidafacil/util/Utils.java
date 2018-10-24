package com.pidafacil.pidafacil.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Switch;

import com.appboy.Appboy;
import com.appboy.enums.Month;
import com.appboy.models.outgoing.AppboyProperties;
import com.pidafacil.pidafacil.R;
import com.pidafacil.pidafacil.helper.UIHelper;
import com.pidafacil.pidafacil.model.Login;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by victor on 08-25-15.
 */
public class Utils {


    private Switch mySwitch;


    public static void saveUserInformation(Appboy appboyInstance, String name,
                                           String lastName, String email, String phone, Object[] dateArgs){
        if(name!=null && !name.equals(""))
            appboyInstance.getCurrentUser().setFirstName(name);
        if(lastName!=null && !lastName.equals(""))
            appboyInstance.getCurrentUser().setLastName(lastName);
        if(email!=null && !email.equals(""))
            appboyInstance.getCurrentUser().setEmail(email);
        if(phone!=null && !phone.equals(""))
            appboyInstance.getCurrentUser().setPhoneNumber(phone);
        if(dateArgs != null && dateArgs.length == 3)
            appboyInstance.getCurrentUser().setDateOfBirth(Integer.parseInt(dateArgs[0].toString()),
                    ((Month) dateArgs[1]), Integer.parseInt(dateArgs[2].toString()));
    }

    public static void appboyEvent(Appboy appboyInstance, String eventName, Object[] args){
        AppboyProperties properties = new AppboyProperties();
        if(eventName.equals("Search")){
            properties.addProperty("search_criteria", args[0].toString());
        }else if(eventName.equals("Explore")){
            properties.addProperty("tag_type", args[0].toString());
        }
        appboyInstance.logCustomEvent(eventName, properties);
    }

    public static void appboyEvent(Appboy appboyInstance, String eventName){
        AppboyProperties properties = new AppboyProperties();
        appboyInstance.logCustomEvent(eventName, properties);
    }

    public static void shareContent(Context ctx, String message, String title, String url, ImageView image) {
        Uri uri = getLocalBitmapUri(image);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, title + " - " + message);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message + " " + url);
        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sendIntent.setType("image/*");
        ctx.startActivity(Intent.createChooser(sendIntent, "Compartir usando"));
    }

    public static Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable){
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file =  new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }
    public static Boolean checkGPSNetworkServices(final Context context) {
        try {
            boolean gpsEnabled;
            boolean networkEnabled;



            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!gpsEnabled && !networkEnabled) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context,AlertDialog.THEME_DEVICE_DEFAULT_DARK);
                dialog.setTitle(context.getResources().getString(R.string.no_gps_title));
                dialog.setMessage(context.getResources().getString(R.string.gps_network_not_enabled));
                dialog.setPositiveButton(context.getResources().getString(R.string.gps_network_settings), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(myIntent);
                    }
                });
                dialog.setNegativeButton(context.getString(R.string.ignore), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
                dialog.show();

              //  appboyEvent(instance, context.getString(R.string.appboy_no_internet));

                checkInternetConnection(context);

                return false;

            } else {
                return checkInternetConnection(context);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }// fin checkGPSNetworkServices

    public static boolean checkInternetConnection(final Context context){
        try {
            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo == null) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setTitle(context.getResources().getString(R.string.no_internet_title));
                dialog.setMessage(context.getResources().getString(R.string.no_internet_available));
                dialog.setPositiveButton(context.getResources().getString(R.string.gps_network_settings), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_SETTINGS);
                        context.startActivity(myIntent);
                    }
                });
                dialog.setNegativeButton(context.getString(R.string.ignore), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
                dialog.show();

               // appboyEvent(instance, context.getString(R.string.appboy_no_internet));

                return false;
            }

            return netInfo.isConnected();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
