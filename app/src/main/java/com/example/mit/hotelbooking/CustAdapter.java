package com.example.mit.hotelbooking;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Mit on 13-09-2016.
 */
public class CustAdapter extends ArrayAdapter<Item> {
    ArrayList<Item>  data;
    Context context;
    int layout;
    public CustAdapter(Context context, int resource,ArrayList<Item> data) {
        super(context, resource,data);
        this.context = context;
        this.data = data;
        this.layout = resource;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RecordHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layout, parent, false);

            holder = new RecordHolder();
            holder.name = (TextView) row.findViewById(R.id.name);
            holder.cat = (TextView) row.findViewById(R.id.cat);
            holder.price = (TextView) row.findViewById(R.id.price);

            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }

        Item item = data.get(position);
        holder.name.setText(item.name);
        holder.cat.setText(item.category);
        holder.price.setText(String.valueOf(item.price));


        return row;

    }

    static class RecordHolder {
        TextView name;
        TextView cat;
        TextView price;



    }
}
