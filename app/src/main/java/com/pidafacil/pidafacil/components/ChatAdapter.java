package com.pidafacil.pidafacil.components;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pidafacil.pidafacil.R;
import com.pidafacil.pidafacil.beans.ChatBean;
import com.pidafacil.pidafacil.singleton.UD;

import java.util.List;

/**
 * Created by victor on 04-15-15.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.GenericHolder>{
    private List beans;

    public List getBeans() {
        return beans;
    }

    public void setBeans(List beans) {
        this.beans = beans;
    }

    public void clear() {
        beans.clear();
    }

    public ChatAdapter(List beans){
        this.beans = beans;
    }

    public boolean add(Object object) {
        return beans.add(object);
    }

    @Override
    public int getItemCount() {
        return beans.size();
    }

    @Override
    public void onBindViewHolder(GenericHolder holder, int i) {
        Context ctx = UD.getInstance().getContext();
        ChatBean chatBean = (ChatBean) beans.get(i);
        if(chatBean.getType() == ChatBean.TypeChat.SERVER_UPDATE){
            holder.lay_user_hour.setVisibility(View.GONE);
            holder.textUser.setVisibility(View.GONE);
            holder.textMessage.setText(chatBean.getMessage());
            holder.textHour.setText(chatBean.getHour());
        } else {
            holder.lay_user_hour.setVisibility(View.VISIBLE);
            holder.textUser.setVisibility(View.VISIBLE);
            holder.textUser.setText(chatBean.getUser());
            holder.textMessage.setText(chatBean.getMessage());
            holder.textHour.setText(chatBean.getHour());
            if(chatBean.getType() == ChatBean.TypeChat.SUPPORT_CHAT_USERCL){
                holder.textUser.setTextColor(ctx.getResources().getColor(R.color.chat_support_user_cl));
            }else{
                holder.textUser.setTextColor(ctx.getResources().getColor(R.color.chat_support_user_supp));
            }
        }
    }

    @Override
    public GenericHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.recycler_item_chat_support, viewGroup, false);
        return new GenericHolder(itemView);
    }

    public class GenericHolder extends RecyclerView.ViewHolder{
        protected TextView textUser;
        protected TextView textMessage;
        protected TextView textHour;
        protected View lay_user_hour;

        public GenericHolder(View view){
            super(view);
            textUser = (TextView) view.findViewById(R.id.txt_user_name__);
            textMessage = (TextView) view.findViewById(R.id.txt_user_message__);
            textHour = (TextView) view.findViewById(R.id.txt_chat_hour);
            lay_user_hour = view.findViewById(R.id.lay_user_hour);
        }
    }

}
