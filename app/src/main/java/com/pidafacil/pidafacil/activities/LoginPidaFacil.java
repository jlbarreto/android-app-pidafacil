package com.pidafacil.pidafacil.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.appboy.Appboy;
import com.pidafacil.pidafacil.NavigationDrawer;
import com.pidafacil.pidafacil.R;
import com.pidafacil.pidafacil.beans.SimpleParam;
import com.pidafacil.pidafacil.helper.WebServiceHelper;
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

public class LoginPidaFacil extends ActionBarActivity implements ExecutionMethod{
    @Bind(R.id.txt_email_) EditText email;
    @Bind(R.id.txt_password) EditText password;
    private boolean shoppingBack = false;
    private String passwordStr;
    private String emailStr;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pida_facil_login);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(getString(R.string.title_login_activity));

        realm = Realm.getInstance(this);

        ButterKnife.bind(this);

        shoppingCartBackConfig();
    }

    @OnClick(R.id.button_pf_login)
    void enter(View v){
        passwordStr = this.password.getText().toString();
        emailStr = this.email.getText().toString();
        if(emailStr != null && passwordStr != null){
            emailStr = emailStr.trim();
            if(!validation())
                return;
            VoidActivityTask task = WebServiceHelper.emailCheck(this, 0);
            task.addParam("email", emailStr);
            task.execute();
        } else {
            message("Introduce tu correo electrónico y contraseña.");
        }
    }

    public boolean validation(){
        if(emailStr.isEmpty() && passwordStr.isEmpty()){
            message("Introduce tu correo electrónico y contraseña.");
            return false;
        }
        if(!Validator.isEmail(emailStr)){
            message("Correo electrónico inválido.");
            return false;
        }
        if(!Validator.validPassword(passwordStr)){
            message("La contraseña es requerida y debe ser mayor o igual a 4 caracteres.");
            return false;
        }
        return true;
    }

    private void message(String message){
        AlertDialog.Builder build = UIHelper.alert(LoginPidaFacil.this,
                null,
                message);
        build.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = build.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_no_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                goBack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void executeResult(List list, int operationCode) {
        Login login = null;

        if(realm.allObjects(Login.class).size()>0){
            login = realm.allObjects(Login.class).first();
        }

        /*
         *  Check Response
         */
        if(operationCode == 0){
            if(list!=null){
                String status = (String) list.iterator().next();
                if(status.equals("false")){
                    message("No existe una cuenta asociada a este correo electrónico.");
                }else{
                    List<SimpleParam> params = new ArrayList<SimpleParam>();
                    params.add(new SimpleParam("email", emailStr));
                    params.add(new SimpleParam("password", passwordStr));
                    VoidActivityTask task1 = new VoidActivityTask("/login", params, this, 1) {
                        @Override
                        public void execute(StringBuilder result) {
                            Log.d("correo:WS-RESULT", result.toString());
                            try {
                                JSONObject o = new JSONObject(result.toString());
                                this.resultList = new ArrayList<>();
                                this.resultList.add(o.getString("status"));
                                this.resultList.add(o.getJSONObject("data"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    task1.execute();
                }
            }
        }

        /*
         * Login Response
         */
        if(operationCode == 1){
            if(list!=null){
                Iterator it = list.iterator();
                String status = (String) it.next();
                if(status.equals("true")){
                    org.json.JSONObject data = (JSONObject) it.next();
                    try {
                        realm.beginTransaction();
                        if(login == null)
                            login = realm.createObject(Login.class);

                        login.setId(data.getInt("user_id"));
                        login.setEmail(data.getString("email"));
                        login.setUserName(data.getString("name"));
                        login.setUserLastName(data.getString("last_name"));
                        login.setPhoneNumber(data.getString("phone"));
                        login.setSospecha(data.getString("sospecha"));
                        Log.d("Sospecha", data.getString("sospecha"));
                        realm.commitTransaction();

                        /*
                            Este valor determina si usuario regresa a carrito de compras
                            despues de Inicio de Sesion o Registro
                         */
                        UD.getInstance().put("shopping_back__", null);

                        // Registrar evento Log In en Appboy
                        Utils.appboyEvent(Appboy.getInstance(getApplicationContext()),
                                getString(R.string.appboy_login),
                                new Object[]{});

                        if (this.shoppingBack) {
                            startActivity(new Intent(getApplicationContext(), ShoppingCartActivity.class));
                            finish();
                        } else {
                            goToMain();
                        }

                    } catch (JSONException e) {
                        Log.d("PARSE-ERR",e.getMessage());
                        realm.commitTransaction();
                        parentView();
                    }
                }else{
                    message("Contraseña o correo electrónico inválido.");
                }
            }
        }
    }

    public void parentView() {
        NavUtils.navigateUpFromSameTask(this);
    }

    private void shoppingCartBackConfig(){
        Boolean shoppingBack = (Boolean) UD.getInstance().get("shopping_back__");
        if(shoppingBack!=null){
            this.shoppingBack = shoppingBack;
        }
    }

    private void goToMain() {
        Intent homeIntent = new Intent(getApplicationContext(), NavigationDrawer.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
        finish();
    }

    private void goBack() {
        Intent activity = new Intent(getApplicationContext(), LoginActivity.class);
        activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(activity);
        finish();
    }
}