package com.pidafacil.pidafacil.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appboy.Appboy;
import com.appboy.enums.Month;
import com.pidafacil.pidafacil.R;
import com.pidafacil.pidafacil.helper.UIHelper;
import com.pidafacil.pidafacil.model.Login;
import com.pidafacil.pidafacil.task.VoidTask;
import com.pidafacil.pidafacil.util.Utils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Calendar;

import io.realm.Realm;

public class EditProfileActivity extends ActionBarActivity {

    TextView txtName;
    TextView txtLastName;
    TextView txt_phone;
    TextView birthdate_;
    Button send;
    LinearLayout content;
    TextView message;
    Realm r;
    Login login;
    String serverdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        r = Realm.getInstance(getApplicationContext());
        configure();
        setTitle(getString(R.string.title_navbar_activity_edit_profile));
        load();
    }

    void configure() {
        txtName = (TextView) findViewById(R.id.txt_name_);
        txtLastName = (TextView) findViewById(R.id.txt_lastname_);
        birthdate_ = (TextView) findViewById(R.id.birthdate_);
        txt_phone = (TextView) findViewById(R.id.txt_phone);
        send = (Button) findViewById(R.id.button_send);
        content = (LinearLayout) findViewById(R.id._content_);
        message = (TextView) findViewById(R.id.__message__);

        if(r.allObjects(Login.class).size()>0){
            login = r.allObjects(Login.class).first();
        }else{
            content.setVisibility(View.GONE);
            message.setVisibility(View.VISIBLE);
            return;
        }

        birthdate_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int y = c.get(Calendar.YEAR), m = c.get(Calendar.MONTH), d = c.get(Calendar.DAY_OF_MONTH);
                String date = birthdate_.getText().toString();
                String[] bdate = date.split("-");

                if (bdate.length == 3) {
                    if (bdate[0] != "0000") {
                        y = Integer.parseInt(bdate[0]);
                        m = Integer.parseInt(bdate[1]);
                        d = Integer.parseInt(bdate[2]);
                    }
                }

                DatePickerDialog dp = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog datePickerDialog, int y, int m0, int d) {
                        birthdate_.setText(y + "-" + convert(m0 + 1) + "-" + d);
                    }
                }, y, m - 1, d);
                dp.show(getFragmentManager(), "Fecha de Nacimiento");
            }
        });

        events();
    }

    private String convert(int i) {
        return (String.valueOf(i).length()==1?"0"+i:""+i);
    }

    void events(){
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = txtName.getText().toString(),
                             lastname = txtLastName.getText().toString(),
                             phone = txt_phone.getText().toString();

                if(name.equals("") || lastname.equals("")){
                    message("El nombre y el apellido son requeridos",
                            getString(R.string.missing_data));
                    return;
                }

                if(phone.equals("")){
                    message("Por favor ingresa tu numero telefónico",
                            getString(R.string.missing_data));
                    return;
                }

                String birth = birthdate_.getText().toString();

                if(birth.isEmpty()){
                    birth = "0000-00-00";
                }

                VoidTask task = new VoidTask("/profile/edit");
                task.addParam("user_id", String.valueOf(login.getId()));
                task.addParam("name",name);
                task.addParam("last_name", lastname);
                task.addParam("phone",phone);
                task.addParam("birth_date", birth);
                final String finalBirth = birth;
                task.setPostExecute(new VoidTask.PostExecute() {
                    @Override
                    public void execute(StringBuilder response) {
                        Log.d("EDIT PROFILE", response.toString());
                        r.beginTransaction();
                        login.setUserName(name);
                        login.setUserLastName(lastname);
                        login.setPhoneNumber(phone);
                        r.commitTransaction();
                        Object[] args = null;

                        if (!finalBirth.isEmpty()) {

                            if(finalBirth.split("-").length > 0){
                                String[] birth = finalBirth.split("-");
                                args = new Object[birth.length];
                                args[0] = birth[0];
                                args[1] = (birth[1].equals("01")? Month.JANUARY:
                                        birth[1].equals("02")? Month.FEBRUARY:
                                                birth[1].equals("03")? Month.MARCH:
                                                        birth[1].equals("04")? Month.APRIL:
                                                                birth[1].equals("05") ? Month.MAY:
                                                                        birth[1].equals("06")? Month.JUNE:
                                                                                birth[1].equals("07")? Month.JULY:
                                                                                        birth[1].equals("08")? Month.AUGUST:
                                                                                                birth[1].equals("09")? Month.SEPTEMBER:
                                                                                                        birth[1].equals("10")? Month.OCTOBER:
                                                                                                                birth[1].equals("11")? Month.NOVEMBER:
                                                                                                                        Month.DECEMBER);
                                args[2] = birth[2];
                            }
                        }

                        Utils.saveUserInformation(Appboy.getInstance(
                                getApplicationContext()),name, lastname,null, phone, args);
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.profile_updated),
                                Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    }
                });
                task.execute();
            }
        });
    }

    VoidTask.PostExecute post = new VoidTask.PostExecute() {
        @Override
        public void execute(StringBuilder response) {
            Log.d("Profile:WS-RESULT",response.toString());
            try {
                org.json.JSONObject data = new JSONObject(response.toString());
                data = data.getJSONObject("data");
                txtName.setText(data.getString("name"));
                txtLastName.setText(data.getString("last_name"));
                txt_phone.setText(data.getString("phone"));
                String birthdate = data.getString("birth_date");
                String[] date = birthdate.split("\\-");
                if(!date[0].equals("0000")){
                    birthdate_.setText(birthdate);
                    serverdate = birthdate;
                }

                txt_phone.setText(data.getString("phone"));
                Log.d("INFO","date - "+ Arrays.toString(date));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    void load(){
        VoidTask task = new VoidTask("/profile");
        task.addParam("user_id",  String.valueOf(login.getId()));
        task.setPostExecute(post);
        task.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    void message(String str, String title){
        AlertDialog.Builder builder = UIHelper.alert(EditProfileActivity.this, title, str);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
    }
}
