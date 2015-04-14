package com.cyberplays.quicksit;


import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import java.util.List;


public class ResActivity extends ActionBarActivity {

    private TextView name, addr, type, wait;
    private Button menu, yelp, make;
    private GoogleMap map;
    private double lat, lng;
    private User user;
    private Restaurant restaurant;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            setContentView(R.layout.activity_res);

            //SET ACTION BAR COLOR
            ActionBar bar = getSupportActionBar();
            bar.hide();

            //Get the Intent bundle that started this activity to get objects
            Bundle b = getIntent().getExtras();
            if (b != null){
                user = b.getParcelable("user");
                restaurant = b.getParcelable("restaurant");
            }
            //GET RESTAURANT LAT n LNG
            lat = restaurant.getLat();
            lng = restaurant.getLong();

            //IF INTERNET -> SETUP MAP
            if (isPlayServicesAvailable()) {
                initMap();
            }

            //INIT THE VIEWS WITH RESTAURANT DATA FIELDS
            initViews();
        }

    }


    //INITIALIZE VIEWS
    protected void initViews() {
        name = (TextView) findViewById(R.id.res_name);
        name.setText(restaurant.getName());

        addr = (TextView) findViewById(R.id.res_addr);
        addr.setText(getAddress());

        type = (TextView) findViewById(R.id.res_type);
        type.setText(restaurant.getType() + " Cuisine");

        wait = (TextView) findViewById(R.id.wait_time);
        wait.setText(Integer.toString(restaurant.getWait()) + " min.");

        initButtons();
    }


    //CHECK FOR PLAY SERVICES
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

    //INITIALIZE GOOGLE MAP + POPULATE
    private void initMap() {
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.fragment2)).getMap();

        LatLng loc = new LatLng(lat,lng);

        //Add that specific restaurant to the map
        map.addMarker(new MarkerOptions()
                .position(loc)
                .title(restaurant.getName())
                .snippet(restaurant.getType()));

        // Move the camera instantly to restaurant with a zoom of 15.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 6));

        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
    }

    //Used to convert LatLng to actual readable address
    public String getAddress() {
        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(getApplicationContext());
            if (lat != 0 || lng != 0) {
                addresses = geocoder.getFromLocation(lat,lng, 1);
                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getAddressLine(1);
                String country = addresses.get(0).getAddressLine(2);
                String fullAddr = (address + ", " + city);
                return fullAddr;
            } else {
                Toast.makeText(getApplicationContext(), "latitude and longitude are null",Toast.LENGTH_LONG).show();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //INTIALIZE BUTTONS
    public void initButtons() {
        menu = (Button) findViewById(R.id.menu);
        menu.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    //stlye for touches
                    menu.setBackgroundColor(getResources().getColor(R.color.shittyRoses));
                    menu.setTextColor(getResources().getColor(R.color.white));


                    //Send them to menu URL
                    String url = restaurant.getMenuURL();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    //style for un-touche
                    menu.setBackgroundColor(getResources().getColor(R.color.white));
                    menu.setTextColor(getResources().getColor(R.color.shittyRoses));
                 }
                return false;
            }
        });

        yelp = (Button) findViewById(R.id.yelp);
        yelp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    //Style
                    yelp.setBackgroundColor(getResources().getColor(R.color.shittyRoses));
                    yelp.setTextColor(getResources().getColor(R.color.white));

                    //Send them to YELP URL getyelpurl
                    String url = restaurant.getYelpURL();
                    Log.d("URL STRING:", url);
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    //style for un-touche
                    yelp.setBackgroundColor(getResources().getColor(R.color.white));
                    yelp.setTextColor(getResources().getColor(R.color.shittyRoses));
                }
                return false;
            }
        });

        make = (Button) findViewById(R.id.res_make);
        make.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    //style for touch
                    make.setBackgroundColor(getResources().getColor(R.color.white));
                    make.setTextColor(getResources().getColor(R.color.shittyRoses));
                    if (restaurant.takesReservations()==0){
                        Toast.makeText(getApplicationContext(), "This restaurant does not take reservations.",
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }


                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    //style for un-touch
                    make.setBackgroundColor(getResources().getColor(R.color.shittyRoses));
                    make.setTextColor(getResources().getColor(R.color.white));
                }
                return false;
            }
        });

    }


}
