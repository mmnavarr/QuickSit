package com.cyberplays.quicksit;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
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
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

import android.app.DatePickerDialog;
import android.app.Dialog;

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
    private Intent i;
    private double lat, lng;
    private User user;
    private Restaurant restaurant;

    static final int dialog_id = 1; //DatePicker
    static final int dialog_id2= 2; //TimePicker
    int yr,day,month,hour,minute;
    int partySize;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            setContentView(R.layout.activity_res);
            Calendar today = Calendar.getInstance();
            yr = today.get(Calendar.YEAR);
            day = today.get(Calendar.DAY_OF_MONTH);
            month = today.get(Calendar.MONTH);
            hour = today.get(Calendar.HOUR_OF_DAY);
            minute = today.get(Calendar.MINUTE);


            //SET ACTION BAR COLOR
            ActionBar bar = getSupportActionBar();
            bar.hide();

            //Get the Intent that started this activity
            i = getIntent();
            //user = i.getParcelableExtra("user");
            //restaurant = i.getParcelableExtra("restaurant");
            Bundle b = getIntent().getExtras();
            if (b != null){
                user = b.getParcelable("user");
                restaurant = b.getParcelable("restaurant");
            }
            lat = restaurant.getLat();
            lng = restaurant.getLong();

            // GET RESTAURANT DATA SINCE PARCELABLE OBJECT ISNT ABLE TO PASS ** FIXXXX
            /*lat = i.getDoubleExtra("lat", 41.11);
            lng = i.getDoubleExtra("lng", 41.11);
            String rName = i.getStringExtra("name");
            String rType = i.getStringExtra("type");
            int rWait = i.getIntExtra("wait", 20);
            String yelpURL = i.getStringExtra("yelp");
            String menuURL = i.getStringExtra("menu");
            Boolean takesRes = i.getBooleanExtra("reservable", Boolean.FALSE);
            restaurant = new Restaurant(rName,rType, lat, lng, rWait, yelpURL, menuURL, takesRes);
*/
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

        wait = (TextView) findViewById(R.id.wait_time);
        wait.setText(Integer.toString(restaurant.getWait()) + " min.");

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

                    if (restaurant.takesReservations()==1){
                        showDialog(dialog_id);
                        showDialog(dialog_id2);

                    }

                    else {
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
        });}

    protected Dialog onCreateDialog(int id) {
        switch(id)
        {
            case dialog_id:
                return new DatePickerDialog(this,mDateSetListener,yr,month,day);
            case dialog_id2:
                return new TimePickerDialog(this,mTimeSetListener,hour,minute,false);
        }

      return null;
    }
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        yr = year;
        month = monthOfYear;
        day = dayOfMonth;

        }
    };

    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view,
                                      int hourOfDay, int hour_minute) {
                    hour = hourOfDay;
                    minute = hour_minute;
                partySize = getIntent().getExtras().getInt("Psize");
                Toast.makeText(getBaseContext(),"Settled Time :"+month+"/" +day +"/" +yr + " at " +hour +":"+minute +" for party of:" +partySize,Toast.LENGTH_LONG).show();


                }
            };

}








