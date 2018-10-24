package com.pidafacil.pidafacil.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.pidafacil.pidafacil.NavigationDrawer;
import com.pidafacil.pidafacil.R;
import com.pidafacil.pidafacil.components.OrderAdapter;
import com.pidafacil.pidafacil.fragments.ExploreFragment;
import com.pidafacil.pidafacil.helper.UIHelper;
import com.pidafacil.pidafacil.helper.WebServiceHelper;
import com.pidafacil.pidafacil.model.Login;
import com.pidafacil.pidafacil.singleton.UD;
import com.pidafacil.pidafacil.task.ExecutionMethod;
import com.pidafacil.pidafacil.task.VoidActivityTask;

import java.util.List;

import io.realm.Realm;

public class OrderActivity extends ActionBarActivity implements ExecutionMethod{
    private RecyclerView recyclerViewOrders;
    private LinearLayoutManager llm;
    private TextView message_;
    private String page_size;
    private int page_post = 0;
    private List e;
    private boolean isLooking = false;
    private Login login;
    private int user_id = 0;
    private CircularProgressView loading_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        setTitle("Tus pedidos");
        configure();
        asynch();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.shopping_car){
            startActivity(new Intent(getApplicationContext(), ShoppingCartActivity.class));
        }
        if(id == R.id.menu_principal){
         goToMain();
        }

        return super.onOptionsItemSelected(item);
    }

    public void configure(){
        login = Realm.getInstance(
                getApplicationContext()).allObjects(Login.class).first();
        loading_ = (CircularProgressView) findViewById(R.id.loading_);
        loading_.startAnimation();
        llm = new LinearLayoutManager(getApplicationContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        Dialog d = new Dialog(OrderActivity.this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.dialog_order_detail_);
        UD.getInstance().put("D_SHOW", d);
        page_size = getString(R.string.pagination_page_size);
        recyclerViewOrders = (RecyclerView) findViewById(R.id.rec_orders);
        recyclerViewOrders.setHasFixedSize(true);
        recyclerViewOrders.setLayoutManager(llm);
        message_  = (TextView) findViewById(R.id.__message__);
        events();
        dialogs();
    }

    private void dialogs() {
        AlertDialog.Builder b = UIHelper.alert(OrderActivity.this, null, "Nuevo producto agregado al carrito.");
        b.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startActivity(new Intent(getApplicationContext(), ShoppingCartActivity.class));
            }
        });
        UD.getInstance().put("order_dialog", b);
        UD.getInstance().put("order_dialog1",UIHelper.alert(OrderActivity.this, null,
                "Tienes una orden pendiente de procesar. \n Si agregas los productos eliminarías la orden anterior."));
        UD.getInstance().put("order_dialog2",UIHelper.alert(OrderActivity.this, null,
                "¿Deseas agregar estos productos al carrito?"));
    }

    void events(){
        recyclerViewOrders.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                final int visibleItemCount = llm.getChildCount();
                final int totalItemCount = llm.getItemCount();
                final int pastVisiblesItems = llm.findFirstVisibleItemPosition();
                if (!isLooking)
                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        Log.d("INFO", " Cargando...");
                        isLooking = true;
                        paginate();
                    }
            }
        });
    }

    private void paginate() {
        VoidActivityTask task = WebServiceHelper.orderQuery(this, 1);
        task.addParam("user_id",String.valueOf(user_id));
        task.addParam("page_size",page_size);
        task.addParam("page_post", String.valueOf(page_post++));
        task.execute();
    }

    void asynch(){
        user_id = login.getId();
        if(user_id > 0){
            VoidActivityTask task = WebServiceHelper.orderQuery(this, 0);
            task.addParam("user_id",String.valueOf(user_id));
            task.addParam("page_size",page_size);
            task.addParam("page_post", String.valueOf(page_post++));
            task.execute();
        }else{
            message_.setText("Debes iniciar sesión para ver tus pedidos.");
            message_.setVisibility(View.VISIBLE);
            recyclerViewOrders.setVisibility(View.GONE);
        }
    }

    @Override
    public void executeResult(List list, int operationCode) {
        isLooking = false;
        if(operationCode == 0){
            if(list!=null)
                if(list.size()>0){
                    e = list;
                    recyclerViewOrders.setAdapter(new OrderAdapter(list, getApplicationContext()));
                    loading_.setVisibility(View.GONE);
                }else{
                    noData();
                }
            else
                noData();
        }else{
            if(list!=null)
                if(list.size()>0){
                    for(Object o: list)
                        e.add(o);
                    recyclerViewOrders.setAdapter(new OrderAdapter(e,getApplicationContext()));
                }
        }
    }

    public void noData(){
        message_.setVisibility(View.VISIBLE);
        loading_.setVisibility(View.GONE);
    }

    private void goToMain() {
        Intent homeIntent = new Intent(getApplicationContext(), NavigationDrawer.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
        finish();
    }



}
