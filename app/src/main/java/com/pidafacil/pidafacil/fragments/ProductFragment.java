package com.pidafacil.pidafacil.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.pidafacil.pidafacil.R;
import com.pidafacil.pidafacil.components.ProductAdapter;
import com.pidafacil.pidafacil.helper.WebServiceHelper;
import com.pidafacil.pidafacil.model.RestaurantRequest;
import com.pidafacil.pidafacil.singleton.UD;
import com.pidafacil.pidafacil.task.ExecutionMethod;
import com.pidafacil.pidafacil.task.VoidActivityTask;
import com.pidafacil.pidafacil.util.Resource;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

/**
 * Created by victor on 06-04-15.
 */
public class ProductFragment extends Fragment implements ExecutionMethod{
    private RecyclerView recyclerView;
    private LinearLayoutManager mLayoutManager;
    private ImageView loading;
    private CircularProgressView loading_;
    private TextView message_;
    private int page_post = 0;
    private String page_size;
    private List e;
    private String section;
    private boolean isLooking = false;
    private View view;
    private UD U = UD.getInstance();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_product, container, false);
        getActivity().setTitle((CharSequence) U.get(UD.SECTION_SELECTED_NAME_));
        viewBackground();
        loading_ = (CircularProgressView) view.findViewById(R.id.loading_);
        loading_.startAnimation();
        message_ = (TextView) view.findViewById(R.id.__message__);
        configure();
        U.put("product_fragment_", this);
        load();
        paginate();
        UD.VIEW_ = 4;
        return view;
    }

    public void configure(){
        page_size = getString(R.string.pagination_page_size);
        recyclerView = (RecyclerView) view.findViewById(R.id.rec_products);
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

                if(!isLooking)
                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        isLooking = true;
                        paginate();
                    }
            }

        });
    }

    RestaurantRequest request;

    void load(){
        if(e!=null){
            recyclerView.setAdapter(new ProductAdapter(e));
            loading_.setVisibility(View.GONE);
        } else {
            section = U.get(UD.SECTION_).toString();
            VoidActivityTask taskActivity = WebServiceHelper.getProductQuery(this, 0);
            taskActivity.addParam("section_id", section);
            taskActivity.addParam("page_size", page_size);
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

    void paginate(){
        VoidActivityTask taskActivity = WebServiceHelper.getProductQuery(this, 1);
        taskActivity.addParam("section_id", section);
        taskActivity.addParam("page_size", page_size);
        taskActivity.addParam("page_post", String.valueOf(page_post++));
        taskActivity.execute();
    }

    @Override
    public void executeResult(List list, int operationCode) {
        isLooking = false;
        if(operationCode == 0){
            if(list != null)
                if(list.size()>0){
                    e = list;
                    ProductAdapter productAdapter = new ProductAdapter(list);
                    loading_.setVisibility(View.GONE);
                    recyclerView.setAdapter(productAdapter);
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

    public void goToDetail(String product) {
        MaterialNavigationDrawer parent = (MaterialNavigationDrawer) getActivity();
        parent.setFragmentChild(new DetailFragment(), product);
        UD.getInstance().setCurrentFragment(DetailFragment.class.getSimpleName());
    }

    private Target targetBackground = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            view.setBackgroundDrawable(new BitmapDrawable(bitmap));}
        @Override
        public void onBitmapFailed(Drawable errorDrawable) { }
        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) { }
    };

    //Muestra el background personalizado del restaurante en el fragmento.
    //Este es el codigo compactado de SectionFragmentTab.java
    private void viewBackground(){
        try{
            Point size = new Point();
            ((WindowManager) view.getContext()
                    .getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay().getSize(size);
            Picasso.with(getActivity().getApplicationContext())
                    .load(Resource.RESOURCE_URI + U.get("uri_restaurant_background").toString())
                    .resize(size.x, size.y).centerCrop().into(targetBackground);
        }catch (NullPointerException e){ }
    }
}
