package com.pidafacil.pidafacil.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;

import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.appboy.Appboy;
import com.pidafacil.pidafacil.NavigationDrawer;
import com.pidafacil.pidafacil.R;
import com.pidafacil.pidafacil.beans.StateBean;
import com.pidafacil.pidafacil.helper.JsonParser;
import com.pidafacil.pidafacil.helper.UIHelper;
import com.pidafacil.pidafacil.model.Login;
import com.pidafacil.pidafacil.singleton.UD;
import com.pidafacil.pidafacil.task.VoidTask;
import com.pidafacil.pidafacil.util.GPSTracker;
import com.pidafacil.pidafacil.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.realm.Realm;

public class AddAddressActivity extends ActionBarActivity {
    private String name;
    private Intent resultIntent = new Intent();
    public static final int ADD_ADDRESS_REQ_CODE = 1;
    private List<StateBean> beans = null;
    private UD U = UD.getInstance();
    private Spinner zoneSpin;
    private int zone_id;
    private Switch mySwitch;
    private TextView txtCoordenadas;
    private RadioGroup coordenadasGroup;
    private RadioButton coordenadaRadio;
    private RadioButton coordenadaRadioNo;
    GPSTracker gps;
    private CountDownTimer timer;
    public static  double latitude = 1.0;
    public static  double longitude = 1.0;
    public static int gpsOK;

    private ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_address);

        txtCoordenadas = (TextView) findViewById(R.id.txt_coordenadas);
        coordenadasGroup = (RadioGroup) findViewById(R.id.rdgCoordenadasGroup);
        coordenadaRadio = (RadioButton) findViewById(R.id.rdbCoordenadasSi);
        coordenadaRadioNo = (RadioButton) findViewById(R.id.rdbCoordenadasNo);
        coordenadasGroup.check(R.id.rdbCoordenadasSi);
        findViewById(R.id.sending_add).setVisibility(View.GONE);
        coordenadasGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.rdbCoordenadasSi) {
                    // Si compartir coordenadas
                    gpsOK = 1;
                    if (checkGPSNetworkServices(AddAddressActivity.this)) {
                        gps = new GPSTracker(AddAddressActivity.this);
                        if (gps.canGetLocation()) {
                            //  txtCoordenadas.setVisibility(View.VISIBLE);

                            latitude = gps.getLatitude();
                            longitude = gps.getLongitude();
                            gpsOK = 1;
                            txtCoordenadas.setText("" + latitude + "," + longitude);
                            findViewById(R.id.add_address_2).setVisibility(View.GONE);
                            findViewById(R.id.sending_add).setVisibility(View.VISIBLE);
                            //   progress = ProgressDialog.show(AddAddressActivity.this, null, "Espere...", true);

                            timer = new CountDownTimer(5000, 1000) {
                                //300000, 20
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    latitude = gps.getLatitude();
                                    longitude = gps.getLongitude();
                                    Log.d("gps0", "timer"+latitude+","+longitude);
                                }

                                @Override
                                public void onFinish() {
                                    try{
                                        capturarUbicacion();
                                        Log.d("timer", "timer ");
                                    }catch(Exception e){
                                        Log.e("Error", "Error: " + e.toString());
                                    }
                                }
                            }.start();


                        } else {
                            // can't get location
                            // GPS or Network is not enabled
                            // Ask user to enable GPS/network in settings

                            gps.showSettingsAlert();
                        }
                    }

                } else if (checkedId == R.id.rdbCoordenadasNo) {
                    gpsOK = 0;
                    txtCoordenadas.setText("");
                    latitude = 0.0;
                    longitude = 0.0;

                }
            }
        });

        configure();

        setTitle("Agregar dirección");

        U.setCurrentActivity(this);

        if (U.get("cache_addrbeans") != null) {
            beans = (List<StateBean>) U.get("cache_addrbeans");
            configureZones();
        } else {
            loadAddresses();
        }
    }

    private void loadAddresses(){
        VoidTask task = new VoidTask("/zones/get");
        task.setPostExecute(new VoidTask.PostExecute() {
            @Override
            public void execute(StringBuilder response) {
                try {
                    JsonParser parser = new JsonParser();
                    parser.setResponse(response);
                    beans = parser.parseZones();
                    U.put("cache_addrbeans", beans);
                    configureZones();
                } catch (JSONException e) {
                    Log.d("zones:PARSE-ERR", e.getMessage());
                }
            }
        });
        task.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if(UD.TYPE_VIEW == 2){
                UD.TYPE_VIEW = 0;
                startActivity(new Intent(getApplicationContext(), AddressActivity.class));
            }else{
                NavUtils.navigateUpFromSameTask(this);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void configure(){
        zoneSpin = ((Spinner) findViewById(R.id.spin_zonas));

        findViewById(R.id.button_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v = ((Activity) UD.getInstance().getCurrentActivity()).findViewById(R.id.add_address_);
                name = ((TextView) v.findViewById(R.id.txt_name)).getText().toString();
                String dir1 = ((TextView) v.findViewById(R.id.txt_dir1)).getText().toString();
                String ref = ((TextView) v.findViewById(R.id.txt_ref)).getText().toString();
                String coordenadas = ((TextView) v.findViewById(R.id.txt_coordenadas)).getText().toString();
                RadioButton siCoordenadas = (RadioButton) v.findViewById(R.id.rdbCoordenadasSi);
                Realm r = Realm.getInstance(v.getContext());
                Login login = r.allObjects(Login.class).first();

                if(latitude == 1.0 || longitude==1.0){

                    Log.d("No capturadas", "" + latitude + "," + longitude);

                }
                Log.d("gps1", "" +gpsOK);
                if(siCoordenadas.isChecked()){
                    if (checkGPSNetworkServices(AddAddressActivity.this)) {
                        gps = new GPSTracker(AddAddressActivity.this);
                        if (gps.canGetLocation()) {
                            //  txtCoordenadas.setVisibility(View.VISIBLE);
                            latitude = gps.getLatitude();
                            longitude = gps.getLongitude();
                            gpsOK = 1;
                            if (latitude == 1.0 || longitude == 1.0) {
                                timer = new CountDownTimer(5000, 1000) {
                                    //300000, 20
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        latitude = gps.getLatitude();
                                        longitude = gps.getLongitude();
                                        Log.d("gps0", "timer"+latitude+","+longitude);


                                    }

                                    @Override
                                    public void onFinish() {
                                        try{
                                            capturarUbicacion();

                                            Log.d("timer", "Coordenadas_capturadas"+latitude+","+longitude);
                                        }catch(Exception e){
                                            Log.e("Error", "Error: " + e.toString());
                                        }
                                    }
                                }.start();


                                latitude = gps.getLatitude();
                                longitude = gps.getLongitude();
                            }
                            if (latitude == 1.0 || longitude == 1.0) {
                                latitude = gps.getLatitude();
                                longitude = gps.getLongitude();
                            }

                            txtCoordenadas.setText("" + latitude + "," + longitude);
                            Log.d("entro", "" + latitude + "," + longitude + "ok:" + gpsOK);
                            coordenadaRadio.setChecked(true);

                        } else {
                            // can't get location
                            // GPS or Network is not enabled
                            // Ask user to enable GPS/network in settings

                            gps.showSettingsAlert();
                        }
                    }
                }

                if (name.trim().isEmpty() || dir1.trim().isEmpty()) {
                    message("Debes agregar un nombre y dirección", "Faltan datos por completar");
                    return;
                }

                if(name.isEmpty()){
                    message("El nombre de la dirección es requerido", "Faltan datos por completar");
                    return;
                }
                if(dir1.length()<6){
                    errorMessage(getString(R.string.dir1_error));
                    return;
                }
                if(dir1.isEmpty()){
                    message("Direccion 1 es requerido", "Faltan datos por completar");
                    return;
                }
                Log.d("gps2",""+gpsOK+siCoordenadas.isChecked());

                if(siCoordenadas.isChecked() && (latitude ==1.0 ||longitude == 1.0 || latitude ==0.0 ||longitude == 0.0))
                {
                    errorMessage(getString(R.string.no_gps_title2));
                    return;
                }
                if(gpsOK == 1 && (latitude ==1.0 ||longitude == 1.0))
                {
                    errorMessage(getString(R.string.no_gps_title2));
                    return;
                }



                zone_id = zoneId();
                VoidTask task = new VoidTask("/user/address/createNew");
                task.addParam("user_id", String.valueOf(login.getId()));
                task.addParam("address_name", name);
                task.addParam("address_1", dir1);
                task.addParam("address_2", "");
                task.addParam("map_coordinates", ""+latitude+","+longitude );
                task.addParam("reference", ref);
                task.addParam("zone_id", String.valueOf(zone_id));
                task.addParam("Android_add","Android");/// para el manejo del tiempo en la address agragada
                task.setPostExecute(new VoidTask.PostExecute() {
                    @Override
                    public void execute(StringBuilder response) {
                        Log.d("ADD ADDRESS", response.toString());
                        try {
                            JSONObject object = new JSONObject(response.toString());
                            if (object.getString("status").equals("true")) {
                                resultIntent.putExtra("address_id", object.getJSONObject("data").getString("address_id"));
                                resultIntent.putExtra("name", name);
                                resultIntent.putExtra("zone_id", zone_id);
                                //resultIntent.putExtra("map_coordinates",  ""+latitude+","+longitude);
                                Log.d("INFO", "INTENT ZONE " + zone_id);
                                Log.d("INFO", "coordenadas " +""+latitude+","+longitude);
                                setResult(Activity.RESULT_OK, resultIntent);
                                Utils.appboyEvent(Appboy.getInstance(getApplicationContext()),
                                        getString(R.string.appboy_add_address),
                                        new Object[]{});
                                latitude =1.0;
                                longitude =1.0;
                                finish();
                            }
                        } catch (JSONException e) {
                            Log.d("address:PARSE-ERROR", e.getMessage());
                        }
                    }
                });

                task.execute();

            }
        });



    }

    void message(String message, String title){
        AlertDialog.Builder b = UIHelper.aceptAlert(AddAddressActivity.this, title, message);
        AlertDialog a = b.create();
        a.show();
    }

    void configureZones(){
        String[] zones = new String[beans.size()];

        for(int i=0; i < beans.size(); i++)
            zones[i] = beans.get(i).getState();

        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(),
                R.layout.spinner_custom_item, zones);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_custom_item);
        zoneSpin.setAdapter(adapter);
    }

    int zoneId(){
        int selectedZone = zoneSpin.getSelectedItemPosition();
        StateBean bean = beans.get(selectedZone);
        return bean.getId();
    }
    private void errorMessage(String msg){
        AlertDialog.Builder builder =
                UIHelper.aceptAlert(AddAddressActivity.this,
                        getString(R.string.missing_data),
                        msg);
        AlertDialog d = builder.create();
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.show();
    }

    public  Boolean checkGPSNetworkServices(final Context context) {
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

                        coordenadaRadio.setChecked(true);


                    }
                });
                dialog.setNegativeButton(context.getString(R.string.ignore), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        coordenadaRadioNo.setChecked(true);

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

    public  boolean checkInternetConnection(final Context context){
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
                        coordenadaRadio.setChecked(true);
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


    public void capturarUbicacion(){
        if(latitude==1.0 ||longitude == 1.0 ||latitude ==0.0 ||longitude == 0.0){
            timer.start();
        }else{
            timer.cancel();
            // progress.dismiss();
            findViewById(R.id.sending_add).setVisibility(View.GONE);
            findViewById(R.id.add_address_2).setVisibility(View.VISIBLE);
        }



    }



    /**
     * Funcion para mostrar pantalla principal de la aplicacion
     */
    private void goToMain() {
        Intent homeIntent = new Intent(getApplicationContext(), LoginActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
        finish();
    }
}
