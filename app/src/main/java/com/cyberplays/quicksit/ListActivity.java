package com.cyberplays.quicksit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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

    // url to get all restaurants list
    private static String url_all_restaurants = "http://cyberplays.com/quicksit/webservice/connect_and_pull_rests.php";

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

    //CREATE RESTAURANT JSON ARRAY
    JSONArray restaurants = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            setContentView(R.layout.activity_list);
            Bundle b = getIntent().getExtras();

            //CALL HTTPREQUEST IN BACKGROUND ASYNCTASK TO DB
            new LoadAllRestaurants().execute();

            if (b != null){
                user = b.getParcelable("user");
            }

            Log.d("view DEBUG", Integer.toString(user.getSize()));
            myLocation = user.getLocation();

        }
    }

    //CHECK IF PLAY SERVICES ARE AVAILABLE
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


    //INITIALIZE GOOGLE MAP FOR PAGE
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
                //PASS FIELDS NEEDED IN NEXT ACTIVITY
                /*i.putExtra("name", array.get(position).getName());
                i.putExtra("type", array.get(position).getType());
                i.putExtra("lat", array.get(position).getLat());
                i.putExtra("lng", array.get(position).getLong());
                i.putExtra("wait", array.get(position).getWait());
                i.putExtra("menu", array.get(position).getMenuURL());
                i.putExtra("yelp", array.get(position).getYelpURL());
                i.putExtra("takesRes", array.get(position).takesReservations());
*/
                Bundle b = new Bundle();
                b.putParcelable("user", user);
                Restaurant rest = array.get(position);
                b.putParcelable("restaurant", rest);

                i.putExtras(b);
                //i.putExtra("restaurant", array.get(position));
                //i.putExtra("user", user);
                startActivity(i);
                Log.d("view DEBUG", "RESTAURANT CLICK!");
            }
        });
    }

    //Background Async Task to Load all restaurants by making HTTP Request
    class LoadAllRestaurants extends AsyncTask<String, String, String> {

        //DECLARE RESTAURANT ARRAYLIST FOR LIST
        public ArrayList<Restaurant> rests = new ArrayList<Restaurant>();

        InputStream is = null;
        JSONObject jObj = null;
        String json = null;
        HttpResponse httpResponse = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ListActivity.this);
            pDialog.setMessage("Loading restaurants. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        //GET ALL RESTAURANTS IN BACKGROUND THREAD
        protected String doInBackground(String... args) {
            //CALL getJSONFromURL to obtain JSONObject
            JSONObject json = getJSONFromUrl(url_all_restaurants);


            //TO CHECK IF THE JSON IS NULL
            if (json == null)
                Log.d("JSON IS NULL:", "YES");
            else {
                Log.d("JSON IS NULL:", "NO");
            }


            // Check your log cat for JSON reponse
            Log.d("All Restaurants: ", json.toString());


            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    Log.d("SUCCESS?", "YEAHH - 1");
                    // restaurants found
                    // Getting Array of Restaurants
                    restaurants = json.getJSONArray(TAG_RESTAURANTS);
                    Log.d("JSON ARRAY",restaurants.toString());


                    // looping through All Restaurants and add to array
                    for (int i = 0; i < restaurants.length(); i++) {
                        JSONObject c = restaurants.getJSONObject(i);

                        // Storing each json item in variable
                        String rest_name = c.getString(TAG_REST_NAME);
                        String rest_type = c.getString(TAG_REST_TYPE);
                        String rest_yelp = c.getString(TAG_REST_YELP);
                        String rest_menu = c.getString(TAG_REST_MENU);
                        String rest_phone = c.getString(TAG_REST_PHONE);
                        int rest_reservation_temp = c.getInt(TAG_REST_RES);

                        Boolean rest_reservation = Boolean.TRUE;
                        if (rest_reservation_temp == 0) {
                            rest_reservation = Boolean.FALSE;
                        } else if (rest_reservation_temp == 1) {
                            rest_reservation = Boolean.TRUE;
                        }

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

        //After completing background task Dismiss the progress dialog and set up UI
        @Override
        protected void onPostExecute(String nothing) {
            //UPDATE ARRAY OF RESTAURANTS WITH ONES LOADES FROM DB
            array = rests;

            // DISMISS DIALOG AFTER THE DB HAS BEENN PULLED
            pDialog.dismiss();

            //START POPULATING
            if (isPlayServicesAvailable()) {
                initMap();
            }

            initList();
        }

        //METHOD TO MAKE HTTP REQUEST AND OBTAIN RESTAURANT JSON
        public JSONObject getJSONFromUrl(String url) {
            // Making HTTP request
            try {
                HttpParams params = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(params, 10000);
                HttpConnectionParams.setSoTimeout(params, 10000);
                HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
                HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
                HttpProtocolParams.setUseExpectContinue(params, true);
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient(params);
                HttpGet httpPost = new HttpGet(url);
                httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            } catch (UnsupportedEncodingException ee) {
                //Log.i("UnsupportedEncodingException...", is.toString());
            } catch (ClientProtocolException e) {
                //Log.i("ClientProtocolException...", is.toString());
            } catch (IOException e) {
                Log.i("IOException...", is.toString());
            }

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "utf-8"), 8); //old charset iso-8859-1
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                reader.close();
                json = sb.toString();
                Log.i("StringBuilder...", json);
            } catch (Exception e) {
                Log.e("Buffer Error", "Error converting result " + e.toString());
            }
            // try parse the string to a JSON object
            try {
                jObj = new JSONObject(json);
                Log.i("TRY:", "1");
            } catch (Exception e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
                try {
                    jObj = new JSONObject(json.substring(json.indexOf("{"), json.lastIndexOf("}") + 1));
                    Log.i("TRY:", "2");
                } catch (Exception e0) {
                    Log.e("JSON Parser0", "Error parsing data [" + e0.getMessage() + "] " + json);
                    Log.e("JSON Parser0", "Error parsing data " + e0.toString());
                    try {
                        jObj = new JSONObject(json.substring(1));
                        Log.i("TRY:", "3");
                    } catch (Exception e1) {
                        Log.e("JSON Parser1", "Error parsing data [" + e1.getMessage() + "] " + json);
                        Log.e("JSON Parser1", "Error parsing data " + e1.toString());
                        try {
                            jObj = new JSONObject(json.substring(2));
                            Log.i("TRY:", "4");
                        } catch (Exception e2) {
                            Log.e("JSON Parser2", "Error parsing data [" + e2.getMessage() + "] " + json);
                            Log.e("JSON Parser2", "Error parsing data " + e2.toString());
                            try {
                                jObj = new JSONObject(json.substring(3));
                                Log.i("TRY:", "5");
                            } catch (Exception e3) {
                                Log.e("JSON Parser3", "Error parsing data [" + e3.getMessage() + "] " + json);
                                Log.e("JSON Parser3", "Error parsing data " + e3.toString());
                            }
                        }
                    }
                }
            }

            // return JSON String
            return jObj;

        }
    }


    //BUBBLE SORT TO SORT LISTVIEW BASED ON DISTANCE TO RESTAURANTS
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
