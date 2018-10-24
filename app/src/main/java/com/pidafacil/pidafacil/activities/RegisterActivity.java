package com.pidafacil.pidafacil.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.appboy.Appboy;
import com.pidafacil.pidafacil.NavigationDrawer;
import com.pidafacil.pidafacil.R;
import com.pidafacil.pidafacil.beans.SimpleParam;
import com.pidafacil.pidafacil.helper.UIHelper;
import com.pidafacil.pidafacil.model.Login;
import com.pidafacil.pidafacil.singleton.UD;
import com.pidafacil.pidafacil.task.ExecutionMethod;
import com.pidafacil.pidafacil.task.VoidActivityTask;
import com.pidafacil.pidafacil.util.Utils;
import com.pidafacil.pidafacil.util.Validator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class RegisterActivity extends ActionBarActivity implements ExecutionMethod {
    @Bind(R.id.txt_email_) EditText email;
    @Bind(R.id.txt_password) EditText password;
    @Bind(R.id.txt_password_confirm_) EditText passwordConfirm;
    private String passwordStr;
    private String passwordConfirmStr;
    private String emailStr;
    private Realm r;
    private boolean shoppingBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        configure();
        shoppingCartBackConfig();
    }

    void configure(){
        setTitle(getString(R.string.title_activity_signup));
        r = Realm.getInstance(this);
    }

    @OnClick(R.id.button_register__)
    void register(View v){
        passwordStr = this.password.getText().toString();
        emailStr = this.email.getText().toString();
        passwordConfirmStr = this.passwordConfirm.getText().toString();

        if(emailStr != null && passwordStr != null){
            if(!validation())
                return;

            List<SimpleParam> params = new ArrayList<SimpleParam>();
            params.add(new SimpleParam("email", emailStr));
            params.add(new SimpleParam("password", passwordStr));
            params.add(new SimpleParam("terms_acceptance", "1"));

            VoidActivityTask task2 = new VoidActivityTask("/register", params, this, 2) {
                @Override
                public void execute(StringBuilder result) {
                    Log.d("register:WS-RESULT", result.toString());
                    this.resultList = new ArrayList();
                    try {
                        JSONObject o = new JSONObject(result.toString());
                        String status = o.getString("status");
                        this.resultList.add(status);
                        this.resultList.add(o.getJSONObject("data"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            task2.execute();
        }else {
            message("Faltan datos por completar", "Registro");
        }
    }

    public boolean validation(){

        if(emailStr.isEmpty() && passwordStr.isEmpty()){
            message("Ingresa un correo electrónico y contraseña", "Faltan datos por completar");
            return false;
        }

        if(!Validator.isEmail(emailStr)){
            message("Este correo electrónico no es valido", "Correo inválido");
            return false;
        }

        if(!Validator.validPassword(passwordStr)){
            message("La contraseña es requerida y debe ser mayor o igual a 4 caracteres", "Contraseña inválida");
            return false;
        }

        if(!passwordStr.equals(passwordConfirmStr)){
            message("Las contraseñas no coinciden", "Contraseña inválida");
            return false;
        }

        return true;
    }

    private void message(String message, String title){
        AlertDialog.Builder build = UIHelper.alert(RegisterActivity.this, title, message);
        build.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        build.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_no_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void executeResult(List list, int operationCode) {
        List objects = r.allObjects(Login.class);
        Login login;

        if(objects.size()>0){
            login = (Login) objects.get(0);
        }else{
            r.beginTransaction();
            login = r.createObject(Login.class);
            r.commitTransaction();
        }

        if(operationCode == 2){
            if(list!=null){
                Log.d("WS-REGISTER", list.toString());
                Iterator it = list.iterator();
                String status = (String) it.next();
                if(status.equals("true")){
                    org.json.JSONObject data = (JSONObject) it.next();
                    Log.d("WS-REGISTER", data.toString());
                    try {
                        r.beginTransaction();
                        login.setId(data.getInt("user_id"));
                        login.setUserName("Usuario");
                        login.setEmail(data.getString("email"));
                        r.commitTransaction();
                        Appboy appboy = Appboy.getInstance(this);
                        appboy.changeUser(data.getString("email"));

                        // Almacenar informacion de usuario en Appboy
                        Utils.saveUserInformation(appboy,
                                null, null, data.getString("email"), null, null);

                        // Registrar evento en Appboy
                        Utils.appboyEvent(Appboy.getInstance(getApplicationContext()),
                                getString(R.string.appboy_signup),
                                new Object[]{});

                        if (this.shoppingBack) {
                            startActivity(new Intent(getApplicationContext(), ShoppingCartActivity.class));
                            finish();
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.continue_shopping),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            goToMain();
                        }
                    } catch (JSONException e) {
                        r.cancelTransaction();
                        if("No value for name".equals(e.getMessage().trim())){
                            login.setUserName("Usuario");
                            r.commitTransaction();
                            toView(LoginActivity.class);
                            finish();
                        }else{
                            r.commitTransaction();
                        }
                    }
                }else{
                    org.json.JSONObject o = (JSONObject) it.next();
                    try {
                        String message = o.getJSONArray("email").get(0).toString();
                        if (!message.isEmpty()) {
                            message("Este correo electrónico ya ha sido registrado", "Correo inválido");
                        }
                        return;
                    } catch (JSONException e) { message("Por favor, intenta de nuevo", "Algo salio mal");}
                }
            }
        }
    }

    public void toView(Class activityClass) {
        startActivity(new Intent(getApplicationContext(), activityClass));
    }

    private void shoppingCartBackConfig(){
        Boolean shoppingBack = (Boolean) UD.getInstance().get("shopping_back__");
        if(shoppingBack!=null){
            this.shoppingBack = shoppingBack;
            UD.getInstance().put("shopping_back__", null);
        }
    }

    private void goToMain() {
        Intent homeIntent = new Intent(getApplicationContext(), NavigationDrawer.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
        finish();
    }

}
