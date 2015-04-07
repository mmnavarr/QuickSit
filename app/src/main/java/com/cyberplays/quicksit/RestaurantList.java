package com.cyberplays.quicksit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;


import android.app.ProgressDialog;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;



public class RestaurantList extends com.cyberplays.quicksit.ListActivity {


    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> restaurantsList;


    //*** TO DO ****
    // url to get all restaurants list
    private static String url_all_restaurants = "http://cyberplays.com/quicksit/webservice/get_all_restaurants.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_RESTAURANTS = "restaurants";
    private static final String TAG_REST_ID = "rest_id";
    private static final String TAG_REST_NAME = "rest_name";

    // create Restaurants JSONArray
    JSONArray restaurants = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.all_restaurants);

        // Hashmap for ListView
        restaurantsList = new ArrayList<HashMap<String, String>>();

        // Loading restaurants in Background Thread
        new LoadAllRestaurants().execute();


    }

     //Background Async Task to Load all product by making HTTP Request
    class LoadAllRestaurants extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RestaurantList.this);
            pDialog.setMessage("Loading restaurants. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting all restaurants from url
         * */
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
                    // products found
                    // Getting Array of Products
                    restaurants = json.getJSONArray(TAG_RESTAURANTS);

                    // looping through All Products
                    for (int i = 0; i < restaurants.length(); i++) {
                        JSONObject c = restaurants.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString(TAG_REST_ID);
                        String name = c.getString(TAG_REST_NAME);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_REST_ID, id);
                        map.put(TAG_REST_NAME, name);

                        // adding HashList to ArrayList
                        restaurantsList.add(map);
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
        protected void onPostExecute(String file_url) {

            // dismiss the dialog after getting all products
            pDialog.dismiss();


           /* // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    //Updating parsed JSON data into ListView
                    ListAdapter adapter = new SimpleAdapter(
                            RestaurantList.this, restaurantsList,
                            R.layout.list_item, new String[] { TAG_REST_ID,
                            TAG_REST_NAME},
                            new int[] { R.id.rest_id, R.id.rest_name });
                    // updating listview
                    setListAdapter(adapter);
                }
            } */

        }

    }

}
