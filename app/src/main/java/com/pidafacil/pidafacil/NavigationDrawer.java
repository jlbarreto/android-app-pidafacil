package com.pidafacil.pidafacil;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.appboy.Appboy;
import com.appboy.enums.NotificationSubscriptionType;
import com.appboy.ui.inappmessage.AppboyInAppMessageManager;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.newrelic.agent.android.NewRelic;
import com.pidafacil.pidafacil.activities.AddAddressActivity;
import com.pidafacil.pidafacil.activities.AddressActivity;
import com.pidafacil.pidafacil.activities.DeepLinkParserActivity;
import com.pidafacil.pidafacil.activities.LoginActivity;
import com.pidafacil.pidafacil.activities.LoginPidaFacil;
import com.pidafacil.pidafacil.activities.ShoppingCartActivity;
import com.pidafacil.pidafacil.activities.ZoomAnimation;
import com.pidafacil.pidafacil.beans.Suggestion;
import com.pidafacil.pidafacil.beans.UserRequestBean;
import com.pidafacil.pidafacil.components.ItemAdapter;
import com.pidafacil.pidafacil.fragments.DetailFragment;
import com.pidafacil.pidafacil.fragments.ExploreFragment;
import com.pidafacil.pidafacil.fragments.OrderFragment;
import com.pidafacil.pidafacil.fragments.ProductFragment;
import com.pidafacil.pidafacil.fragments.PromoProductFragment;
import com.pidafacil.pidafacil.fragments.RestaurantFragment;
import com.pidafacil.pidafacil.fragments.RestaurantInfoFragment;
import com.pidafacil.pidafacil.fragments.ShippingZonesFragment;
import com.pidafacil.pidafacil.fragments.WhatsAppFragment;
import com.pidafacil.pidafacil.helper.UIHelper;
import com.pidafacil.pidafacil.model.Login;
import com.pidafacil.pidafacil.model.Product;
import com.pidafacil.pidafacil.model.RestaurantRequest;
import com.pidafacil.pidafacil.singleton.UD;
import com.pidafacil.pidafacil.task.VoidTask;
import com.pidafacil.pidafacil.util.NetworkValidator;
import com.pidafacil.pidafacil.util.Utils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.exceptions.RealmMigrationNeededException;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialAccount;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;
import it.neokree.materialnavigationdrawer.elements.listeners.MaterialSectionListener;


/**
 * Created by mauricio on 25/5/15.
 * Actividad principal que extiende de MaterialNavigationDrawer.
 * Su funcion es construir el menu lateral, con los elementos, cuentas asociadas
 * del usuario y otros complementos.
 *
 * Sobre esta actividad estan los fragmentos y tambien
 * direccionamiento a otras actividades.
 */
public class NavigationDrawer extends MaterialNavigationDrawer  {
    public static GoogleAnalytics analytics;
    private Tracker tracker;
    private SearchView searchView;
    private boolean logged = false;
    private Realm realm;
    private List<Suggestion> suggestions;
    private List<Suggestion> tempSuggestions = new ArrayList<>();
    private boolean refreshData;

    private int itemsAdded = 0;
    private TextView itemsAddedQty = null;

    MaterialSection explore, orders, shoppingcart, profile, help, support, zones, tour;

    /**
     * Bloque de construccion, aqui se inician las secciones de menu, se inscancia la base
     * de datos Realm y en caso de error se reconstruye la deficion de las clases de Realm.
     * se apunta al metodo de configuracion y a la funcionalidad para auto-completar del
     * buscador.
     **/
    @Override
    public void init(Bundle savedInstanceState) {
        MaterialAccount account;
        UD.getInstance().setContext(this.getApplicationContext());
        try{
            realm = Realm.getInstance(getApplicationContext());
        }catch (RealmMigrationNeededException e){
            Realm.deleteRealmFile(getApplicationContext());
            realm = Realm.getInstance(getApplicationContext());
        }
        this.allowArrowAnimation();
        this.disableLearningPattern();
        trackNavDrawer();
        session();
        account = new MaterialAccount(this.getResources(), "PidaFacil", "", R.drawable.user_light, R.drawable.header_background);
        this.addAccount(account);
        explore = newSection("Explorar", R.drawable.ic_explore, new ExploreFragment());
        orders = newSection("Repetir pedido",R.drawable.ic_repeat_order,new OrderFragment());
        shoppingcart = newSection("Carrito de Compras", R.drawable.ic_shopping_cart, new Intent(this, ShoppingCartActivity.class));
        support = newSection("Atención al Cliente", R.drawable.ic_chat, new WhatsAppFragment());
        zones = newSection("Zonas de Cobertura", R.drawable.ic_map, new ShippingZonesFragment());
        tour = newSection("¿Cómo Comprar?",R.drawable.ic_help, new Intent(this, ZoomAnimation.class));
        help = newSection(getString(R.string.app_version), this);

        this.addSection(explore);
        this.addSection(orders);
        this.addSection(shoppingcart);
        this.addSection(profile);
        this.addSection(support);
        this.addSection(zones);
        this.addSection(tour);
        this.addBottomSection(help);
        configuration();
        autoComplete();
        appGetFirstTimeRun();
    }

    /**
     * Controlar hacia donde dirigir al usuario al presionar el boton Back
     */
    @Override
    public void onBackPressed() {

        if (getCurrentSection() == explore) {
            Log.d("Seccion", ""+getCurrentSection().toString());
            if( UD.VIEW_ ==0){
                super.onBackPressed();
            }
            if (UD.getInstance().getCurrentFragment() == null) {
                setFragment(new ExploreFragment(), "Explorar");
            } else {
                if (UD.getInstance().getCurrentFragment().equals(RestaurantInfoFragment.class.getSimpleName())) {
                    setFragment(new ExploreFragment(), "Explorar");
                } else if (UD.getInstance().getCurrentFragment().equals(RestaurantFragment.class.getSimpleName())) {
                    setFragment(new ExploreFragment(), "Explorar");
                } else if (UD.getInstance().getCurrentFragment().equals(ProductFragment.class.getSimpleName())) {
                    UD.getInstance().setCurrentFragment(RestaurantInfoFragment.class.getSimpleName());
                    super.onBackPressed();
                } else if (UD.getInstance().getCurrentFragment().equals(DetailFragment.class.getSimpleName())) {
                    UD.getInstance().setCurrentFragment(ProductFragment.class.getSimpleName());
                    super.onBackPressed();
                }else if (UD.getInstance().getCurrentFragment().equals(OrderFragment.class.getSimpleName())) {
                    setFragment(new ExploreFragment(), "Explorar");
                    closeDrawer();
                    super.onBackPressed();
                }
            }
        }
    }

    /**
     * Configuracion de autocompletar del buscador
     */
    void autoComplete(){
        VoidTask task = new VoidTask("/autocomplete");
        task.setPostExecute(new VoidTask.PostExecute() {
            @Override
            public void execute(StringBuilder response) {
                if (response != null) {
                    try {
                        JSONObject data = new JSONObject(response.toString());
                        if (data.getString("status").equals("true")) {
                            suggestions = new ArrayList<Suggestion>();
                            JSONArray arr = data.getJSONArray("data");
                            for (int i = 0; i < arr.length(); i++) {
                                org.json.JSONObject o = arr.getJSONObject(i);
                                suggestions.add(new Suggestion(o.getInt("type"), o.getInt("value"), o.getString("label")));
                            }
                        }
                    } catch (JSONException e) {
                        Log.d("auto:PARSE-ERROR", e.getMessage());
                    }
                }
            }
        });

        task.execute();
    }

    /**
     * Metodo de intercepcion de DeepLink para NavigationDrawer.
     * */
    void deepLinkInterceptor(){
        Intent intent = getIntent();
        Boolean deepLink = intent.getBooleanExtra(DeepLinkParserActivity.IS_DEEPLINK, false);
        if(deepLink!=null && deepLink == true){
            String toView = intent.getStringExtra(DeepLinkParserActivity.TO_VIEW);
            if (toView.equals(RestaurantInfoFragment.class.getName())){
                UD.getInstance().put(UD.RESTAURANT_, String.valueOf(intent.getStringExtra("restaurant_id")));
                UD.getInstance().put(UD.RESTAURANT_NAME_, null);
                // this.setFragmentChild(new RestaurantInfoFragment(), "");
                setFragment(new RestaurantInfoFragment(), "");
                closeDrawer();
            } else if(toView.equals(PromoProductFragment.class.getName()))
            {
                final String restaurant_id = intent.getStringExtra("restaurant_id");
                UD.getInstance().put(UD.RESTAURANT_, restaurant_id);
                UD.getInstance().put(UD.RESTAURANT_NAME_, null);
                this.setFragmentChild(new PromoProductFragment(), "");
                //setFragment(new PromoProductFragment(), "");
                // closeDrawer();
            } else if(toView.equals(OrderFragment.class.getName())) {
                //this.setFragmentChild(new OrderFragment(), "Repetir Pedido");
                setFragment(new OrderFragment(), "Repetir Pedido");
                closeDrawer();

            } else if(toView.equals(LoginActivity.class.getName())) {
                Intent signUp = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(signUp);
            } else if(toView.equals(AddAddressActivity.class.getName())) {
                if (!logged) {
                    Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                    login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(login);
                }else{
                    Intent addAddress = new Intent(getApplicationContext(), AddAddressActivity  .class);
                    startActivity(addAddress);
                    finish();
                }
            } else if(toView.equals(LoginPidaFacil.class.getName())){
                if (!logged) {
                    Intent login = new Intent(getApplicationContext(), LoginPidaFacil.class);
                    login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(login);
                    finish();
                }
            } else if(toView.equals(ShoppingCartActivity.class.getName())){
                Intent shoppingCart = new Intent(getApplicationContext(), ShoppingCartActivity.class);
                shoppingCart.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(shoppingCart);
                finish();
            } else if(toView.equals(DetailFragment.class.getName())){
                VoidTask task = new VoidTask("/product/restaurant");
                task.addParam("product_id", intent.getStringExtra("product_id"));
                task.setPostExecute(new VoidTask.PostExecute() {
                    @Override
                    public void execute(StringBuilder response) {
                        try {
                            JSONObject d = new JSONObject(response.toString());
                            d = d.getJSONObject("data");
                            UD.getInstance().put(UD.RESTAURANT_, d.getString("restaurant_id"));
                            UD.getInstance().put(UD.RESTAURANT_NAME_, d.getString("name"));
                            UD.getInstance().put(UD.PRODUCT_, d.getString("product_id"));
                            UD.getInstance().put(UD.CURRENT_USER_RESTAURANT_REQUEST_,
                                    new UserRequestBean(Integer.valueOf(d.getString("restaurant_id")), ""));
                            UD.getInstance().put(UD.PRODUCT_NAME_, d.getString("product_name"));
                            UD.getInstance().put("logo_uri__", d.getString("restaurant_logo"));
                            //setFragmentChild(new DetailFragment(), d.getString("name"));
                            setFragment(new DetailFragment(), d.getString("name"));
                            closeDrawer();
                        } catch (JSONException e) {
                            Log.d("PARSER-ERR", e.getMessage());
                        }
                    }
                });
                task.execute();
            } else if(toView.equals(RestaurantFragment.class.getName())){
                //this.setFragmentChild(new ExploreFragment(), "");
               // setFragment(new ExploreFragment(), "Repetir Pedido");
                final String id = intent.getStringExtra("category_id");
                UD.getInstance().put(UD.CATEGORY_, id);
                UD.getInstance().put(UD.DEEP_LINK_, id);
              //  this.setFragmentChild(new RestaurantFragment(), "");
                setFragment(new RestaurantFragment(), "");
               closeDrawer();
            } else {
                this.setFragmentChild(new ExploreFragment(), "");
            }
        }
    }

    /**
     * Implementacion de newrelic y Appboy
     * */
    @Override
    protected void onStart() {
        super.onStart();
        NewRelic.withApplicationToken("AAa8a2c43310e51ee07381b62273ab012a2d10baea")
                .start(getApplication());
        deepLinkInterceptor();

        refreshData = Appboy.getInstance(NavigationDrawer.this).openSession(NavigationDrawer.this);

        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);

        tracker = analytics.newTracker("UA-66329309-1");
        tracker.enableExceptionReporting(false);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);

    }

    @Override
    protected void onStop() {
        super.onStop();
        Utils.appboyEvent(Appboy.getInstance(getApplicationContext()), "Closed Session", new Object[]{} );
        Appboy.getInstance(NavigationDrawer.this).closeSession(NavigationDrawer.this);
        Log.d("sesion cerrada","Sesion cerrada");
    }

    /*
    * Construccion del menu superior, carrito y buscador.
    * Aqui se encuentran la configuracion del buscador y
    * del icono del ShoppingCart.
    *
    * Basicamente el searchview tiene metodos que escuchan
    * peticiones del usuario al interactuar con el buscador.
    * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.searchview_).getActionView();
        SearchableInfo info = manager.getSearchableInfo(getComponentName());
        searchView.setSearchableInfo(info);
        searchView.setQueryHint("Pizza, Hamburguesa");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                loadSuggestions(s);
                return true;
            }
        });

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int i) {
                Log.d("INFO", "SuggestionSelected " + i);
                submitSuggestion(i);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onSuggestionClick(int i) {
                Log.d("INFO", "SuggestionClicked " + i);
                submitSuggestion(i);
                searchView.clearFocus();
                return true;
            }
        });

        final View menu_hotlist = menu.findItem(R.id.shopping_car).getActionView();
        itemsAddedQty = (TextView) menu_hotlist.findViewById(R.id.txtItemQty);
        updateCartItemsCount(cartItemsTotal());
        new MenuItemListener(menu_hotlist, getString(R.string.title_shopping_cart)) {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ShoppingCartActivity.class));
            }
        };

        return true;
    }

    public void updateCartItemsCount(final int newItemsAdded) {
        itemsAdded = newItemsAdded;
        if (itemsAddedQty == null) return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (newItemsAdded == 0)
                    itemsAddedQty.setVisibility(View.INVISIBLE);
                else {
                    itemsAddedQty.setVisibility(View.VISIBLE);
                    itemsAddedQty.setText(Integer.toString(newItemsAdded));
                    shoppingcart.setNotifications(newItemsAdded);
                }
            }
        });
    }

    static abstract class MenuItemListener implements View.OnClickListener, View.OnLongClickListener {
        private String hint;
        private View view;

        MenuItemListener(View view, String hint) {
            this.view = view;
            this.hint = hint;
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override abstract public void onClick(View v);

        @Override public boolean onLongClick(View v) {
            final int[] screenPos = new int[2];
            final Rect displayFrame = new Rect();
            view.getLocationOnScreen(screenPos);
            view.getWindowVisibleDisplayFrame(displayFrame);
            final Context context = view.getContext();
            final int width = view.getWidth();
            final int height = view.getHeight();
            final int midy = screenPos[1] + height / 2;
            final int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            Toast cheatSheet = Toast.makeText(context, hint, Toast.LENGTH_SHORT);
            if (midy < displayFrame.height()) {
                cheatSheet.setGravity(Gravity.TOP | Gravity.RIGHT,
                        screenWidth - screenPos[0] - width / 2, height);
            } else {
                cheatSheet.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, height);
            }
            cheatSheet.show();
            return true;
        }
    }

    /*
    * Cuando un elemento sea seleccionado en el menu superior
    * este evento se dispara
    * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /**
     * Metodo de configuracion.
     * Se realiza una verificacion de conexion primero, posteriormente una verificacion de datos
     * en session (base de datos realm local) despues se envia el userId a appboy (correo) y
     * se habilita la suscripcion para recibir notificaciones.
     * */
    void configuration() {
        if(!NetworkValidator.hasConnection(this)){
            AlertDialog.Builder builder =  UIHelper.aceptAlert(NavigationDrawer.this, "Sin conexión a Internet",
                    "Para usar PidaFacil se requiere conexion a internet activa");
            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    finish();
                }
            });
            builder.show();
        }

        if(realm.allObjects(Login.class).size()>0){
            Login login = realm.allObjects(Login.class).first();
            if(login != null){
                if(login.getId()>0){
                    Appboy.getInstance(getApplicationContext()).changeUser(login.getEmail());
                    Appboy.getInstance(getApplicationContext()).getCurrentUser()
                            .setPushNotificationSubscriptionType(NotificationSubscriptionType.SUBSCRIBED);
                }
            }
        }else{
            realm.beginTransaction();
            Login login = realm.createObject(Login.class);
            login.setUserName("Invitado");
            login.setId(0);
            login.setLoginMethod(0);
            realm.commitTransaction();
        }

        explore.setOnClickListener(new MaterialSectionListener() {
            @Override
            public void onClick(MaterialSection materialSection) {
                setFragment(new ExploreFragment(), "Explorar");
                closeDrawer();
            }
        });
    }

    /*
    * Verificacion de sesion.
    * */
    public void session() {
        if(realm.allObjects(Login.class).size()==0){
            logged = false;
            profile = newSection("Iniciar sesión", R.drawable.ic_profile, new Intent(this, LoginActivity.class));
        }else{
            Login login = realm.allObjects(Login.class).first();
            if(login.getId() > 0){
                logged = true;
                profile = newSection("Mi Perfil", R.drawable.ic_profile, new Intent(this, LoginActivity.class));
            }else{
                logged = false;
                profile = newSection("Iniciar sesión", R.drawable.ic_profile, new Intent(this, LoginActivity.class));
            }
        }
    }

    /**
     * Carga las sugerencias del searchview.
     * Despues de recibir el texto escrito por el usuario
     * el searchview envia la expresion que sera
     * estudiada dentro de loadsuggestions() y preparara
     * el adaptar con la matriz para que estas sugerencias
     * sean desplegadas en el buscador.
     * @param expr
     */
    private void loadSuggestions(String expr) {
        String[] columns = new String[] { "_id" , "itemText" };
        MatrixCursor cursor = new MatrixCursor(columns);
        int id_ = 0;

        if(suggestions!=null)
            if(suggestions.size()>0){
                for(Suggestion suggestion: suggestions){
                    boolean match = false;
                    String suggestString = suggestion.getLabel().toString();
                    String[] values = suggestString.split(" ");
                    for(String str: values){
                        if(expr.length()==1){
                            char[] chars = expr.toLowerCase().toCharArray();
                            char[] chars1 = str.toLowerCase().toCharArray();
                            if(chars[0] == chars1[0]){
                                match = true;
                                break;
                            }
                        }
                        if(str.toLowerCase().startsWith(expr.toLowerCase())){
                            match = true;
                            break;
                        }
                    }
                    if(match){
                        if(id_ == 0) tempSuggestions.clear();
                        Object[] row = {id_++, suggestion.getLabel()};
                        Log.d("FIND","Agregando "+ Arrays.toString(row)+ " a las sugerencias ");
                        tempSuggestions.add(suggestion);
                        cursor.addRow(row);
                    }
                }

                searchView.setSuggestionsAdapter(new ItemAdapter(getApplicationContext(), cursor, false));
            }
    }

    /**
     * Envia la sugerencia apartir de la posision
     * seleccionada por el usuario. Este evento
     * disparara un fragmento que mostrara la informacion
     * solicitada por el usuario.
     * @param suggestionPosition
     */
    void submitSuggestion(int suggestionPosition){
        Suggestion suggestion = tempSuggestions.get(suggestionPosition);
        Log.d("SUBMIT", suggestion.toString());
        Fragment fragment;

        if(suggestion.getType() == 1){
            Log.d("SUBMIT","To View: "+suggestion.getLabel());
            fragment = new RestaurantFragment();
            UD.getInstance().put(UD.CATEGORY_, Integer.valueOf(suggestion.getValue()));
        }else{
            Log.d("SUBMIT","To View: "+suggestion.getLabel());
            fragment = new RestaurantInfoFragment();
            UD.getInstance().put(UD.RESTAURANT_, String.valueOf(suggestion.getValue()));
            UD.getInstance().put(UD.RESTAURANT_NAME_, String.valueOf(suggestion.getLabel()));
        }

        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        for(int i=0; i<fragments.size(); i++){
            Fragment f = fragments.get(i);
            if(f instanceof DetailFragment || f instanceof RestaurantInfoFragment || f instanceof ProductFragment
                    || f instanceof PromoProductFragment ){
                tx.remove(f);
            }
        }
        tx.commit();
        this.setFragmentChild(fragment, suggestion.getLabel());
        Utils.appboyEvent(Appboy.getInstance(getApplicationContext()), "Search", new Object[]{suggestion.getLabel()} );
    }

    /**
     * El onResume() nos sirve para verificar cambios en elementos
     * de sesion del usuario y tambien una redireccion al MainFragment
     * en caso de que el usuario venga del ShoppingCar
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Actualizar el contador de elementos en el carrito
        updateCartItemsCount(cartItemsTotal());

        Boolean tomain = (Boolean) UD.getInstance().get("to_main");
        // Muestra actividad principal en caso de que haya enviado el pedido.
        if(tomain != null && tomain == true){
            setFragment(new ExploreFragment(), "Explorar");
            UD.getInstance().put("to_main", null);
        }

        // Cuenta en menu lateral
        try{
            if(realm.allObjects(Login.class).size()>0) {
                final Login login = realm.allObjects(Login.class).first();
                if(login.getId()>0){
                    if(login.isPhoto()){
                        Target target = new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                MaterialAccount account = new MaterialAccount(getResources(),
                                        login.getUserName().concat(" ").concat(login.getUserLastName()),
                                        login.getEmail(),bitmap,R.drawable.header_background);
                                addAccount(account);
                                if(getAccountList().size()>1){
                                    removeAccount(getCurrentAccount());
                                }
                            }
                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) { }
                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) { }
                        };
                        Log.d("MND-INFO", "User photo: "+login.getPhotoUri());
                        Picasso.with(getApplicationContext()).load(login.getPhotoUri()).into(target);
                    }else{
                        MaterialAccount account = new MaterialAccount(getResources(),login.getUserName().concat(" ").concat(login.getUserLastName()),
                                login.getEmail(),R.drawable.user_light,R.drawable.header_background);
                        addAccount(account);
                        if(getAccountList().size()>1){
                            removeAccount(getCurrentAccount());
                        }
                    }
                }
            }
        } catch(NullPointerException nullpointer){
            this.addAccount(new MaterialAccount(this.getResources(), "PidaFacil", "", R.drawable.user_light, R.drawable.header_background));
            if(this.getAccountList().size()>1){
                this.removeAccount(this.getCurrentAccount());
            }
        }

        // Buscando inAppMessages
        AppboyInAppMessageManager.getInstance().registerInAppMessageManager(this);
        if(refreshData){
            Appboy.getInstance(NavigationDrawer.this).requestInAppMessageRefresh();
            refreshData = false;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public Tracker getTracker() {
        if(tracker==null)
            tracker = ((Application) super.getApplication()).getDefaultTracker();
        return tracker;
    }

    private void trackNavDrawer() {
        getTracker().enableAutoActivityTracking(true);
        getTracker().enableAdvertisingIdCollection(true);
        getTracker().setScreenName("Image- " + NavigationDrawer.class.getName());
        getTracker().send(new HitBuilders.ScreenViewBuilder().build());
        getTracker().send(new HitBuilders.EventBuilder()
                .setCategory("Show Nav Drawer")
                .setAction("show")
                .build());
    }

    private Integer cartItemsTotal() {
        RealmResults<RestaurantRequest> l = realm.allObjects(RestaurantRequest.class);
        if (l.size() > 0) {
            RestaurantRequest bean = l.first();
            RealmList<Product> rl = bean.getProducts();
            return rl.size();
        } else {
            return  0;
        }
    }

    public void startZoomAnimation(){
        Intent intent = new Intent(getApplicationContext(), ZoomAnimation.class);
        startActivity(intent);
        finish();
    }
    private int appGetFirstTimeRun() {
        //Check if App Start First Time
        SharedPreferences appPreferences = getSharedPreferences("MyAPP", 0);
        int appCurrentBuildVersion = BuildConfig.VERSION_CODE;
        int appLastBuildVersion = appPreferences.getInt("app_first_time", 0);

        Log.d("appPreferences", "app_first_time = " + appLastBuildVersion);

        if (appLastBuildVersion == appCurrentBuildVersion ) {
            return 1; //ya has iniciado la app alguna vez

        } else {
            appPreferences.edit().putInt("app_first_time",
                    appCurrentBuildVersion).apply();
            if (appLastBuildVersion == 0) {
                Log.d("Primera vez:",""+appLastBuildVersion);
                startZoomAnimation();
                return 0; //es la primera vez

            } else {
                Log.d("Version  nueva:",""+appLastBuildVersion);
               // startZoomAnimation();
                return 2; //es una versión nueva
            }
        }
    }


}
