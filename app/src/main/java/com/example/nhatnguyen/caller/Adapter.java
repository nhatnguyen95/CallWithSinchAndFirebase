package com.example.nhatnguyen.caller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Nhat Nguyen on 08-Apr-16.
 */
public class Adapter extends ArrayAdapter<User> {
    private Context context;
    private ArrayList<User> objects;
    private int resId;

    public Adapter(Context context,  ArrayList<User> objects, int resource) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
        this.resId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater flate = LayoutInflater.from(context);
        View row = flate.inflate(resId, null);
        TextView tvName = (TextView)row.findViewById(R.id.userCustom);
        TextView tvNumber = (TextView)row.findViewById(R.id.numberCustom);
        tvName.setText(objects.get(position).getName());
        tvNumber.setText(objects.get(position).getPhoneNumber());
        return row;
    }
}
