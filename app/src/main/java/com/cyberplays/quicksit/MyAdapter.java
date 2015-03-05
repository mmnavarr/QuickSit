package com.cyberplays.quicksit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by malcolmnavarro on 2/3/15.
 */
public class MyAdapter extends ArrayAdapter<Restaurant> {
    private final Context context;
    private ArrayList<Restaurant> rests = new ArrayList<Restaurant>();
    private final int resource;
    private LayoutInflater mInflater;
    private TextView res_name, res_type, res_dist;

    public MyAdapter(Context context, int resource, ArrayList<Restaurant> rests) {
        super(context, resource, rests);
        this.context = context;
        this.rests = rests;
        this.resource = resource;
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

        Restaurant r = rests.get(position);

        res_name.setText(r.getName());

        res_type.setText(r.getType());

        res_dist.setText(Double.toString(r.getDist()) + "mi");

        return v;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }


}
