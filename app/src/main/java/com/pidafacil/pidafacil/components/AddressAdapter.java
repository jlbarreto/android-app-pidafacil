package com.pidafacil.pidafacil.components;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pidafacil.pidafacil.activities.*;
import com.pidafacil.pidafacil.R;
import com.pidafacil.pidafacil.beans.AddressBean;
import com.pidafacil.pidafacil.singleton.UD;
import com.pidafacil.pidafacil.task.VoidTask;

import java.util.List;

/**
 * Created by victor on 04-15-15.
 */
public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.GenericHolder>{
    public List beans;
    AddressActivity addressActivity;

    public AddressAdapter(List beans, AddressActivity addressActivity){
        this.beans = beans;
        this.addressActivity = addressActivity;
    }

    @Override
    public int getItemCount() {
        return beans.size();
    }

    @Override
    public void onBindViewHolder(GenericHolder holder, int i) {
        AddressBean bean = (AddressBean) beans.get(i);
        holder.textViewId.setText(String.valueOf(bean.getId()));
        holder.textViewTitle.setText(bean.getName());
        holder.textViewDescription.setText(bean.getAddress1().concat(" " + bean.getAddress2()));
    }

    @Override
    public GenericHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.recycler_item_address_row, viewGroup, false);
        itemView.findViewById(R.id.text_content_).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout ll = (LinearLayout) v.getParent().getParent();
                TextView id = (TextView) ll.findViewById(R.id.id__);
                UD.getInstance().put("address_id", id.getText().toString());
                addressActivity.edit();
            }
        });
        return new GenericHolder(itemView);
    }

    public class GenericHolder extends RecyclerView.ViewHolder{

        protected TextView textViewId;
        protected TextView textViewTitle;
        protected TextView textViewDescription;
        protected ImageView textdel;

        public GenericHolder(View view){
            super(view);
            this.textViewId = (TextView) view.findViewById(R.id.id__);
            this.textViewTitle = (TextView) view.findViewById(R.id.txt_addr_title);
            this.textViewDescription = (TextView) view.findViewById(R.id.txt_addr_descript);
            this.textdel = (ImageView) view.findViewById(R.id.txt_delete_addr);
            this.textdel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v0) {
                    final View v = v0;
                    AlertDialog.Builder alert = (AlertDialog.Builder) UD.getInstance().get("alert_del");
                    alert.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {
                            View parent = (View) v.getParent().getParent().getParent();
                            TextView id_ = (TextView) parent.findViewById(R.id.id__);
                            Log.d("id_",""+id_.getText().toString());
                            final String id = id_.getText().toString();
                            Log.d("id",""+id);
                            if(!id.isEmpty()){
                                VoidTask task = new VoidTask("/user/address/drop");
                                task.addParam("address_id", id);
                                task.setPostExecute(new VoidTask.PostExecute() {
                                    @Override
                                    public void execute(StringBuilder response) {
                                        List addr = AddressActivity.addressBeans;
                                        for(int i=0; i<addr.size(); i++){
                                            String idStr = String.valueOf(((AddressBean) addr.get(i)).getId());
                                            if(id.equals(idStr)){
                                                AddressActivity.addressBeans.remove(i);
                                            }
                                        }

                                        addressActivity.orderElements();
                                        dialog.dismiss();
                                    }
                                });
                                task.execute();
                            }
                        }
                    });

                    AlertDialog d = alert.create();
                    d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    d.show();
                }
            });
        }

    }

}
