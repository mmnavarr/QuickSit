package com.cyberplays.quicksit;


import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
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
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

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

    private TextView name, addr, type, wait;
    private Button menu, yelp, make;
    private GoogleMap map;
    private Intent i;
    private double lat, lng;
    private User user;
    private Restaurant restaurant;
    private String resname, restype, yelpurl,menurl;
    private int takesRes;
    private int waitTime;


    //for reservations
    private static final String TIME_PATTERN = "HH:mm";
    private Calendar calendar;
    private DateFormat dateFormat;
    private SimpleDateFormat timeFormat;

    static final int dialog_id = 1; //DatePicker
    static final int dialog_id2= 2; //TimePicker
    int yr,day,month,hour,minute;
    int partySize;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            setContentView(R.layout.activity_res);

            //SET ACTION BAR COLOR
            ActionBar bar = getSupportActionBar();
            bar.hide();




            Bundle b = getIntent().getExtras();
            if (b != null){
                user = b.getParcelable("user");
                restaurant = b.getParcelable("restaurant");
            }
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
                .snippet(restaurant.getType())
                );

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
        make.setOnTouchListener(this);
    }


    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(year, monthOfYear, dayOfMonth);
        TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), true).show(getFragmentManager(), "timePicker");
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.res_make: {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    calendar = Calendar.getInstance();
                    yr = calendar.get(Calendar.YEAR);
                    day = calendar.get(Calendar.DAY_OF_MONTH);
                    month = calendar.get(Calendar.MONTH);
                    hour = calendar.get(Calendar.HOUR_OF_DAY);
                    minute = calendar.get(Calendar.MINUTE);

                    dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
                    timeFormat = new SimpleDateFormat(TIME_PATTERN, Locale.getDefault());
                    //style for touch
                    make.setBackgroundColor(getResources().getColor(R.color.white));
                    make.setTextColor(getResources().getColor(R.color.shittyRoses));

                    if (restaurant.takesReservations() == 1) {
                        DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)).show(getFragmentManager(), "datePicker");


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
            final String url = "http://cyberplays.com/quicksit/webservice/make_reservations.php";
            HttpPost httpPost = new HttpPost(url);

            // Because we are not passing values over the URL, we should have a mechanism to pass the values that can be
            //uniquely separate by the other end.
            //To achieve that we use BasicNameValuePair
            //Things we need to pass with the POST request
            BasicNameValuePair resIdPair = new BasicNameValuePair("res_id", res_id);
            BasicNameValuePair namePair = new BasicNameValuePair("name", name);

            // We add the content that we want to pass with the POST request to as name-value pairs
            //Now we put those sending details to an ArrayList with type safe of NameValuePair
            ArrayList<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
            nameValuePairList.add(resIdPair);
            nameValuePairList.add(namePair);

            try {
                // UrlEncodedFormEntity is an entity composed of a list of url-encoded pairs.
                //This is typically useful while sending an HTTP POST request.
                UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);

                // setEntity() hands the entity (here it is urlEncodedFormEntity) to the request.
                httpPost.setEntity(urlEncodedFormEntity);

                try {
                    // HttpResponse is an interface just like HttpPost.
                    //Therefore we can't initialize them
                    HttpResponse httpResponse = httpClient.execute(httpPost);

                    // According to the JAVA API, InputStream constructor do nothing.
                    //So we can't initialize InputStream although it is not an interface
                    InputStream inputStream = httpResponse.getEntity().getContent();

                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    StringBuilder stringBuilder = new StringBuilder();

                    String bufferedStrChunk = null;

                    while((bufferedStrChunk = bufferedReader.readLine()) != null){
                        stringBuilder.append(bufferedStrChunk);
                    }

                    return stringBuilder.toString();

                } catch (ClientProtocolException cpe) {
                    System.out.println("First Exception caz of HttpResponese :" + cpe);
                    cpe.printStackTrace();
                } catch (IOException ioe) {
                    System.out.println("Second Exception caz of HttpResponse :" + ioe);
                    ioe.printStackTrace();
                }

            } catch (UnsupportedEncodingException uee) {
                System.out.println("An Exception given because of UrlEncodedFormEntity argument :" + uee);
                uee.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //GET HYPED
        }
    }

}








