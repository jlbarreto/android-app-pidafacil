package com.pidafacil.pidafacil.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appboy.Appboy;
import com.pidafacil.pidafacil.NavigationDrawer;
import com.pidafacil.pidafacil.R;
import com.pidafacil.pidafacil.components.RequestAdapter;
import com.pidafacil.pidafacil.helper.UIHelper;
import com.pidafacil.pidafacil.model.Login;
import com.pidafacil.pidafacil.model.Product;
import com.pidafacil.pidafacil.model.RestaurantRequest;
import com.pidafacil.pidafacil.singleton.UD;
import com.pidafacil.pidafacil.util.Parser;
import com.pidafacil.pidafacil.components.CustomLinearLayoutManager;
import com.pidafacil.pidafacil.util.Utils;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class ShoppingCartActivity extends ActionBarActivity {

    private RecyclerView recyclerView;
    private Button buttonsend;
    private Realm realm;
    private TextView txt_total;
    private TextView txt_rname;
    private LinearLayout lay_res;
    private Appboy appboy;
    private TextView num_whatsapp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_shoppingcart);

        Boolean tomain = (Boolean) UD.getInstance().get("to_main");

        appboy = Appboy.getInstance(this);

        if(tomain!=null){
            if(tomain == true)
                NavUtils.navigateUpFromSameTask(this);
            UD.getInstance().put("to_main", false);
        }

        configure();
        setTitle(getString(R.string.title_shopping_cart));
        realm = Realm.getInstance(this);
        RealmResults<RestaurantRequest> l = realm.allObjects(RestaurantRequest.class);
        Dialog d = new Dialog(ShoppingCartActivity.this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.dialog_prod_detail_req);
        UD.getInstance().put("D_SHOW", d);

        boolean products = true;

        if (l.size() > 0) {
            RestaurantRequest bean = l.first();
            RealmList<Product> rl = bean.getProducts();
            total(rl);
            recyclerView.setAdapter(new RequestAdapter(rl, this));
        } else {
            products = false;
        }

        if (!products) {
            buttonsend.setVisibility(View.GONE);
            AlertDialog.Builder b = UIHelper.alert(ShoppingCartActivity.this,
                    "Aún no has agregado productos al carrito de compras",
                    "Dirigete a Explorar para seleccionar tus productos.");
            b.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent homeIntent = new Intent(getApplicationContext(), NavigationDrawer.class);
                    homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(homeIntent);
                    finish();
                    onBackPressed();
                }
            });
            b.show();
        }

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    boolean validate = false;

    @Override
    protected void onPause() {
        super.onPause();
        Utils.appboyEvent(appboy, getString(R.string.appboy_card_abandoned), new Object[]{});
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(validate){
            if(realm.allObjects(RestaurantRequest.class).size()==0){
                end();
            }
        } else {
            validate = true;
            Utils.appboyEvent(Appboy.getInstance(getApplicationContext()),
                    getString(R.string.appboy_go_cart),
                    new Object[]{});
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                end();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void configure() {
        buttonsend = (Button) findViewById(R.id.button_send);
        txt_total = (TextView) findViewById(R.id.txt_total);
        txt_rname = (TextView) findViewById(R.id.txt_restaurant_name_);
        lay_res = (LinearLayout) findViewById(R.id.lay_res_);
        num_whatsapp = (TextView) findViewById(R.id.txtNumWhatsapp);
        CustomLinearLayoutManager llm = new CustomLinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView = (RecyclerView) findViewById(R.id.rec_request_products);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(llm);
        buttons();
        dimmensions();



    }

    void dimmensions() {
        int height = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight();
        int recyclerViewHeight = ((int) (height * 0.65));
        ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
        params.height = recyclerViewHeight;
        recyclerView.setLayoutParams(params);
    }

    void buttons() {
        (buttonsend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UD.getInstance().put(UD.PAYMTH_DETAILS, null);
                UD.getInstance().put(UD.DATA_SEL_PAYM, null);
                UD.getInstance().put(UD.DATA_SEL_TSERV, null);
                UD.getInstance().put(UD.DATA_SEL_ADDR, null);
                UD.SEL_TSER = -1;
                UD.SEL_ADDR = -1;

                RealmResults<RestaurantRequest> l = realm.allObjects(RestaurantRequest.class);
                if (l.size() > 0) {
                    Login lo;
                    boolean notLogged = false;

                    try{
                        lo = realm.allObjects(Login.class).first();
                        if (lo.getId() <= 0) {
                            notLogged = true;
                        }
                    }catch (ArrayIndexOutOfBoundsException e){
                        notLogged = true;
                    }

                    if (!notLogged) {

                        // Registrar evento en Appboy
                        Utils.appboyEvent(Appboy.getInstance(getApplicationContext()), "Processing Order", new Object[]{});
                        // Proceder a la compra
                        CompleteRequestActivity.SERVICE_TYPE = -1;
                        startActivity(new Intent(v.getContext(), CompleteRequestActivity.class));
                    } else {

                        /*
                            Enviar a inicio de sesion si usuario no ha iniciado sesion
                            o no esta registrado
                         */
                        UD.getInstance().put("shopping_back__", true);
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finish();
                    }
                } else {
                    notData();
                }
            }
        });
    }

    public void notData() {
        recyclerView.setVisibility(View.GONE);
        buttonsend.setVisibility(View.GONE);
        txt_total.setVisibility(View.GONE);
        lay_res.setVisibility(View.GONE);
    }

    public void total(RealmList<Product> rl) {
        float value = (float) 0.0;
        for (int i = 0; i < rl.size(); i++) {
            value += (rl.get(i).getValue() * rl.get(i).getQuantity());
        }
        RestaurantRequest req = realm.allObjects(RestaurantRequest.class).first();
        UD.getInstance().put("total__",Parser.decimalFormatString(value));
        txt_total.setText("Total: $" + Parser.decimalFormatString(value));
        txt_rname.setText(req.getName());
        num_whatsapp.setText(UD.NUM_ATENCION_CLIENTE);
    }

    public void reloadProducts() {
        RestaurantRequest req =  realm.allObjects(RestaurantRequest.class).first();
        recyclerView.setAdapter(new RequestAdapter(req.getProducts(), this));
    }

    void end(){
        Intent homeIntent = new Intent(getApplicationContext(), NavigationDrawer.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }

}