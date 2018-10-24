package com.pidafacil.pidafacil.fragments;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.pidafacil.pidafacil.R;
import com.pidafacil.pidafacil.beans.RestaurantInfo;
import com.pidafacil.pidafacil.components.ProductAdapter;
import com.pidafacil.pidafacil.helper.WebServiceHelper;
import com.pidafacil.pidafacil.singleton.UD;
import com.pidafacil.pidafacil.task.ExecutionMethod;
import com.pidafacil.pidafacil.task.VoidActivityTask;
import com.pidafacil.pidafacil.task.VoidTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

/**
 * Created by victor on 05-28-15.
 */
public class PromoProductFragment extends Fragment implements ExecutionMethod {
    private RecyclerView recyclerView;
    private LinearLayoutManager llm;
    private ImageView loading;
    private CircularProgressView loading_;
    private TextView message_;
    private String restaurant_id = String.valueOf(UD.getInstance().get(UD.RESTAURANT_));
    private String page_size;
    private int page_post = 0;
    private boolean isLooking = false;
    private List e;
    private Button buttonRestaurant;
    private TextView textPromo;
    private String restaurantName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_product, container, false);
        restauranteImage();
        UD.getInstance().put("product_fragment_", this);
        restaurantName = (String) UD.getInstance().get(UD.RESTAURANT_NAME_);
        loading_ = (CircularProgressView) v.findViewById(R.id.loading_);
        loading_.startAnimation();
        message_ = (TextView) v.findViewById(R.id.__message__);
        configure(v);
        load();
        UD.VIEW_ = 4;
        return v;
    }

    private void restauranteImage() {
        VoidTask task = new VoidTask("/restaurant/get");
        task.addParam("restaurant_id", restaurant_id);
        task.setPostExecute(new VoidTask.PostExecute() {
            @Override
            public void execute(StringBuilder response) {
                try {
                    if (response != null) {
                        Log.d("INFO", "Guardando imagen alternativa");
                        RestaurantInfo b = new RestaurantInfo()
                                .parseObject(new JSONObject(response.toString()));
                        UD.getInstance().put("logo_uri__", b.getImgUriLogo());
                        if (restaurantName == null) {
                            restaurantName = b.getName();
                            getActivity().setTitle(restaurantName);
                        }
                    }
                } catch (JSONException e1) {
                }
            }
        });
        task.execute();
    }

    public void configure(View v){
        buttonRestaurant = (Button) v.findViewById(R.id.button_restaurant);
        page_size = getString(R.string.pagination_page_size);
        textPromo = (TextView) v.findViewById(R.id.txt_promo);
        recyclerView = (RecyclerView) v.findViewById(R.id.rec_products);
        recyclerView.setHasFixedSize(true);
        llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
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

        buttonRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toRestaurant();
            }
        });
    }

    void toRestaurant(){
        MaterialNavigationDrawer parent = (MaterialNavigationDrawer) getActivity();
        parent.setFragmentChild(new RestaurantInfoFragment(), restaurantName);
    }

    void paginate(){
        VoidActivityTask taskActivity = WebServiceHelper.getPromoQuery(this, 1);
        taskActivity.addParam("restaurant_id",restaurant_id);
        taskActivity.addParam("page_size", page_size);
        taskActivity.addParam("page_post", String.valueOf(page_post++) );
        taskActivity.execute();
    }

    void load(){
        if(e!=null){
            ProductAdapter productAdapter = new ProductAdapter(e);
            loading_.setVisibility(View.GONE);
            recyclerView.setAdapter(productAdapter);
            dimmensions();
        }else{
            VoidActivityTask taskActivity = WebServiceHelper.getPromoQuery(this, 0);
            taskActivity.addParam("restaurant_id",restaurant_id);
            taskActivity.addParam("page_size",page_size);
            taskActivity.addParam("page_post", String.valueOf(page_post++));
            taskActivity.execute();
        }
    }

    void errMessage(){
        message_.setText("No se encontraron elementos..");
        recyclerView.setVisibility(View.GONE);
        loading_.setVisibility(View.GONE);
        message_.setVisibility(View.VISIBLE);
    }

    @Override
    public void executeResult(List list, int operationCode) {
        if(operationCode == 0){
            if(list != null)
                if(list.size()>0){
                    e = list;
                    ProductAdapter productAdapter = new ProductAdapter(list);
                    loading_.setVisibility(View.GONE);
                    recyclerView.setAdapter(productAdapter);
                    dimmensions();
                }else
                    errMessage();
            else
                errMessage();
        }else{
            if(list != null)
                if(list.size()>0) {
                    for (Object o : list)
                        e.add(o);
                    ProductAdapter productAdapter = new ProductAdapter(e);
                    recyclerView.setAdapter(productAdapter);
                }
        }
    }

    void dimmensions(){
        buttonRestaurant.setVisibility(View.VISIBLE);
        textPromo.setVisibility(View.VISIBLE);
    }

    public void goToDetail(String product){
        MaterialNavigationDrawer parent = (MaterialNavigationDrawer) getActivity();
        parent.setFragmentChild(new DetailFragment(), product );
    }
}
