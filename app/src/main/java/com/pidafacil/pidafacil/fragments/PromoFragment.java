package com.pidafacil.pidafacil.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.pidafacil.pidafacil.R;
import com.pidafacil.pidafacil.beans.CategoryBean;
import com.pidafacil.pidafacil.components.CustomLinearLayoutManager;
import com.pidafacil.pidafacil.components.RecyGridCategoryAdapter;
import com.pidafacil.pidafacil.components.RestaurantAdapter;
import com.pidafacil.pidafacil.helper.WebServiceHelper;
import com.pidafacil.pidafacil.icon.IconMapping;
import com.pidafacil.pidafacil.singleton.UD;
import com.pidafacil.pidafacil.task.ExecutionMethod;
import com.pidafacil.pidafacil.task.VoidActivityTask;
import com.pidafacil.pidafacil.task.VoidTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

/**
 * Created by mauricio on 26/5/15.
 */
public class PromoFragment extends Fragment implements ExecutionMethod {
    private RecyclerView recyclerViewTypes;
    private RecyclerView recyclerViewMoods;
    private RecyclerView recyclerViewRestaurants;
    private int page_post = 0;
    private String page_size = "5";
    private List results;
    private CustomLinearLayoutManager cmanager;
    private CircularProgressView loading_;
    private TextView message;
    boolean isLooking = false;
    private UD U = UD.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("Master-Child","Child created");
        UD.VIEW_ = 1;
        View view = inflater.inflate(R.layout.fragment_promo, container, false);
        configure(view);

        if(U.get("cat_list_1") == null || U.get("cat_list_2") == null)
            configureRecyclers();
        else {
            List<CategoryBean> l = (List<CategoryBean>) U.get("cat_list_1");
            RecyGridCategoryAdapter ca = new RecyGridCategoryAdapter(l);
            recyclerViewTypes.setAdapter(ca);
            l = (List<CategoryBean>) U.get("cat_list_2");
            U.get("cat_list_2");
            RecyGridCategoryAdapter ca0 = new RecyGridCategoryAdapter(l);
            recyclerViewMoods.setAdapter(ca0);
            query(null);
        }
        U.put("promo_fragment_", this);
        return view;
    }

    @Override
    public void executeResult(List list, int operationCode) {

        if(operationCode == 0)
            if(list == null){
                notLoading();
                notElements();
                return;
            }else if(list.size()<0){
                notLoading();
                notElements();
                return;
            }

        if(operationCode == 0){
            results = list;
            recyclerViewRestaurants.setAdapter(new RestaurantAdapter(results));
            notLoading();
        }else if(operationCode==1){
            if(list!=null)
            for(Object o : list) results.add(o);
            recyclerViewRestaurants.setAdapter(new RestaurantAdapter(results));
            notLoading();
        }
    }

    void configureRecyclers(){
        VoidTask task = new VoidTask("/restaurant/categories");
        task.addParam("tag_type_id", "1");
        task.setPostExecute(new VoidTask.PostExecute() {
            @Override
            public void execute(StringBuilder response) {
                Log.d("INFO", "Categories " + response.toString());
                List<CategoryBean> l = new ArrayList<>();
                try {
                    JSONObject d = new JSONObject(response.toString());
                    JSONArray arr = d.getJSONArray("data");
                    for (int i = 0; i < arr.length(); i++) {
                        org.json.JSONObject o = arr.getJSONObject(i);
                        l.add(new CategoryBean(o.getInt("tag_id"),
                                o.getString("tag_name"), o.getInt("tag_type_id"),o.getString("image")));
                    }
                } catch (JSONException e) {
                }

                if (l != null)
                    if (l.size() > 0) {
                        U.put("cat_list_1", l);
                        RecyGridCategoryAdapter ca = new RecyGridCategoryAdapter(l);
                        recyclerViewTypes.setAdapter(ca);
                    }
            }
        });
        task.execute();
        task = new VoidTask("/restaurant/categories");
        task.addParam("tag_type_id", "2");
        task.setPostExecute(new VoidTask.PostExecute() {
            @Override
            public void execute(StringBuilder response) {
                Log.d("INFO", "Moods " + response.toString());
                List<CategoryBean> l2 = new ArrayList<>();
                try {
                    JSONObject d = new JSONObject(response.toString());
                    JSONArray arr = d.getJSONArray("data");
                    for (int i = 0; i < arr.length(); i++) {
                        org.json.JSONObject o = arr.getJSONObject(i);
                        l2.add(new CategoryBean(o.getInt("tag_id"),
                                o.getString("tag_name"), o.getInt("tag_type_id"), o.getString("image")));
                    }
                } catch (JSONException e) {
                }
                if (l2 != null)
                    if (l2.size() > 0) {
                        U.put("cat_list_2", l2);
                        RecyGridCategoryAdapter ca = new RecyGridCategoryAdapter(l2);
                        recyclerViewMoods.setAdapter(ca);
                    }
            }
        });
        task.execute();
        query(null);
    }

    public void configure(View view){
        UD.getInstance().put("promo_fragment_", this);

        loading_ = (CircularProgressView) view.findViewById(R.id.loading_);
        loading_.startAnimation();
        message = (TextView) view.findViewById(R.id.__message__);

        GridLayoutManager manager = new GridLayoutManager(getActivity().getApplicationContext(),1);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewTypes  =(RecyclerView) view.findViewById(R.id.rec_food_types);
        recyclerViewTypes.setHasFixedSize(true);
        recyclerViewTypes.setLayoutManager(manager);

        GridLayoutManager manager2 = new GridLayoutManager(getActivity().getApplicationContext(),1);
        manager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewMoods  =(RecyclerView) view.findViewById(R.id.rec_moods);
        recyclerViewMoods.setHasFixedSize(true);
        recyclerViewMoods.setLayoutManager(manager2);

        cmanager = new CustomLinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewRestaurants = (RecyclerView) view.findViewById(R.id.rec_restaurants_);
        recyclerViewRestaurants.setHasFixedSize(true);
        recyclerViewRestaurants.setLayoutManager(cmanager);
        recyclerViewRestaurants.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                final int visibleItemCount = cmanager.getChildCount();
                final int totalItemCount = cmanager.getItemCount();
                final int pastVisiblesItems = cmanager.findFirstVisibleItemPosition();

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
        Integer tag = (Integer) UD.getInstance().get("current_tag");
        VoidActivityTask task = WebServiceHelper.restaurantsQuery(this, 1);
        if(tag != null)
            task.addParam("tag_id", String.valueOf(tag));
        task.addParam("page_size", page_size);
        task.addParam("page_post", String.valueOf(page_post++));
        task.execute();
    }

    public void query(Integer tag_id){
        showloading();
        page_post = 0;
        UD.getInstance().put("current_tag", tag_id);
        VoidActivityTask task = WebServiceHelper.restaurantsQuery(this, 0);
        if(tag_id != null)
            task.addParam("tag_id", String.valueOf(tag_id));
        task.addParam("page_size", page_size);
        task.addParam("page_post", String.valueOf(page_post++));
        task.execute();
    }

    void showloading(){
        recyclerViewRestaurants.setVisibility(View.GONE);
        message.setVisibility(View.GONE);
        loading_.setVisibility(View.VISIBLE);
    }

    void notLoading(){
        message.setVisibility(View.GONE);
        recyclerViewRestaurants.setVisibility(View.VISIBLE);
        loading_.setVisibility(View.GONE);
    }

    void notElements(){
        recyclerViewRestaurants.setVisibility(View.GONE);
        loading_.setVisibility(View.GONE);
        message.setVisibility(View.VISIBLE);
    }

    public void showProduct(String restaurantName) {
        MaterialNavigationDrawer parent = ((MaterialNavigationDrawer) this.getActivity());

        if(restaurantName!=null)
            parent.setFragmentChild(new PromoProductFragment(), restaurantName);

    }
}