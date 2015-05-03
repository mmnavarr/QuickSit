package com.cyberplays.quicksit;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class ResActivity extends ActionBarActivity implements DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener, View.OnTouchListener{
    //views
    private TextView name, addr, type, wait;
    private Button menu, yelp, make;
    private EditText input;

    //for google maps/locations
    private GoogleMap map;
    private double lat, lng;

    //objects related to user/host interaction
    private User user;
    private Restaurant restaurant;
    private String res_date, res_time, res_name, res_id, party_size;

    //for reservations
    private static final String TIME_PATTERN = "HH:mm";
    private Calendar calendar;
    private DateFormat dateFormat;
    private SimpleDateFormat timeFormat;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            setContentView(R.layout.activity_res);
            calendar = Calendar.getInstance();


            dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
            timeFormat = new SimpleDateFormat(TIME_PATTERN, Locale.getDefault());
            //SET ACTION BAR COLOR
            ActionBar bar = getSupportActionBar();
            bar.hide();




            Bundle b = getIntent().getExtras();
            if (b != null){
                user = b.getParcelable("user");
                restaurant = b.getParcelable("restaurant");
            }
            // Get latitude and longitude of user location for use in the google maps API
            lat = restaurant.getLat();
            lng = restaurant.getLong();
            // Get string representations of these fields for database use
            res_id = Integer.toString(restaurant.getResId());
            party_size = Integer.toString(user.getSize());
            res_name = null;

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

        //Initialize buttons separately to make code cleaner
        initButtons();
    }

    // Checks if google play services are available
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

        //get the LatLng representation of the restaurant's location
        LatLng loc = new LatLng(lat,lng);

        //Add that specific restaurant to the map
        map.addMarker(new MarkerOptions()
                .position(loc)
                .title(restaurant.getName())
                .snippet(restaurant.getType())
                );

        // Move the camera instantly to restaurant location with a zoom of 15.
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

    //Method to initialize all buttons in the activity
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
                    //style for un-touch
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
                    //style for un-touch
                    yelp.setBackgroundColor(getResources().getColor(R.color.white));
                    yelp.setTextColor(getResources().getColor(R.color.shittyRoses));
                }
                return false;
            }
        });


        make = (Button) findViewById(R.id.res_make);
        make.setOnTouchListener(this);
    }

    //Method called when the user selects a date in the date picker dialog
    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(year, monthOfYear, dayOfMonth); // Store the selected date
        res_date = dateFormat.format(calendar.getTime());
        TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY),// Open the time picker dialog
                calendar.get(Calendar.MINUTE), true).show(getFragmentManager(), "timePicker");
    }
    // Method called when the user selects a time in the time picker dialog
    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay); //Store relevant info.
        calendar.set(Calendar.MINUTE, minute);
        res_time = timeFormat.format(calendar.getTime());
        openNameDialog(); //Open the name setter dialog

    }
    // Method which handles button touches. Required for implementing View.OnTouchListener.
    // Button functionality set in initButtons() could also be set here.
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.res_make: {// If the user selects the "make reservation" button
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    //style for touch
                    make.setBackgroundColor(getResources().getColor(R.color.white));
                    make.setTextColor(getResources().getColor(R.color.shittyRoses));

                    if (restaurant.takesReservations() == 1) {// Make sure the restaurant takes reservations
                        DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)).show(getFragmentManager(), "datePicker");
                        // open the dialog to pick a date, which will lead into prompting for all other info.

                    } else {
                        Toast.makeText(getApplicationContext(), "This restaurant does not take reservations.",
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    //style for un-touch
                    make.setBackgroundColor(getResources().getColor(R.color.shittyRoses));
                    make.setTextColor(getResources().getColor(R.color.white));
                }
                break;
            }

        }
        return false;
    }

    //Method which opens a dialog so the user can give a name for their reservation
    public void openNameDialog(){
        LayoutInflater layoutInflater = LayoutInflater.from(this.getApplicationContext());
        View dialogView = layoutInflater.inflate(R.layout.dialog_setname, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setView(dialogView);
        //INITIALIZE EDIT TEXT
        input = (EditText) dialogView.findViewById(R.id.dialog_setname_setname);


        //SETUP DIALOG WINDOW
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //GET USER INPUT FOR WAIT TIME
                        res_name = input.getText().toString();
                        Toast.makeText(getApplicationContext(), "Request sent.", Toast.LENGTH_SHORT).show();
                        new PostResAsyncTask().execute(res_id,res_name,party_size,res_date,res_time);
                        //Once the user gives a name and selects okay, the reservation request is sent to the restaurant


                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,	int id) {
                                dialog.cancel();}});

        //CREATE THE ALERT DIALOG
        AlertDialog alert = alertDialogBuilder.create();
        // SHOW THE ALERT
        alert.show();
    }

    //ASYNC TASK TO POST WIAT TIME TO DB
    class PostResAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String res_id = params[0];
            String name = params[1];
            String p_size = params[2];
            String res_date = params[3];
            String res_time = params[4];

            HttpClient httpClient = new DefaultHttpClient();

            // In a POST request, we don't pass the values in the URL.
            //Therefore we use only the web page URL as the parameter of the HttpPost argument
            final String url = "http://cyberplays.com/quicksit/webservice/make_reservation.php";
            HttpPost httpPost = new HttpPost(url);

            // Because we are not passing values over the URL, we should have a mechanism to pass the values that can be
            //uniquely separate by the other end.
            //To achieve that we use BasicNameValuePair
            //Things we need to pass with the POST request
            BasicNameValuePair resIdPair = new BasicNameValuePair("rest_id", res_id);
            BasicNameValuePair namePair = new BasicNameValuePair("name", name);
            BasicNameValuePair pSizePair = new BasicNameValuePair("p_size", p_size);
            BasicNameValuePair resDatePair = new BasicNameValuePair("rest_date", res_date);
            BasicNameValuePair resTimePair = new BasicNameValuePair("rest_time", res_time);


            // We add the content that we want to pass with the POST request to as name-value pairs
            //Now we put those sending details to an ArrayList with type safe of NameValuePair
            ArrayList<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
            nameValuePairList.add(pSizePair);
            nameValuePairList.add(resDatePair);
            nameValuePairList.add(resTimePair);
            nameValuePairList.add(resIdPair);
            nameValuePairList.add(namePair);


            //Encoding POST data
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairList));
            } catch (UnsupportedEncodingException e) {
                // log exception
                e.printStackTrace();
            }

            //making POST request.
            HttpResponse httpResponse;
            try {
                httpResponse = httpClient.execute(httpPost);
                // write response to log
                Log.d("Http Post Response:", httpResponse.toString());
                try {
                    String responseText = EntityUtils.toString(httpResponse.getEntity());
                    Log.d("RESPONSE", responseText);
                    return responseText;
                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.i("Parse Exception", e + "");
                }
            } catch (ClientProtocolException e) {
                // Log exception
                e.printStackTrace();
            } catch (IOException e) {
                // Log exception
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject jsonObject = new JSONObject(result);
                int success = jsonObject.getInt("success");

                if (success == 1) {

                    Toast.makeText(getApplicationContext(), "Your request has been received.", Toast.LENGTH_SHORT).show();

                } else if (success == 0) {
                    Toast.makeText(getApplicationContext(), "Your request has been denied", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}








