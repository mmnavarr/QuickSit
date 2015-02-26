package com.cyberplays.quicksit;

import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

    private TextView name, addr, type;
    private GoogleMap map;
    private Intent i;
    private double lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            setContentView(R.layout.activity_res);

            //SET ACTION BAR COLOR
            ActionBar bar = getSupportActionBar();
            bar.hide();
            //bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#CE123E")));

            //Get the Intent that started this activity
            i = getIntent();

            //if Internet -> setup map
            if (isPlayServicesAvailable()) {
                initMap();
            }

            //Init the views with the passed Restaurant data
            initViews();
        }

    }


    protected void initViews() {
        name = (TextView) findViewById(R.id.res_name);
        name.setText(i.getStringExtra("name"));

        addr = (TextView) findViewById(R.id.res_addr);
        addr.setText(getAddress());

        type = (TextView) findViewById(R.id.res_type);
        type.setText(i.getStringExtra("type") + " Cuisine");

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
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.fragment2)).getMap();

        //Get latitude and logitude
        lat = i.getDoubleExtra("lat", 43.041165);
        lng = i.getDoubleExtra("lng", -76.119486);
        LatLng loc = new LatLng(lat,lng);

        //Add that specific restaurant to the map
        map.addMarker(new MarkerOptions()
                .position(loc)
                .title(i.getStringExtra("name"))
                .snippet(i.getStringExtra("type")));

        // Move the camera instantly to hamburg with a zoom of 15.
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


    //MENU BUTTON ONCLICKLISTENER
    public void menuClick(View v) {
        //Intent webIntent = Intent();
    }

    //YELP BNUTTON ONCLICKLISTENER
    public void yelpClick(View v) {

    }

    //MAKE RESERVATION BUTTON ONCLICKLISTENER
    public void makeClick(View v) {

    }


}
