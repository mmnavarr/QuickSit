package com.cyberplays.quicksit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.location.Location;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


public class ListActivity extends Activity {

    private GoogleMap map;
    private ListView mListView;

    public ArrayList<Restaurant> array = new ArrayList<Restaurant>();
    private MyAdapter adapter;
    private User user;
    public Location myLocation;

//kjsfhdg

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            setContentView(R.layout.activity_list);
            Bundle b = getIntent().getExtras();

            if (b != null){
                user = b.getParcelable("User");
            }
            myLocation = user.getLocation();
            initList();

            if (isPlayServicesAvailable()) {
                initMap();
            }

        }
    }

    protected boolean isPlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (status == ConnectionResult.SUCCESS) {
            Toast.makeText(getApplicationContext(), "Connection Success", Toast.LENGTH_SHORT).show();
            return (true);
        } else if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
            Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "You're beat", Toast.LENGTH_SHORT).show();
        }

        return (false);
    }


    private void initMap() {
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.fragment1)).getMap();

        //Loop through Restaurant array and add to map
        for (int i = 0; i < array.size(); i++) {
            Restaurant r = array.get(i);

            map.addMarker(new MarkerOptions()
                .position(new LatLng(r.getLat(),r.getLong()))
                .title(r.getName())
                .snippet(r.getType()));
        }

        // Move the camera instantly to hamburg with a zoom of 15.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(array.get(1).getLat(), array.get(1).getLong()), 6));

        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);
    }


    private void initList() {
        mListView = (ListView) findViewById(R.id.list);

        //Add Restaurants to array.
        array.add(new Restaurant("Alto Cinco","Mexican",43.041165,-76.119486));
        array.add(new Restaurant("Pastabilities", "Italian",43.04831,-76.155311));
        array.add(new Restaurant("Dinosaur Bar-B-Que", "Southern BBQ",43.05249,-76.15467));
        array.add(new Restaurant("The Mission Restaurant", "Mexican",43.04812,	-76.14742));
        array.add(new Restaurant("Francesca's Cucina", "American",43.058505,-76.152333));
        array.add(new Restaurant("Tully's Good Times", "American",43.056009,-76.088804));
        array.add(new Restaurant("Lemon Grass", "Thai",43.047555,-76.154434));
        array.add(new Restaurant("Stella's Diner", "American",43.06908,-76.165269));

        //Create list adapter with layout and array of restaurants to populate
        adapter = new MyAdapter(getApplicationContext(), R.layout.listview_item, array, myLocation);
        //Set list adapter
        mListView.setAdapter(adapter);
        //Handle onclick to push all the information of click restaurant
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), ResActivity.class);
                i.putExtra("name", array.get(position).getName());
                i.putExtra("type", array.get(position).getType());
                i.putExtra("lat", array.get(position).getLat());
                i.putExtra("lng", array.get(position).getLong());
                i.putExtra("user", user);
                startActivity(i);
                Log.d("view DEBUG", "RESTAURANT CLICK!");
            }
        });
    }
}
