package com.pidafacil.pidafacil.components;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.pidafacil.pidafacil.R;

/**
 * Created by victor on 05-20-15.
 * Se ocupa para el SuggestionAdapter, de esta manera
 * se muestran las sugerencias del buscador.
 */
public class ItemAdapter extends CursorAdapter {

    TextView text;

    public ItemAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        Log.d("ADAPTER","Configurando el cursor con "+c.getCount()+" elementos");
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.searchview_list_item_suggest_, parent, false);
        text = (TextView) view.findViewById(R.id.item);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        text.setText(cursor.getString(cursor.getColumnIndex("itemText")));
    }
}
