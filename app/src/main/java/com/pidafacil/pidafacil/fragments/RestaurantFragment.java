package com.pidafacil.pidafacil.fragments;

import android.content.Intent;
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

import com.appboy.Appboy;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.pidafacil.pidafacil.NavigationDrawer;
import com.pidafacil.pidafacil.R;
import com.pidafacil.pidafacil.activities.DeepLinkParserActivity;
import com.pidafacil.pidafacil.beans.CategoryBean;
import com.pidafacil.pidafacil.beans.GeneralOptionsBean;
import com.pidafacil.pidafacil.beans.RestaurantBean;
import com.pidafacil.pidafacil.components.RecyGridCategoryAdapter;
import com.pidafacil.pidafacil.components.RestaurantAdapter;
import com.pidafacil.pidafacil.singleton.UD;
import com.pidafacil.pidafacil.task.ExecutionMethod;
import com.pidafacil.pidafacil.task.VoidActivityTask;
import com.pidafacil.pidafacil.task.VoidTask;
import com.pidafacil.pidafacil.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

/**
 * Created by victor on 06-03-15.
 */
public class RestaurantFragment extends Fragment implements ExecutionMethod{
    private RecyclerView recyclerView;
    private LinearLayoutManager mLayoutManager;
    private String category;
    private String category2;
    private CircularProgressView loading_;
    private TextView message_;
    private int page_post = 0;
    private List e;
    private String page_size;
    boolean isLooking = false;
    private List results;
    private UD U = UD.getInstance();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurant, container, false);
        loading_ = (CircularProgressView) view.findViewById(R.id.loading_);
        loading_.startAnimation();
        message_ = (TextView) view.findViewById(R.id.__message__);
        category = UD.getInstance().get(UD.CATEGORY_).toString();
        String sTextoBuscado= "CategoryBean";
        String sTexto ="";
        int contador=0 ,pos=0;
        List<CategoryBean> beans=null;
        loadCategories();
        if(UD.getCategories()== null){
            if(U.get("cat_list_1")!=null){
           category2=U.get("cat_list_1").toString();
            Log.d("cate",""+U.get("cat_list_1"));
            beans = (List<CategoryBean>) U.get("cat_list_1");
            Log.d("contador",""+contador);
            //beans=contador;
                Intent intent0 = new Intent(getActivity(), NavigationDrawer.class);
                intent0.putExtra(DeepLinkParserActivity.IS_DEEPLINK, true);
                intent0.putExtra(DeepLinkParserActivity.TO_VIEW, RestaurantFragment.class.getName());
                intent0.putExtra("category_id", category);
                startActivity(intent0);

            }else{
                Intent intent0 = new Intent(getActivity(), NavigationDrawer.class);
                intent0.putExtra(DeepLinkParserActivity.IS_DEEPLINK, true);
                intent0.putExtra(DeepLinkParserActivity.TO_VIEW, RestaurantFragment.class.getName());
                intent0.putExtra("category_id", category);
                startActivity(intent0);
            }
        }else{
              beans = UD.getCategories();
             }

        Integer c = Integer.valueOf(category);


        for(CategoryBean bean : beans)
            if(bean.getTagId() == c){
                getActivity().setTitle(bean.getTagName());
                UD.VIEW_ = 2;
                UD.getInstance().put(UD.TAG_ID_, bean.getTagId().toString());
                UD.getInstance().put(UD.TAG_SELECTED_NAME_, bean.getTagName());
                Log.d("TAG_SELECTED_NAME_", "" + U.get(UD.TAG_SELECTED_NAME_).toString());
                Log.d("TAG_ID_", "" + U.get(UD.TAG_ID_).toString());

                Utils.appboyEvent(Appboy.getInstance(getActivity().getApplicationContext()),
                        "Explore", new Object[]{bean.getTagName()});

            }
        Log.d("TAG_SELECTED_NAME_2", "" + U.get(UD.TAG_SELECTED_NAME_).toString());
        sendAppboyCustomAttributesRevenue();
        configure(view);
        UD.getInstance().put("restaurant_fragment_", this);
        load();

        return view;
    }

    public void configure(View view){
        page_size = getString(R.string.pagination_page_size);
        recyclerView = (RecyclerView) view.findViewById(R.id.rest_list);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        //recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                final int visibleItemCount = mLayoutManager.getChildCount();
                final int totalItemCount = mLayoutManager.getItemCount();
                final int pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                if (!isLooking)
                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        Log.d("INFO", " Cargando...");
                        isLooking = true;
                        paginate();
                    }
            }
        });
    }

    void errMessage(){
        message_.setText("Por el momento no hay ningún restaurante en esta sección.");
        loading_.setVisibility(View.GONE);
        message_.setVisibility(View.VISIBLE);
    }

    @Override
    public void executeResult(List list, int operationCode) {
        isLooking = false;

        if(operationCode == 0){
            loading_.setVisibility(View.GONE);
            if(list!=null)
                if(list.size()>0){
                    e = list;
                    RestaurantAdapter ca = new RestaurantAdapter(list);
                    recyclerView.setAdapter(ca);
                }else
                    errMessage();
            else
                errMessage();
        }else{
            if(list!=null)
                if(list.size()>0){
                    for(Object o : list)
                        e.add(o);
                    RestaurantAdapter ca = new RestaurantAdapter(e);
                    recyclerView.setAdapter(ca);
                }
        }
    }

    void load(){
        VoidActivityTask taskActivity = new VoidActivityTask("/restaurant", this, 0) {
            @Override
            public void execute(StringBuilder result) {
                try {
                    org.json.JSONObject data = new org.json.JSONObject(result.toString());
                    Log.d("restaurant:WS-RESULT", result.toString());
                    if(data.getString("status").equals("true")){
                        resultList = new RestaurantBean()
                                .parseArray(new org.json.JSONObject(result.toString()));
                    }

                } catch (JSONException e) {
                    Log.d("restaurant:ERR-PARSE", e.getMessage());
                }
            }
        };

        taskActivity.addParam("tag_id", category);
        taskActivity.addParam("page_size", page_size);
        taskActivity.addParam("page_post", String.valueOf(page_post++) );

        if(e!=null){
            recyclerView.setAdapter(new RestaurantAdapter(e));
            loading_.setVisibility(View.GONE);
        }else{
            taskActivity.execute();
        }
    }

    void paginate(){
        VoidActivityTask taskActivity = new VoidActivityTask("/restaurant", this, 1) {
            @Override
            public void execute(StringBuilder result) {
                try {
                    Log.d("restaurant:WS-RESULT",result.toString());
                    org.json.JSONObject data = new org.json.JSONObject(result.toString());
                    if(data.getString("status").equals("true")){
                        resultList = new RestaurantBean()
                                .parseArray(new org.json.JSONObject(result.toString()));
                    }

                } catch (JSONException e) {
                    Log.d("restaurant:ERR-PARSE", e.getMessage());
                }
            }
        };

        taskActivity.addParam("tag_id", category);
        taskActivity.addParam("page_size", page_size);
        taskActivity.addParam("page_post", String.valueOf(page_post++) );
        taskActivity.execute();
    }

    public void goToRestaurantInfo(String restaurantName) {
        MaterialNavigationDrawer parent = (MaterialNavigationDrawer) getActivity();
        parent.setFragment(new RestaurantInfoFragment(), restaurantName);
        UD.getInstance().setCurrentFragment(RestaurantInfoFragment.class.getSimpleName());
        Log.d("RestaurantFragment", "RestaurantFragment");
    }

    private void sendAppboyCustomAttributesRevenue(){
        Appboy.getInstance(getActivity().getApplicationContext()).getCurrentUser().addToCustomAttributeArray(
                "Tag_Visitada:", "ID:" + U.get(UD.TAG_ID_).toString() + "\nNombre: " + U.get(UD.TAG_SELECTED_NAME_).toString()
        );
    }
public void loadCategories(){
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
                UD.setCategories(l);
            } catch (JSONException e) {
            }

            if (l != null)
                if (l.size() > 0) {
                    UD.setCategories(l);
                }
        }
    });
    task.execute();

}


}