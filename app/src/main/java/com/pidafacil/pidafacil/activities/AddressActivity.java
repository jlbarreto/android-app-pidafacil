package com.pidafacil.pidafacil.activities;

import android.app.AlertDialog;
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
import android.widget.LinearLayout;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.pidafacil.pidafacil.R;
import com.pidafacil.pidafacil.beans.AddressBean;
import com.pidafacil.pidafacil.beans.SimpleParam;
import com.pidafacil.pidafacil.components.AddressAdapter;
import com.pidafacil.pidafacil.helper.UIHelper;
import com.pidafacil.pidafacil.model.Login;
import com.pidafacil.pidafacil.singleton.UD;
import com.pidafacil.pidafacil.task.ExecutionMethod;
import com.pidafacil.pidafacil.task.VoidActivityTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class AddressActivity extends ActionBarActivity implements ExecutionMethod {

    private LinearLayout loadingContainer;
    private RecyclerView rec_address;

    public static List addressBeans = new ArrayList();
    private static int ADDRESS_EDIT = 666;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_address);

        rec_address = (RecyclerView) findViewById(R.id.rec_addresses);

        loadingContainer = (LinearLayout) findViewById(R.id.loadingContainer);

        CircularProgressView loading = (CircularProgressView) findViewById(R.id.loading_);

        loading.startAnimation();

        rec_address.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());

        llm.setOrientation(LinearLayoutManager.VERTICAL);

        rec_address.setLayoutManager(llm);

        AlertDialog.Builder alert =
                UIHelper.alert(AddressActivity.this, "Eliminar dirección", "¿Estas seguro que deseas eliminar esta dirección?");

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        UD.getInstance().put("alert_del", alert);

        setTitle("Mis direcciones");

        load();

    }

    public void load(){
        List<SimpleParam> params = new ArrayList<SimpleParam>();
        params.add(new SimpleParam("user_id",
                String.valueOf(Realm.getInstance(getApplicationContext()).allObjects(Login.class).first().getId())));

        VoidActivityTask task = new VoidActivityTask("/user/address", params, this, 0) {
            @Override
            public void execute(StringBuilder result) {
                JSONObject data = null;
                try {
                    data = new JSONObject(result.toString());
                    if(data.getString("status").equals("true")){
                        JSONArray arr = data.getJSONArray("data");
                        if(arr.length() > 0){
                            this.resultList = new ArrayList();
                            for(int i = 0; i<arr.length(); i++){
                                data = arr.getJSONObject(i);
                                AddressBean bean = new AddressBean();
                                bean.setId(data.getInt("address_id"));
                                bean.setName(data.getString("address_name"));
                                bean.setAddress1(data.getString("address_1"));
                                bean.setAddress2(data.getString("address_2"));
                                this.resultList.add(bean);
                            }
                        }
                    }
                } catch (JSONException e) {
                    Log.d("JSON-ERR",e.getMessage());
                }
            }
        };

        task.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_address_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.ab_add_address) {
            addAddress();
        }else if(id == R.id.menu_principal){
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void executeResult(List list, int operationCode) {
        if(list!=null) {
            if(list.size()>0) {
                addressBeans = list;
                this.rec_address.setAdapter(new AddressAdapter(addressBeans, this));
                notLoading();
            } else {
                notDirs();
            }
        } else {
            notDirs();
        }
    }

    void addAddress() {
        UD.TYPE_VIEW = 2;
        startActivityForResult(new Intent(getApplicationContext(), AddAddressActivity.class), AddAddressActivity.ADD_ADDRESS_REQ_CODE);
    }

    void showLoad(){
        rec_address.setVisibility(View.GONE);
        loadingContainer.setVisibility(View.VISIBLE);
    }

    void notLoading(){
        rec_address.setVisibility(View.VISIBLE);
        loadingContainer.setVisibility(View.GONE);
    }

    void notDirs(){
        notLoading();
        rec_address.setVisibility(View.GONE);

        AlertDialog.Builder dialog =
                UIHelper.alert(this,
                        "Agregar dirección",
                        "Aun no has agregado una dirección de envío.");
        dialog
                .setCancelable(false)
                .setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        addAddress();
                        dialog.dismiss();
                    }
                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ADDRESS_EDIT){
            showLoad();
            load();
        }
        if(requestCode == AddAddressActivity.ADD_ADDRESS_REQ_CODE){
            showLoad();
            load();
        }

    }

    public void orderElements() {
        rec_address.setAdapter(new AddressAdapter(addressBeans, this));
    }

    public void edit() {
        startActivityForResult(new Intent(getApplicationContext(), AddressDetailActivity.class), ADDRESS_EDIT);
    }

}
