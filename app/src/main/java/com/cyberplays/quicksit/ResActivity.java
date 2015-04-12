package com.cyberplays.quicksit;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.util.List;


public class ResActivity extends ActionBarActivity {

    private TextView name, addr, type;
    private Button menu, yelp, make;
    private GoogleMap map;
    private Intent i;
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
            //bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#CE123E")));

            //Get the Intent that started this activity
            i = getIntent();
            user = i.getParcelableExtra("user");
            restaurant = i.getParcelableExtra("restaurant");

            lat = restaurant.getLat();
            lng = restaurant.getLong();

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
        name.setText(restaurant.getName());

        addr = (TextView) findViewById(R.id.res_addr);
        addr.setText(getAddress());

        type = (TextView) findViewById(R.id.res_type);
        type.setText(restaurant.getType() + " Cuisine");

        initButtons();
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
        lat = restaurant.getLat();
        lng = restaurant.getLong();

        LatLng loc = new LatLng(lat,lng);

        //Add that specific restaurant to the map
        map.addMarker(new MarkerOptions()
                .position(loc)
                .title(restaurant.getName())
                .snippet(restaurant.getType()));

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
                    String url = "http://altocinco.net/menu/";
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
                    //stlye for touches
                    yelp.setBackgroundColor(getResources().getColor(R.color.shittyRoses));
                    yelp.setTextColor(getResources().getColor(R.color.white));

                    //MAKE A YELP CALL....

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

                    Toast.makeText(getApplicationContext(), "Make Res Call", Toast.LENGTH_SHORT).show();

                    /*AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());

                    alertDialogBuilder.setView(R.layout.make_dialog);

                    // set dialog message
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //HTTPOST REQUEST TO HOST SIDE
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            dialog.cancel();
                                        }
                                    });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();*/
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
