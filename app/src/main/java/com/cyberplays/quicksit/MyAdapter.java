package com.cyberplays.quicksit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

//Adapter for populating the listview of restaurants
public class MyAdapter extends ArrayAdapter<Restaurant> {
    private final Context context;
    private ArrayList<Restaurant> rests = new ArrayList<Restaurant>();
    private final int resource;
    private LayoutInflater mInflater;
    private Location userLocation;

    private TextView res_name, res_type, res_dist;

    public MyAdapter(Context context, int resource, ArrayList<Restaurant> rests, Location location) {
        super(context, resource, rests);
        this.context = context;
        this.rests = rests;
        this.resource = resource;
        this.userLocation = location;

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return rests.size();
    }

    @Override
    public Restaurant getItem(int position) {
        return rests.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, resource);
    }

    private View createViewFromResource(int position, View convertView,
                                        ViewGroup parent, int resource) {
        View v;
        if (convertView == null) {
            v = mInflater.inflate(resource, parent, false);
            res_name = (TextView) v.findViewById(R.id.list_item_name);

            res_type = (TextView) v.findViewById(R.id.list_item_type);

            res_dist = (TextView) v.findViewById(R.id.list_item_dist);
        } else {
            v = convertView;
        }

        res_name.setText(rests.get(position).getName());

        res_type.setText(rests.get(position).getType());

        res_dist.setText(Double.toString(rests.get(position).getDist(userLocation)) + "mi");

        return v;
    }


    @Override
    public int getViewTypeCount() {
        return getCount();
    }




}
