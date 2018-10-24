package com.pidafacil.pidafacil.components;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pidafacil.pidafacil.R;
import com.pidafacil.pidafacil.beans.SimpleProductBean;

import java.util.List;

/**
 * Created by victor on 04-14-15.
 */
public class ProductsOrderAdapter extends RecyclerView.Adapter<ProductsOrderAdapter.OrderHolder> {

    private List beans;

    public ProductsOrderAdapter(List beans) {
        this.beans = beans;
    }

    @Override
    public OrderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item_order_product_row, parent, false);
        return new OrderHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderHolder holder, int position) {
        SimpleProductBean bean = (SimpleProductBean) beans.get(position);
        holder.textViewName.setText(bean.getDetail());
        holder.textViewPrice.setText(bean.getPrice());
    }

    @Override
    public int getItemCount() {
        return beans.size();
    }

    public class OrderHolder extends RecyclerView.ViewHolder{

        protected TextView textViewName;
        protected TextView textViewPrice;

        public OrderHolder(View view){
            super(view);
            this.textViewName = (TextView) view.findViewById(R.id.lbl_descript);
            this.textViewPrice = (TextView) view.findViewById(R.id.lbl_price);
        }

    }
}
