package com.pidafacil.pidafacil.components;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pidafacil.pidafacil.R;
import com.pidafacil.pidafacil.beans.CategoryBean;
import com.pidafacil.pidafacil.fragments.ExploreFragment;
import com.pidafacil.pidafacil.fragments.PromoFragment;
import com.pidafacil.pidafacil.singleton.UD;
import com.pidafacil.pidafacil.util.Resource;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by victor on 02-10-15.
 */
public class RecyGridCategoryAdapter extends RecyclerView.Adapter<RecyGridCategoryAdapter.CategoryHolder>{

    public List<CategoryBean> beans;
    private UD U = UD.getInstance();


    public RecyGridCategoryAdapter(List<CategoryBean> beans){
        this.beans = beans;
    }

    @Override
    public int getItemCount() {
        return beans.size();
    }

    @Override
    public void onBindViewHolder(CategoryHolder holder, int i) {
        CategoryBean bean = beans.get(i);
        if(bean.getTagUrl()!= null)
            if(!bean.getTagUrl().equals("null")){
                Picasso.with(UD.getInstance().getContext())
                        .load(Resource.APP_ICONS + bean.getTagUrl()).into(holder.imageView);
            }

        UD.getInstance().put(UD.TAG_ID_, bean.getTagId().toString());
        UD.getInstance().put(UD.TAG_SELECTED_NAME_, bean.getTagName());
        Log.d("TAG_SELECTED_NAME_", U.get(UD.TAG_SELECTED_NAME_).toString());
        Log.d("TAG_ID_", U.get(UD.TAG_ID_).toString());

        holder.textView.setText(String.valueOf(bean.getTagId()));
    }

    @Override
    public CategoryHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.recycler_grid_category_content, viewGroup, false);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout linearLayout = (LinearLayout) v;
                TextView id = (TextView) linearLayout.getChildAt(1);
                UD.getInstance().put(UD.CATEGORY_, id.getText());
                UD.getInstance().put(UD.TAG_ID_, id.getText().toString());
                UD.getInstance().put(UD.TAG_SELECTED_NAME_, id.getText());
                Log.d("TAG_SELECTED_NAME_", U.get(UD.TAG_SELECTED_NAME_).toString());
                Log.d("TAG_ID_", U.get(UD.TAG_ID_).toString());
                UD.setCategories(beans);

                if(UD.VIEW_ == 1){
                    PromoFragment fragment = (PromoFragment) UD.getInstance().get("promo_fragment_");
                    fragment.query(Integer.valueOf(id.getText().toString()));
                }else{
                    ExploreFragment fragment = (ExploreFragment) UD.getInstance().get("explore_fragment");
                    fragment.goToRestaurants();
                }
            }
        });

        return new CategoryHolder(itemView);
    }

    public class CategoryHolder extends RecyclerView.ViewHolder{
        protected ImageView imageView;
        protected TextView textView;
        public CategoryHolder(View view){
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.grid_item_img);
            this.textView = (TextView) view.findViewById(R.id.text_id_category);
        }
    }

}

