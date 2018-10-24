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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.pidafacil.pidafacil.R;
import com.pidafacil.pidafacil.beans.CategoryBean;
import com.pidafacil.pidafacil.beans.GeneralOptionsBean;
import com.pidafacil.pidafacil.beans.RestaurantBean;
import com.pidafacil.pidafacil.components.RecyGridCategoryAdapter;
import com.pidafacil.pidafacil.components.RestaurantAdapter;
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
 * Created by mauricio on 27/5/15.
 */
public class ExploreFragment extends Fragment implements ExecutionMethod {
    private RecyclerView recyclerViewTypes;
    private RecyclerView recyclerViewRestaurants;
    private UD U = UD.getInstance();
    private List<RestaurantBean> beans;
    private CircularProgressView loading;
    private LinearLayout content1;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        VoidActivityTask task = new VoidActivityTask("/generalOption", this, 1) {
            @Override
            public void execute(StringBuilder result) {
                Log.d("INFO", "CONFIGURACIONES GENERALES PF " + result);
                this.resultList = new ArrayList();
                try {
                    GeneralOptionsBean b = new GeneralOptionsBean()
                            .parseObject(new JSONObject(result.toString()));
                    this.resultList.add(b);
                } catch (JSONException e) { }
            }
        };
        task.addParam("id_conf_general_options", "1");
        task.execute();

        View view = inflater.inflate(R.layout.fragment_explore,container,false);

        UD.VIEW_ = 0;
        configure(view);

        if(U.get("cat_list_1") == null)
            configureRecyclers();
        else {
            List<RestaurantBean> l0 = (List<RestaurantBean>) U.get("restaurant_list");
            recyclerViewRestaurants.setAdapter(new RestaurantAdapter(l0));
            List<CategoryBean> l = (List<CategoryBean>) U.get("cat_list_1");
            RecyGridCategoryAdapter ca = new RecyGridCategoryAdapter(l);
            recyclerViewTypes.setAdapter(ca);
            showElements();
        }
        U.put("explore_fragment", this);
        U.put("from_fragment",this);
        return view;
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
                    for(int i=0;i<arr.length();i++){
                        org.json.JSONObject o = arr.getJSONObject(i);
                        l.add(new CategoryBean(
                                o.getInt("tag_id"),
                                o.getString("tag_name"),
                                o.getInt("tag_type_id"),
                                o.getString("image")));
                    }
                } catch (JSONException e) { }

                if(l!=null)
                    if(l.size()>0){
                        U.put("cat_list_1", l);
                        showElements();
                        RecyGridCategoryAdapter ca = new RecyGridCategoryAdapter(l);
                        recyclerViewTypes.setAdapter(ca);
                    }
            }
        });
        task.execute();

        VoidTask taskRestaurant = new VoidTask("/restaurant/list");
        taskRestaurant.setPostExecute(
                new VoidTask.PostExecute() {
                    @Override
                    public void execute(StringBuilder result) {
                        Log.d("restaurantL:WS-RESULTS", result.toString());
                        try {
                            org.json.JSONObject d = new org.json.JSONObject(result.toString());
                            if (d.getString("status").equals("true")) {
                                beans = new RestaurantBean().parseArray(d);
                                U.put("restaurant_list", beans);
                                recyclerViewRestaurants.setAdapter(new RestaurantAdapter(beans));
                                showElements();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showElements();
                        }
                    }
                });
        taskRestaurant.execute();
    }

    public void configure(View view){
        loading = (CircularProgressView) view.findViewById(R.id.loading_);
        content1 = (LinearLayout) view.findViewById(R.id.content1);
        GridLayoutManager manager = new GridLayoutManager(getActivity().getApplicationContext(), 1);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewTypes  =(RecyclerView) view.findViewById(R.id.rec_food_types);
        recyclerViewTypes.setHasFixedSize(true);
        recyclerViewTypes.setLayoutManager(manager);

        recyclerViewRestaurants = (RecyclerView) view.findViewById(R.id.rec_restaurants_);
        recyclerViewRestaurants.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

    }

    void showElements(){recyclerViewRestaurants.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        loading.setVisibility(View.GONE);
        content1.setVisibility(View.VISIBLE);
    }

    public void goToRestaurants() {
        U.put("from_fragment", null);
        MaterialNavigationDrawer parent = (MaterialNavigationDrawer) getActivity();
        parent.setFragment(new RestaurantFragment(), "");
        UD.getInstance().setCurrentFragment(RestaurantFragment.class.getSimpleName());
    }

    public void goToRestaurantInfo(String name) {
        U.put("from_fragment", null);
        MaterialNavigationDrawer parent = (MaterialNavigationDrawer) getActivity();
        if(parent != null){
            parent.setFragment(new RestaurantInfoFragment(), name);
            UD.getInstance().setCurrentFragment(RestaurantFragment.class.getSimpleName());
        }else{
            RestaurantFragment fragment0 = (RestaurantFragment) UD.getInstance().get("restaurant_fragment_");
            fragment0.goToRestaurantInfo(name);

        }
    }


    @Override
    public void executeResult(List list, int operationCode) {
        if(operationCode == 1) {
            if (!list.isEmpty() && list != null && list.size()!=0 ) {
                if (list.size() > 0) {
                    GeneralOptionsBean bean = (GeneralOptionsBean) list.iterator().next();
                    UD.NUM_ATENCION_CLIENTE =bean.getNum_atencion_cliente();
                }
            }
        }
    }

}
