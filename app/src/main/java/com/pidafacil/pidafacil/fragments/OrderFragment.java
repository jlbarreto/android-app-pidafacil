package com.pidafacil.pidafacil.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.pidafacil.pidafacil.R;
import com.pidafacil.pidafacil.activities.ShoppingCartActivity;
import com.pidafacil.pidafacil.components.OrderAdapter;
import com.pidafacil.pidafacil.helper.UIHelper;
import com.pidafacil.pidafacil.helper.WebServiceHelper;
import com.pidafacil.pidafacil.model.Login;
import com.pidafacil.pidafacil.singleton.UD;
import com.pidafacil.pidafacil.task.ExecutionMethod;
import com.pidafacil.pidafacil.task.VoidActivityTask;

import java.util.List;

import io.realm.Realm;

/**
 * Created by mauricio on 27/5/15.
 */
public class OrderFragment extends Fragment implements ExecutionMethod {
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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        configure(view);
        asynch();
        UD.VIEW_ = 4;
        return view;
    }

    @Override
    public void executeResult(List list, int operationCode) {
        isLooking = false;
        if(operationCode == 0){
            if(list!=null)
                if(list.size()>0){
                    e = list;
                    recyclerViewOrders.setAdapter(new OrderAdapter(list, getActivity().getApplicationContext()));
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
                    recyclerViewOrders.setAdapter(new OrderAdapter(e, getActivity().getApplicationContext()));
                }
        }
    }

    public void configure(View view){
        login = Realm.getInstance(
                getActivity().getApplicationContext()).allObjects(Login.class).first();
        loading_ = (CircularProgressView) view.findViewById(R.id.loading_);
        loading_.startAnimation();
        llm = new LinearLayoutManager(getActivity().getApplicationContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        page_size = getString(R.string.pagination_page_size);
        recyclerViewOrders = (RecyclerView) view.findViewById(R.id.rec_orders);
        recyclerViewOrders.setHasFixedSize(true);
        recyclerViewOrders.setLayoutManager(llm);
        message_  = (TextView) view.findViewById(R.id.__message__);
        events();
        dialogs();
    }

    private void dialogs() {
        AlertDialog.Builder b = UIHelper.alert(getActivity(), null, "Nuevo producto agregado al carrito.");
        b.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startActivity(new Intent(getActivity().getApplicationContext(), ShoppingCartActivity.class));
            }
        });
        UD.getInstance().put("order_dialog", b);
        UD.getInstance().put("order_dialog1",UIHelper.alert(getActivity(), null,
                "Tienes una orden pendiente de procesar. \n Si agregas los productos eliminarías la orden anterior."));
        UD.getInstance().put("order_dialog2",UIHelper.alert(getActivity(), null,
                "¿Deseas agregar estos productos al carrito?"));
    }

    void events(){
        recyclerViewOrders.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                final int visibleItemCount = llm.getChildCount();
                final int totalItemCount = llm.getItemCount();
                final int pastVisiblesItems = llm.findFirstVisibleItemPosition();

                if(!isLooking)
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
            message_.setText("Debes iniciar sesión para ver tus pedidos");
            message_.setVisibility(View.VISIBLE);
            recyclerViewOrders.setVisibility(View.GONE);

        }
    }

    public void noData(){
        message_.setText("Hasta el momento no has realizado ningún pedido");
        message_.setVisibility(View.VISIBLE);
    }
}
