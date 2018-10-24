package com.pidafacil.pidafacil.components;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appboy.Appboy;
import com.pidafacil.pidafacil.R;
import com.pidafacil.pidafacil.beans.RestaurantBean;
import com.pidafacil.pidafacil.beans.UserRequestBean;
import com.pidafacil.pidafacil.fragments.ExploreFragment;
import com.pidafacil.pidafacil.fragments.PromoFragment;
import com.pidafacil.pidafacil.fragments.RestaurantFragment;
import com.pidafacil.pidafacil.singleton.UD;
import com.pidafacil.pidafacil.util.Resource;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by victor on 02-12-15.
 */
public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantHolder>{
    private List<RestaurantBean> beans;
    public RestaurantAdapter(List<RestaurantBean> beans){
        this.beans = beans;
    }
    private String nombreRest="";
    private String idRest="";

    @Override
    public int getItemCount() {
        int beandata;
        if(beans != null){
            beandata = beans.size();
        }else {beandata=0;}
        return beandata;
    }

    @Override
    public void onBindViewHolder(RestaurantHolder holder, int i) {
        RestaurantBean bean = beans.get(i);
        if(bean.getImgUri() != null)
            if(!bean.getImgUri().equals("null")){
                Picasso.with(UD.getInstance().getContext())
                        .load(Resource.RESOURCE_URI + bean.getImgUri()).error(R.drawable.ic_restaurant).into(holder.imageView);
            }
        holder.textView.setText(bean.getName());
        Log.d("Nombre Rest", bean.getName());
        nombreRest=bean.getName();
        idRest=String.valueOf(bean.getRestaurantId());
        Log.d("Vista ",String.valueOf(UD.VIEW_));
        holder.textViewId.setText(String.valueOf(bean.getRestaurantId()));
        holder.txtDescription.setText(bean.getDescription());
        if(UD.VIEW_ == 0){
        holder.lay_info_rest.setVisibility(View.GONE);
        }
    }

    @Override
    public RestaurantHolder onCreateViewHolder(final ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.recycler_item_restaurant_row, viewGroup, false);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout relativeLayout = (LinearLayout) v;
                TextView view = (TextView) relativeLayout.findViewById(R.id.genr_id);
                TextView name = (TextView) relativeLayout.findViewById(R.id.genr_lbltext);
                String restaurantName = name.getText().toString();
                UD.getInstance().put(UD.RESTAURANT_NAME_, restaurantName);
                UD.getInstance().put(UD.RESTAURANT_, view.getText());
                UserRequestBean userRequest = new UserRequestBean(Integer.valueOf(UD.getInstance()
                            .get(UD.RESTAURANT_).toString()), UD.USER);
                UD.getInstance().put(UD.CURRENT_USER_RESTAURANT_REQUEST_, userRequest);

                Object fragment = UD.getInstance().get("from_fragment");
                if(fragment != null && (fragment instanceof ExploreFragment)){
                    ((ExploreFragment) fragment).goToRestaurantInfo(restaurantName);
                }else{
                    if(UD.VIEW_ == 1){
                        UD.SECTION_SELECTED_NAME_ = UD.RESTAURANT_NAME_;
                        PromoFragment fragment0 = (PromoFragment) UD.getInstance().get("promo_fragment_");
                        fragment0.showProduct(restaurantName);
                    } else {
                        RestaurantFragment fragment0 = (RestaurantFragment) UD.getInstance().get("restaurant_fragment_");
                        fragment0.goToRestaurantInfo(restaurantName);
                    }
                }
            }
        });

        return new RestaurantHolder(itemView);
    }

    public class RestaurantHolder extends RecyclerView.ViewHolder{

        protected ImageView imageView;
        protected TextView textView;
        protected TextView txtDescription;
        protected TextView textViewId;
        protected LinearLayout lay_info_rest;

        public RestaurantHolder(View view){
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.genr_icon);
            this.textView = (TextView) view.findViewById(R.id.genr_lbltext);
            this.textViewId = (TextView) view.findViewById(R.id.genr_id);
            this.txtDescription = (TextView) view.findViewById(R.id.text_res_description);
            this.lay_info_rest = (LinearLayout) view.findViewById(R.id.lay_info_rest);
        }
    }
}
