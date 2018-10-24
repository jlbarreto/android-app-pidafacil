package com.pidafacil.pidafacil.components;

import android.app.Dialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pidafacil.pidafacil.R;
import com.pidafacil.pidafacil.singleton.UD;

import java.util.List;

/**
 * Created by victor on 03-24-15.
 */
public class CustomSimpleAdapter extends RecyclerView.Adapter<CustomSimpleAdapter.CustomSimpleHolder>{
    public List<String> data;
    int operation;

    public CustomSimpleAdapter(List<String> data, int operation){
        this.data = data;
        this.operation = operation;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onBindViewHolder(CustomSimpleHolder holder, int i) {
        String str = data.get(i);
        holder.textView.setText(str.toString());
        holder.txtPosition.setText(String.valueOf(i));
    }

    @Override
    public CustomSimpleHolder onCreateViewHolder(final ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_item_simple_custom_row_, viewGroup, false);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = ((TextView) ((LinearLayout) v).getChildAt(0)).getText().toString();
                TextView txtPosition = (TextView) ((LinearLayout) v).getChildAt(1);
                UD.getInstance().put(UD.DIALOG_VALUE, value);
                UD.getInstance().put(UD.DIALOG_LIST_POSITION, txtPosition.getText().toString());

                ((Dialog) UD.getInstance().get(UD.DIALOG_)).dismiss();
                ((EndDialogMethod) UD.getInstance()
                        .get(UD.DIALOG_END_METHOD)).endDialog(value, operation);
            }
        });

        return new CustomSimpleHolder(itemView);
    }

    public class CustomSimpleHolder extends RecyclerView.ViewHolder{
        protected TextView textView;
        protected TextView txtPosition;

        public CustomSimpleHolder(View view){
            super(view);
            this.textView = (TextView) view.findViewById(R.id.text_str);
            this.txtPosition = (TextView) view.findViewById(R.id.txt_position);
        }
    }
}

