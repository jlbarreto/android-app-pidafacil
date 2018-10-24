package com.pidafacil.pidafacil.components;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pidafacil.pidafacil.R;
import com.pidafacil.pidafacil.beans.ProductBean;
import com.pidafacil.pidafacil.fragments.ProductFragment;
import com.pidafacil.pidafacil.fragments.PromoProductFragment;
import com.pidafacil.pidafacil.singleton.UD;
import com.pidafacil.pidafacil.util.Parser;
import com.pidafacil.pidafacil.util.Resource;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by victor on 02-12-15.
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductHolder>{

    public List<ProductBean> beans;

    public ProductAdapter(List<ProductBean> beans){
        this.beans = beans;
    }

    @Override
    public int getItemCount() {
        return beans.size();
    }

    @Override
    public void onBindViewHolder(ProductHolder holder, int i) {
        ProductBean bean = beans.get(i);
        if(bean.getImageUri().equals("null")){
            String uri = (String) UD.getInstance().get("logo_uri__");
            Picasso.with(UD.getInstance().getContext())
                    .load(Resource.RESOURCE_URI + uri).into(holder.imageView);
        }else
            Picasso.with(UD.getInstance().getContext())
                    .load(Resource.RESOURCE_URI + bean.getImageUri()).into(holder.imageView);

        String name = bean.getName();
        String description = bean.getDescription();

        if(description.length()>50) {
            description = description.substring(0, 50);
            description = description.concat("...");
        }

        holder.textViewId.setText(String.valueOf(bean.getId()));
        holder.textView.setText(name);
        holder.textViewDetail.setText(description);
        holder.textViewPrice.setText("$".concat(Parser.decimalFormatString(bean.getValue())));
    }

    @Override
    public ProductHolder onCreateViewHolder(final ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.recycler_item_product, viewGroup, false);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout relativeLayout = (LinearLayout) v;
                TextView view = (TextView) relativeLayout.getChildAt(1);
                TextView view1 = (TextView) relativeLayout.findViewById(R.id.genr_lbltext);
                String product = view1.getText().toString();
                UD.getInstance().put(UD.PRODUCT_, view.getText());
                UD.getInstance().put(UD.PRODUCT_NAME_, view1.getText().toString());
                Object object = UD.getInstance().get("product_fragment_");

                if(object instanceof PromoProductFragment){
                    PromoProductFragment fragment = (PromoProductFragment) UD.getInstance().get("product_fragment_");
                    fragment.goToDetail(product);
                }else if(object instanceof ProductFragment){
                    ProductFragment fragment = (ProductFragment) UD.getInstance().get("product_fragment_");
                    fragment.goToDetail(product);
                }
            }
        });

        return new ProductHolder(itemView);
    }

    public class ProductHolder extends RecyclerView.ViewHolder{

        protected ImageView imageView;
        protected TextView textView;
        protected TextView textViewId;
        protected TextView textViewPrice;
        protected TextView textViewDetail;

        public ProductHolder(View view){
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.genr_icon);
            this.textView = (TextView) view.findViewById(R.id.genr_lbltext);
            this.textViewId = (TextView) view.findViewById(R.id.genr_id);
            this.textViewPrice = (TextView) view.findViewById(R.id.lbl_price);
            this.textViewDetail = (TextView) view.findViewById(R.id.genr_lbltextdetail);
        }

    }
}
