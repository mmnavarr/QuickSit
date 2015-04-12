package com.cyberplays.quicksit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
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

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ListActivity extends Activity {

    private GoogleMap map;
    private ListView mListView;

    public ArrayList<Restaurant> array = new ArrayList<Restaurant>();
    private MyAdapter adapter;
    private User user;
    public Location myLocation;

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    org.json.simple.parser.JSONParser jParser = new org.json.simple.parser.JSONParser();

    // url to get all restaurants list
    private static String url_all_restaurants = "http://cyberplays.com/quicksit/webservice/get_all_restaurants.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_RESTAURANTS = "restaurants";
    private static final String TAG_REST_ID = "rest_id";
    private static final String TAG_REST_NAME = "rest_name";
    private static final String TAG_REST_TYPE = "rest_type";
    private static final String TAG_REST_YELP = "rest_yelp";
    private static final String TAG_REST_MENU = "rest_menu";
    private static final String TAG_REST_LAT = "rest_lat";
    private static final String TAG_REST_LONG = "rest_long";
    private static final String TAG_REST_PHONE = "rest_phone";
    private static final String TAG_REST_WAIT = "rest_wait_time";
    private static final String TAG_REST_RES = "rest_res";

    // create Restaurants JSONArray
    JSONArray restaurants = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            setContentView(R.layout.activity_list);
            Bundle b = getIntent().getExtras();

            // Loading restaurants in Background Thread
            new LoadAllRestaurants().execute();

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
        bubbleSort();
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

    //Background Async Task to Load all restaurants by making HTTP Request
    class LoadAllRestaurants extends AsyncTask<String, String, String> {

        //DECLARE RESTAURANT ARRAYLIST FOR LIST
        public ArrayList<Restaurant> rests = new ArrayList<Restaurant>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ListActivity.this);
            pDialog.setMessage("Loading restaurants. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        //getting all restaurants
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json = com.cyberplays.quicksit.JSONParser.makeHttpRequest(url_all_restaurants, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("All Restaurants: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // restaurants found
                    // Getting Array of Restaurants
                    restaurants = json.getJSONArray(TAG_RESTAURANTS);


                    // looping through All Restaurants
                    for (int i = 0; i < restaurants.length(); i++) {
                        JSONObject c = restaurants.getJSONObject(i);

                        // Storing each json item in variable
                        String rest_name = c.getString(TAG_REST_NAME);
                        String rest_type = c.getString(TAG_REST_TYPE);
                        String rest_yelp = c.getString(TAG_REST_YELP);
                        String rest_menu = c.getString(TAG_REST_MENU);
                        String rest_phone = c.getString(TAG_REST_PHONE);
                        Boolean rest_reservation = c.getBoolean(TAG_REST_RES);
                        double rest_lat = c.getDouble(TAG_REST_LAT);
                        double rest_long = c.getDouble(TAG_REST_LONG);
                        int rest_wait = c.getInt(TAG_REST_WAIT);


                        Restaurant r = new Restaurant(rest_name,rest_type,rest_yelp,rest_menu,rest_phone,rest_reservation,rest_lat,rest_long,rest_wait);
                        rests.add(r);
                    }

                } else {
                    return null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        //After completing background task Dismiss the progress dialog
        protected void onPostExecute() {
            array = rests;

            // dismiss the dialog after getting all restaurants
            pDialog.dismiss();
        }

    }

    public void bubbleSort(){
        int n = array.size();
        while (n > 0){
            int n2 = 0;
            for (int i = 1; i<= (n-1); i++){
                if (array.get(i-1).getDist(myLocation) > array.get(i).getDist(myLocation)){
                    swap(array,i);
                    n2 = i;
                }
            }
            n = n2;
        }
    }

    public void swap (ArrayList<Restaurant> array, int i){
        Restaurant one = array.get(i-1);
        Restaurant two = array.get(i);
        array.set(i-1,two);
        array.set(i,one);
    }
}
