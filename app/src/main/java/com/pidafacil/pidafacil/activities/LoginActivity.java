package com.pidafacil.pidafacil.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appboy.Appboy;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.pidafacil.pidafacil.NavigationDrawer;
import com.pidafacil.pidafacil.R;
import com.pidafacil.pidafacil.helper.UIHelper;
import com.pidafacil.pidafacil.model.Login;
import com.pidafacil.pidafacil.singleton.UD;
import com.pidafacil.pidafacil.task.VoidTask;
import com.pidafacil.pidafacil.util.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;

/**
 * @author victor
 */
public class LoginActivity extends ActionBarActivity {
    private CallbackManager callbackManager;
    private LinearLayout lay_logindata;
    private Button closeSession;
    private Button ordersButton;
    private Button addressButton;
    private LinearLayout lay_account;
    private TextView profile_email;
    private TextView profile_userName;
    private CircleImageView profile_image_;
    private LinearLayout lay_social;
    private UD U = UD.getInstance();
    private Realm r;
    private static final int RC_SIGN_IN = 0;
    private static int EDIT_REQCODE = 55;
    private GoogleApiClient mGoogleApiClient;
    private ConnectionResult mConnectionResult;
    private Button btnSignIn;
    private Button btnSignIn2;
    private LoginButton authbutton;
    private LoginButton authbutton2;
    private boolean mResolveOnFail = false;
    private boolean shoppingBack = false;
    private MenuItem editMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        U.setCurrentActivity(this);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(!FacebookSdk.isInitialized())
            FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        configure();
        session();
        events();
    }

    @OnClick(R.id.button_register__)
    void register(View v){
        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
    }

    private void shoppingCartBackConfig(){
        Boolean shoppingBack = (Boolean) U.get("shopping_back__");
        if(shoppingBack!=null){
            this.shoppingBack = shoppingBack;
        }
    }

    /************************************
     *  TODO CONFIGURE ELEMENTS         *
     ************************************/
    void configure(){
        r = Realm.getInstance(getApplicationContext());
        lay_social = (LinearLayout) findViewById(R.id.lay_social);
        lay_logindata = (LinearLayout) findViewById(R.id.lay_login_data);
        addressButton = (Button) findViewById(R.id.button_addresses__);
        closeSession = (Button) findViewById(R.id.txt_close_session__);
        ordersButton = (Button) findViewById(R.id.button_requests__);
        lay_account = (LinearLayout) findViewById(R.id.lay_account__);
        profile_email = (TextView) findViewById(R.id.txt_user_email__);
        profile_userName = (TextView) findViewById(R.id.txt_user_name__);
        profile_image_  = (CircleImageView) findViewById(R.id.img__);
        btnSignIn = (Button) findViewById(R.id.btn_sign_in);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resolveSignInError();
            }
        });

        btnSignIn2 = (Button) findViewById(R.id.btn_sign_in2);
        btnSignIn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resolveSignInError();
            }
        });

        authbutton= (LoginButton) findViewById(R.id.login_button);
        authbutton.setBackgroundResource(R.drawable.facebook);
        authbutton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,0);
        authbutton.setReadPermissions(Arrays.asList("public_profile, email"));


        authbutton2= (LoginButton) findViewById(R.id.login_button2);
        authbutton2.setBackgroundResource(R.drawable.facebook);
        authbutton2.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,0);
        authbutton2.setReadPermissions(Arrays.asList("public_profile, email"));

        facebookSessionConfig();
        googleSessionConfig();
        shoppingCartBackConfig();
    }


    // VALIDACION DE SESION
    void session(){
        if(!isLoged())
            this.setTitle(getString(R.string.title_signup_activity));
        else
            showAccount();
    }

    //Verificacion de sesion local
    boolean isLoged(){
        if(r.allObjects(Login.class).size()==0){
            Log.d("INFO"," User is NOT auth ");
            return false;
        }else{
            Login login = r.allObjects(Login.class).first();
            if(login.getId() > 0){
                Log.d("INFO"," User is auth ");
                return true;
            }else{
                Log.d("INFO"," User is NOT auth ");
                return false;
            }
        }
    }

    /********************************
     * TODO GOOGLE PLUS LOGIN IMPL  *
     ********************************/
    private void googleSessionConfig() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        Log.d("INFO:","GOOGLE+ OK");
                        saveUserInformation();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        mGoogleApiClient.connect();
                    }
                })
                .addOnConnectionFailedListener(new OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d("ERROR", "GOOGLE+ ERROR CODE: " + result.getErrorCode());
                        if (result.hasResolution()) {
                            mConnectionResult = result;
                            if (mResolveOnFail) {
                                resolveSignInError();
                                mResolveOnFail=false;
                            }
                        }
                    }
                }).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
    }

    /**
     * TODO FACEBOOK SESSION IMPL
     * */
    private void facebookSessionConfig() {
        callbackManager = CallbackManager.Factory.create();
        authbutton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("INFO: ", "SUCCESS! " + loginResult.toString());
                        final AccessToken accessToken = AccessToken.getCurrentAccessToken();
                        if (accessToken != null) {

                            GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(final JSONObject jsonObject, GraphResponse graphResponse) {
                                    Log.d("INFO: ", "-USER INFO! " + jsonObject.toString());
                                    VoidTask simpleTask = new VoidTask("/email-check", checkMethod);
                                    String email = null;

                                    try {
                                        JSONObject pic = jsonObject.getJSONObject("picture");
                                        JSONObject data = pic.getJSONObject("data");
                                        U.put(UD.DATA_IMG_RESOURCE_PROFILE, data.getString("url"));
                                        U.put(UD.DATA_USER_SOCIAL_NET, jsonObject);
                                        email = jsonObject.getString("email");
                                        Log.d("email",""+email);
                                    } catch (JSONException e) {
                                        Log.d("JSON-ERR", e.getMessage());
                                    }

                                    if (email != null) {
                                        simpleTask.addParam("email", email);
                                        simpleTask.execute();
                                    } else {
                                        UIHelper.aceptAlert(LoginActivity.this, "", "No pudimos obtener tu email").show();
                                        return;
                                    }
                                }
                            });

                            Bundle params = new Bundle();
                            params.putString("fields", "id, name, picture, email");
                            request.setParameters(params);
                            request.executeAsync();
                        }
                    }

                    @Override
                    public void onCancel() {
                        Log.d("FB-INFO", "Operacion Cancelada");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.d("FB-ERR", exception.getMessage());
                    }
                });
    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        Appboy.getInstance(this).openSession(this);
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        Appboy.getInstance(this).closeSession(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        editMenuItem = menu.findItem(R.id.edit_profile_);
        if(!isLoged()){
            editMenuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                end();
                return true;
            case R.id.edit_profile_:
                startActivityForResult(new Intent(getApplicationContext(), EditProfileActivity.class), EDIT_REQCODE);
            default:
                return super.onOptionsItemSelected(item);
        }
        //int id = item.getItemId();
        /*if(id == R.id.edit_profile_){

        }*/
        //return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Log.d("RESULT","Google+ "+resultCode);
            if (resultCode != RESULT_OK){
                mResolveOnFail = true;
            }
            mGoogleApiClient.connect();
        }else if(requestCode == EDIT_REQCODE){
            Log.d("RESULT","User edit "+resultCode);
            Login login = r.allObjects(Login.class).first();
            profile_userName.setText(login.getUserName().concat(" ").concat(login.getUserLastName()));
        }else{
            Log.d("RESULT","FACEBOOK "+resultCode);
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    void resolveSignInError() {
        if(mConnectionResult != null ) {
            if (mConnectionResult.hasResolution()) {
                try {
                    mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
                } catch (IntentSender.SendIntentException e) {
                    mGoogleApiClient.connect();
                }
            }
        }else { mGoogleApiClient.connect();}
    }

    //Guardando informacion del usuario
    void saveUserInformation() {
        try {

            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String personGooglePlusProfile = currentPerson.getUrl();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
                org.json.simple.JSONObject user = new org.json.simple.JSONObject();
                user.put("name",personName);
                user.put("email", email);
                user.put("id",currentPerson.getId());
                U.put(UD.DATA_IMG_RESOURCE_PROFILE, personPhotoUrl);
                U.put(UD.DATA_USER_SOCIAL_NET, new org.json.JSONObject(user.toJSONString()));

                VoidTask simpleTask = new VoidTask("/email-check" , checkMethod);
                simpleTask.addParam("email", email);
                simpleTask.execute();

                Log.d("INFO", "Name: " + personName + ", plusProfile: "
                        + personGooglePlusProfile + ", email: " + email
                        + ", Image: " + personPhotoUrl);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // signInWithGplus() not used

    /****************************************************************
     *  TODO SOCIAL REALM LOGIN IMPLEMENT                               *
     *  LAS DECLARACIONES DE VOIDSIMPLETASK PARA LOGIN DE FACEBOOK  *
     * **************************************************************/
    VoidTask.PostExecute registerMethod  = new VoidTask.PostExecute() {
        @Override
        public void execute(StringBuilder response) {
            Log.d("WS-RESULT","REGISTER TOSTRING " + response.toString());
            try{
                JSONObject object = new JSONObject(response.toString());
                //DESPUES DE GUARDAR EL NUEVO USUARIO
                JSONObject o = (JSONObject) UD.getInstance().get(UD.DATA_USER_SOCIAL_NET);
                Log.d("INFO","User JSON string "+o.toString());
                Login login;

                if(r.allObjects(Login.class).size() > 0){
                    login = r.allObjects(Login.class).first();
                }else{
                    login = null;
                }

                r.beginTransaction();
                if(login!=null){
                    login.setId(object.getJSONObject("data").getInt("user_id"));
                    login.setUserName(o.getString("name"));
                    login.setLoginMethod(2);
                    login.setEmail(o.getString("email"));
                    Appboy.getInstance(LoginActivity.this).changeUser(o.getString("email"));
                    login.setUserLastName(o.getString("last_name"));
                } else {
                    Login lo = r.createObject(Login.class);
                    lo.setId(object.getJSONObject("data").getInt("user_id"));
                    lo.setUserName(o.getString("name"));
                    lo.setEmail(o.getString("email"));
                    lo.setLoginMethod(2);
                    Appboy.getInstance(LoginActivity.this).changeUser(o.getString("email"));
                    lo.setUserLastName(o.getString("last_name"));
                }
                r.commitTransaction();
            } catch (JSONException e) {
                Log.d("JSON-ERR","REGISTER METHOD RESPONSE " + e.getMessage());
                r.commitTransaction();
                showAccount();
            }
            Log.d("INFO: ", "Nuevo Usuario guardado");
            // Registrar evento en Appboy
            Utils.appboyEvent(Appboy.getInstance(getApplicationContext()),
                    getString(R.string.appboy_signup),
                    new Object[]{});
        }
    };

    // CONSULTA DE EMAIL
    VoidTask.PostExecute checkMethod = new VoidTask.PostExecute() {
        @Override
        public void execute(StringBuilder result) {
            Log.d("EMAIL:INFO: WS-RESULT",result.toString());
            try {
                JSONObject object  = new JSONObject(result.toString());
                JSONObject user = (JSONObject) UD.getInstance().get(UD.DATA_USER_SOCIAL_NET);
                String status = object.getString("status");

                if(status.equals("true")){
                    object = object.getJSONObject("data");
                    Login login = null;
                    if(r.allObjects(Login.class).size() > 0)
                        login = r.allObjects(Login.class).first();

                    r.beginTransaction();
                    String[] names = user.getString("name").split(" ");

                    if(login!=null){
                        login.setId(object.getInt("user_id"));
                        login.setSospecha(object.getString("sospecha"));

                        if(names.length >= 2) {
                            login.setUserName(names[0]);
                            login.setUserLastName(names[1]);
                        }else
                            login.setUserName(user.getString("name"));

                        login.setLoginMethod(2);
                        login.setEmail(object.getString("email"));
                    }else{
                        Login lo = r.createObject(Login.class);
                        lo.setId(object.getInt("user_id"));
                        if(names.length >= 2) {
                            lo.setUserName(names[0]);
                            lo.setUserLastName(names[1]);
                        }else
                            lo.setUserName(user.getString("name"));
                        lo.setEmail(object.getString("email"));
                        lo.setSospecha(object.getString("sospecha"));
                        lo.setLoginMethod(2);
                    }

                    r.commitTransaction();

                    Log.d("INFO: ", "Usuario existente guardado");
                    showAccount();
                }else{
                    // REGISTRO DE NUEVO USUARIO POR REDES SOCIALES
                    Log.d("INFO: ","Este Usuario no existe, registrando..");
                    String[] names = user.getString("name").split(" ");

                    VoidTask task = new VoidTask("/social-connect", registerMethod);
                    task.addParam("uid",user.getString("id"));
                    task.addParam("email", user.getString("email"));

                    if(names.length == 2){
                        task.addParam("first_name",names[0]);
                        task.addParam("last_name",names[1]);
                    }else{
                        task.addParam("first_name",names[0]);
                        task.addParam("last_name","");
                    }

                    if(U.get(UD.DATA_IMG_RESOURCE_PROFILE)!=null){
                        task.addParam("image_url", (String) U.get(UD.DATA_IMG_RESOURCE_PROFILE));
                    }else{
                        task.addParam("image_url","");
                    }

                    task.execute();
                }
            } catch (JSONException e) {
                Log.d("JSON-ERR",e.getMessage());
            }

        }
    };


    /** TODO EVENTS */
    public void events(){

        addressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddressActivity.class));
            }
        });

        ordersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), OrderActivity.class));
            }
        });

        closeSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder build = UIHelper.alert(LoginActivity.this,
                        "¿Deseas salir de PidaFacil?",
                        "Puedes ingresar nuevamente cuando quieras.");
                build.setPositiveButton("Salir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        closeSession();
                    }
                });
                build.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = build.create();
                alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
                alert.show();
            }
        });

        findViewById(R.id.button_link_pf_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginPidaFacil.class));
            }
        });
    }

    void closeSession(){
        Log.d("INFO", "Closing sesion");
        Login login = r.allObjects(Login.class).first();

        r.beginTransaction();
        login.removeFromRealm();
        r.commitTransaction();

        if(UD.getInstance().fbSessionIsOpen()){
            Log.d("UD-INFO","Cerrando sesion FB");
            AccessToken.setCurrentAccessToken(null);
            Log.d("UD-INFO","Token = null");
        }

        if(mGoogleApiClient.isConnected()){
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
            Log.d("INFO", "Sesion was closed");
        }

        showLogin();
    }

    /********************************
     * TODO ACCOUNT LOGIN IMPL      *
     * ******************************/
     void showAccount() {
         if(this.shoppingBack){
             UD.getInstance().put("shopping_back__", null);
             startActivity(new Intent(this, ShoppingCartActivity.class));
             finish();
         }

         this.setTitle("Mi Perfil");
         lay_logindata.setVisibility(View.GONE);
         lay_account.setVisibility(View.VISIBLE);
         Login login = r.allObjects(Login.class).first();
         profile_email.setText(login.getEmail());
         profile_userName.setText(login.getUserName() + " " + login.getUserLastName());
         Appboy appboy = Appboy.getInstance(getApplicationContext());
         appboy.changeUser(login.getEmail());
         appboy.getCurrentUser().setEmail(login.getEmail());

         VoidTask.PostExecute postMethod = new VoidTask.PostExecute() {
             @Override
             public void execute(StringBuilder response) {
                 try {
                     if(response !=null) {

                         JSONObject data = new JSONObject(response.toString());
                         data = data.getJSONObject("data");
                         String url = data.getString("photo");
                         if (url != null) {
                             if (!url.equals("null")) {
                                 Login login1 = r.allObjects(Login.class).first();
                                 r.beginTransaction();
                                 login1.setPhoto(true);
                                 login1.setPhotoUri(url);
                                 r.commitTransaction();
                                 Picasso.with(getApplicationContext()).load(login1.getPhotoUri()).into(profile_image_);
                             }
                         }
                         if (!editMenuItem.isVisible())
                             editMenuItem.setVisible(true);
                     }
                 } catch (JSONException e) {
                     Log.d("ERR-JSON",e.getMessage());
                 }
             }
         };

         VoidTask taskUser = new VoidTask("/profile");
         taskUser.setPostExecute(postMethod);
         taskUser.addParam("user_id", String.valueOf(login.getId()) );
         taskUser.execute();
    }

    void showLogin() {
        if(editMenuItem.isVisible())
            editMenuItem.setVisible(false);
        lay_logindata.setVisibility(View.VISIBLE);
        lay_account.setVisibility(View.GONE);
        profile_email.setText("");
        profile_userName.setText("");
    }

    void end(){
        Intent homeIntent = new Intent(getApplicationContext(), NavigationDrawer.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }
}
