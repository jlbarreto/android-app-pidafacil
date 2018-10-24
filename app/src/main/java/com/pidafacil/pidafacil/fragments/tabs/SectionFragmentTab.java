package com.pidafacil.pidafacil.fragments.tabs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.pidafacil.pidafacil.R;
import com.pidafacil.pidafacil.beans.RestaurantInfo;
import com.pidafacil.pidafacil.beans.SectionBean;
import com.pidafacil.pidafacil.components.RestaurantSectionAdapter;
import com.pidafacil.pidafacil.singleton.UD;
import com.pidafacil.pidafacil.task.ExecutionMethod;
import com.pidafacil.pidafacil.task.VoidActivityTask;
import com.pidafacil.pidafacil.util.Resource;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SectionFragmentTab extends Fragment implements ExecutionMethod{
    private RecyclerView recyclerView;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_restaurant_tab_section, container, false);
        recyclerView  =(RecyclerView) view.findViewById(R.id.recycler_sections);
        //recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(container.getContext(),  LinearLayoutManager.VERTICAL, false);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(llm);
       recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2)); //Para los nombres de las categorias por restaurante





        VoidActivityTask task = new VoidActivityTask("/restaurant/sections", this, 1) {
            @Override
            public void execute(StringBuilder result) {
                try {
                    this.resultList = new SectionBean()
                            .parseArray(new JSONObject(result.toString()));
                } catch (JSONException e){ }
            }
        };

        task.addParam("restaurant_id", (String) UD.getInstance().get(UD.RESTAURANT_));
        task.execute();

        VoidActivityTask task2 = new VoidActivityTask("/restaurant/get", this, 2) {
            @Override
            public void execute(StringBuilder result) {
                this.resultList = new ArrayList();
                try {
                    RestaurantInfo b = new RestaurantInfo()
                            .parseObject(new JSONObject(result.toString()));
                    this.resultList.add(b);
                } catch (JSONException e) {

                }
            }
        };

        task2.addParam("restaurant_id", (String) UD.getInstance().get(UD.RESTAURANT_));
        task2.execute();

        return view;
    }

    void hideLoading(){
        this.view.findViewById(R.id.loading_).setVisibility(View.GONE);
    }

    String imgUri; int screenx = 0, screeny = 0;

    Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            try{
                view.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
            }catch (IllegalStateException e){ }
        }
        @Override
        public void onBitmapFailed(Drawable errorDrawable) { }
        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) { }
    };

    @Override
    public void executeResult(List list, int operationCode) {
        if(operationCode == 1){
            if(list.size()>0){
                hideLoading();
                recyclerView.setAdapter(new RestaurantSectionAdapter(list));
            }
        }else {
            RestaurantInfo bean = (RestaurantInfo) list.iterator().next();
            UD.getInstance().put("uri_restaurant_background", bean.getImgUri());
            UD.getInstance().put("logo_uri__", bean.getImgUriLogo());
            if (!bean.getImgUri().equals("null")) {
                try {
                    Context ctx = UD.getInstance().getContext();
                    WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
                    Display display = wm.getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    imgUri = bean.getImgUri();
                    screenx = size.x;
                    screeny = size.y;

                    Picasso.with(getActivity().getApplicationContext()).load(Resource.RESOURCE_URI + imgUri)
                            .resize(screenx, screeny).centerCrop().into(target);
                    view.setTag(target);
                } catch (NullPointerException e1) { }
            }
        }
    }

}