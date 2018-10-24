package com.pidafacil.pidafacil.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.pidafacil.pidafacil.NavigationDrawer;
import com.pidafacil.pidafacil.fragments.DetailFragment;
import com.pidafacil.pidafacil.fragments.OrderFragment;
import com.pidafacil.pidafacil.fragments.PromoProductFragment;
import com.pidafacil.pidafacil.fragments.RestaurantFragment;
import com.pidafacil.pidafacil.fragments.RestaurantInfoFragment;
import com.pidafacil.pidafacil.model.Login;

/**
 * Created by victor on 07-16-15.
 * DeepLinkParser interpreta cada uno de los deeplinks
 * los procesa y compara para redireccionar a las vistas
 * dentro de pidafacil.
 */
public class DeepLinkParserActivity extends Activity {
    final String TAG = "LINKPARSER";
    public static final String CATEGORY_VIEW_EXPR = "/sect";
    public static final String PRODUCT_VIEW_EXPR = "/product";
    public static final String RES_VIEW_EXPR = "/rest";
    public static final String PROM_VIEW_EXPR = "/prom";
    public static final String ORDERS_VIEW_EXPR = "/repetir_pedido";
    public static final String LOGIN_VIEW = "/login_correo";
    public static final String SIGN_UP_VIEW = "/login";
    public static final String CART_VIEW = "/carrito";
    public static final String IS_DEEPLINK = "is_deeplink";
    public static final String TO_VIEW = "to_view";
    public static final String ADD_ADDRESS = "/add_address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if(intent == null && intent.getData() == null)
            finish();

        Log.d(TAG, "URI -> " + intent.getData().toString());
        Log.d(TAG, "PATH " + intent.getData().getPath());
        Uri data = intent.getData();
        String path = data.getPath();

        if(path.contains(RES_VIEW_EXPR)) {
            // /res/{id}
            String[] url = path.split("/");
            String id = url[2];
            Intent intent0 = new Intent(getApplicationContext(), NavigationDrawer.class);
            intent0.putExtra(IS_DEEPLINK, true);
            intent0.putExtra(TO_VIEW, RestaurantInfoFragment.class.getName());
            intent0.putExtra("restaurant_id", id);
            startActivity(intent0);
        } else if(path.contains(PRODUCT_VIEW_EXPR)) {
            // /prod/{id}
            String[] url = path.split("/");
            String id = url[2];
            Intent intent0 = new Intent(getApplicationContext(), NavigationDrawer.class);
            intent0.putExtra(IS_DEEPLINK, true);
            intent0.putExtra(TO_VIEW, DetailFragment.class.getName());
            intent0.putExtra("product_id", id);
            startActivity(intent0);
        } else if(path.contains(PROM_VIEW_EXPR)) {
            // /prom/{res_id}
            String[] url = path.split("/");
            String id = url[2];
            Intent intent0 = new Intent(getApplicationContext(), NavigationDrawer.class);
            intent0.putExtra(IS_DEEPLINK, true);
            intent0.putExtra(TO_VIEW, PromoProductFragment.class.getName());
            intent0.putExtra("restaurant_id", id);
            intent0.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent0);
            finish();
        } else if(path.contains(ORDERS_VIEW_EXPR)) {
            // /myorders/
            Intent intent0 = new Intent(getApplicationContext(), NavigationDrawer.class);
            intent0.putExtra(IS_DEEPLINK, true);
            intent0.putExtra(TO_VIEW, OrderFragment.class.getName());
            startActivity(intent0);
        } else if(path.contains(CATEGORY_VIEW_EXPR)){
            String[] url = path.split("/");
            String id = url[2];
            Intent intent0 = new Intent(getApplicationContext(), NavigationDrawer.class);
            intent0.putExtra(IS_DEEPLINK, true);
            intent0.putExtra(TO_VIEW, RestaurantFragment.class.getName());
            intent0.putExtra("category_id", id);
            startActivity(intent0);
        } else if(path.contains(LOGIN_VIEW)) {
            // /login/
            Intent intent0 = new Intent(getApplicationContext(), NavigationDrawer.class);
            intent0.putExtra(IS_DEEPLINK, true);
            intent0.putExtra(TO_VIEW, LoginPidaFacil.class.getName());
            startActivity(intent0);
        } else if(path.contains(SIGN_UP_VIEW)) {
            // /sign_up/
            Intent intent0 = new Intent(getApplicationContext(), NavigationDrawer.class);
            intent0.putExtra(IS_DEEPLINK, true);
            intent0.putExtra(TO_VIEW, LoginActivity.class.getName());
            startActivity(intent0);
        } else if(path.contains(CART_VIEW)) {
            // /cart/
            Intent intent0 = new Intent(getApplicationContext(), NavigationDrawer.class);
            intent0.putExtra(IS_DEEPLINK, true);
            intent0.putExtra(TO_VIEW, ShoppingCartActivity.class.getName());
            startActivity(intent0);
        } else if(path.contains(ADD_ADDRESS)) {
            // /add_address/
            Intent intent0 = new Intent(getApplicationContext(), NavigationDrawer.class);
            intent0.putExtra(IS_DEEPLINK, true);
            intent0.putExtra(TO_VIEW, AddAddressActivity.class.getName());
            startActivity(intent0);
            finish();
        } else {
            startActivity(new Intent(getApplicationContext(), NavigationDrawer.class));
        }

        finish();
    }
}
