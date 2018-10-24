package com.pidafacil.pidafacil.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.pidafacil.pidafacil.R;
import com.pidafacil.pidafacil.beans.StateBean;
import com.pidafacil.pidafacil.helper.JsonParser;
import com.pidafacil.pidafacil.helper.UIHelper;
import com.pidafacil.pidafacil.singleton.UD;
import com.pidafacil.pidafacil.task.VoidTask;
import com.pidafacil.pidafacil.util.GPSTracker;
import com.pidafacil.pidafacil.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddressDetailActivity extends ActionBarActivity {
    @Bind(R.id.txt_name) EditText name;
    @Bind(R.id.txt_dir1) TextView dir1;
    @Bind(R.id.txt_dir2) TextView dir2;
    @Bind(R.id.spin_zone) Spinner zone;
    @Bind(R.id.txt_ref) TextView ref;
    @Bind(R.id.rdbCoordenadasSi) RadioButton siCoordenadas;
    private String address_id;
    private Spinner zoneSpin;
    private UD U = UD.getInstance();
    private List<StateBean> beans = null;
    private int defaultZone = 0;
    private ArrayAdapter adapterZone;
    private RadioGroup coordenadasGroup;
    private RadioButton coordenadaRadio;
    private RadioButton coordenadaRadioNo;
    GPSTracker gps;
    private CountDownTimer timer;
    public static  double latitude = 1.0;
    public static  double longitude = 1.0;
    public static int gpsOK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_address_detail);
        coordenadasGroup = (RadioGroup) findViewById(R.id.rdgCoordenadasGroup);
        coordenadaRadio = (RadioButton) findViewById(R.id.rdbCoordenadasSi);
        coordenadaRadioNo = (RadioButton) findViewById(R.id.rdbCoordenadasNo);
        coordenadasGroup.check(R.id.rdbCoordenadasSi);
        ButterKnife.bind(this);
        coordenadasGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.rdbCoordenadasSi) {
                    // Si compartir coordenadas
                    gpsOK = 1;
                    if (checkGPSNetworkServices(AddressDetailActivity.this)) {
                        gps = new GPSTracker(AddressDetailActivity.this);
                        if (gps.canGetLocation()) {
                            //  txtCoordenadas.setVisibility(View.VISIBLE);

                            latitude = gps.getLatitude();
                            longitude = gps.getLongitude();
                            gpsOK = 1;

                            findViewById(R.id.add_address_2).setVisibility(View.GONE);
                            findViewById(R.id.sending_add).setVisibility(View.VISIBLE);
                            //   progress = ProgressDialog.show(AddAddressActivity.this, null, "Espere...", true);

                            timer = new CountDownTimer(5000, 1000) {
                                //300000, 20
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    latitude = gps.getLatitude();
                                    longitude = gps.getLongitude();
                                    Log.d("gps0", "timer" + latitude + "," + longitude);
                                }

                                @Override
                                public void onFinish() {
                                    try {
                                        capturarUbicacion();
                                        Log.d("timer", "timer ");
                                    } catch (Exception e) {
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

                    latitude = 0.0;
                    longitude = 0.0;

                }
            }
        });

        configure();

        if(U.get("cache_addrbeans")!=null){
            beans = (List<StateBean>) U.get("cache_addrbeans");
            configureZones();
            loadUserInfomation();
        } else {
            loadAddresses();
        }



    }

    @OnClick(R.id.button_ok)
    void send(View v){
        String name = this.name.getText().toString();
        String dir1 = this.dir1.getText().toString();
        String dir2 = this.dir2.getText().toString();
        String zone = this.zone.getSelectedItem().toString();
        String ref = this.ref.getText().toString();
        RadioButton siCoordenadas = this.siCoordenadas;

        if(latitude == 1.0 || longitude==1.0){

            Log.d("No capturadas", "" + latitude + "," + longitude);

        }
        Log.d("gps1", "" + gpsOK);
        if(siCoordenadas.isChecked()){
            if (checkGPSNetworkServices(AddressDetailActivity.this)) {
                gps = new GPSTracker(AddressDetailActivity.this);
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

        dir2="Vacia";
        Log.d("name","name:"+name);
        if(name.isEmpty() || name.trim().equals("") || name.length() == 0){
            message("El nombre de la dirección es requerido", "Faltan datos por completar");
           return;
        }
        if(dir1.length()<6){
            errorMessage(getString(R.string.dir1_error));
            return;
        }

        if(dir1.isEmpty() || dir1.trim().equals("") || dir1.length() == 0){
            message("Direccion 1 es requerido", "Faltan datos por completar");
            return;
        }

        if(zone.isEmpty()){
            message("Elige una zona de cobertura", "Faltan datos por completar");
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

        //startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);

        String zone_id = String.valueOf(zoneId());

        VoidTask task = new VoidTask("/user/address/edit");
        task.addParam("address_id",address_id);
        task.addParam("address_name",name);
        task.addParam("address_1",dir1);
        task.addParam("address_2",dir2);
        task.addParam("reference",ref);
        task.addParam("zone_id",zone_id);
        task.addParam("map_coordinates", ""+latitude+","+longitude );
        latitude =1.0;
        longitude =1.0;
        task.setPostExecute(new VoidTask.PostExecute() {
            @Override
            public void execute(StringBuilder response) {
                toParent();
            }
        });
        task.execute();
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
                    loadUserInfomation();
                } catch (JSONException e) {
                    Log.d("zonesG:PARSE-ERR", e.getMessage());
                }
            }
        });
        task.execute();
    }

    void toParent(){
        Intent intent = new Intent();
        intent.putExtra("ok", "ok");
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    void message(String message, String title){
        AlertDialog.Builder b = UIHelper.aceptAlert(AddressDetailActivity.this, title, message);
        AlertDialog a = b.create();
        a.show();
    }

    void configure(){
        zoneSpin = ((Spinner) findViewById(R.id.spin_zone));

        adapterZone = new ArrayAdapter(getApplicationContext(),R.layout.spinner_custom_item);
        adapterZone.setDropDownViewResource(R.layout.spinner_dropdown_custom_item);
        zoneSpin.setAdapter(adapterZone);

        setTitle("Editar direccion");


            address_id = String.valueOf(UD.getInstance().get("address_id"));
    }

    void configureZones(){

        String[] zones = new String[beans.size()];

        for(int i=0; i < beans.size(); i++)
            zones[i] = beans.get(i).getState();

        if((zones.length-1)<defaultZone){
            defaultZone = 0;
        }
        adapterZone.clear();
        adapterZone.addAll(zones);
        adapterZone.notifyDataSetChanged();
    }

    void loadUserInfomation(){
        VoidTask task = new VoidTask("/user/address/get");
        task.addParam("address_id", address_id);
        task.setPostExecute(new VoidTask.PostExecute() {
            @Override
            public void execute(StringBuilder response) {
                Log.d("addressG:WS-RESULT", response.toString());
                try {
                    JSONObject data = new JSONObject(response.toString());
                    data = data.getJSONObject("data");
                    name.setText(data.getString("address_name"));
                    dir1.setText(data.getString("address_1"));
                    dir2.setText(data.getString("address_2"));
                    ref.setText(data.getString("reference"));

                    int zone_id = data.getInt("zone_id");

                    for (int i = 0; i < beans.size(); i++) {
                        StateBean bean = beans.get(i);
                        if (bean.getId().equals(zone_id)) {
                            configureZones();
                            zoneSpin.setSelection(i);
                            break;
                        }
                    }
                } catch (JSONException e) {
                    Log.d("addressG:ERR", e.getMessage());
                }
            }
        });
        task.execute();
    }

    int zoneId(){
        int selectedZone = zoneSpin.getSelectedItemPosition();
        StateBean bean = beans.get(selectedZone);
        return bean.getId();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void errorMessage(String msg){
        AlertDialog.Builder builder =
                UIHelper.aceptAlert(AddressDetailActivity.this,
                        getString(R.string.missing_data),
                        msg);
        AlertDialog d = builder.create();
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.show();
    }
    @Override
    protected void onResume() {
        super.onResume();



    }


    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            switch (requestCode) {
                case 1:
                    Log.d("gps","gps");
                    break;
            }
        }
    }
*/

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

}
