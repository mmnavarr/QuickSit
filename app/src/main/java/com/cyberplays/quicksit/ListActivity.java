package com.cyberplays.quicksit;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


public class ListActivity extends Activity {


    static final LatLng HAMBURG = new LatLng(53.558, 9.927);
    static final LatLng KIEL = new LatLng(53.551, 9.993);
    private GoogleMap map;
    private ListView mListView;

    private ArrayList<Restaurant> array = new ArrayList<Restaurant>();
    private MyAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            setContentView(R.layout.activity_list);

            if (isPlayServicesAvailable()) {
                initMap();
            }

            initList();

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
        map = ((MapFragment) this.getFragmentManager().findFragmentById(R.id.fragment)).getMap();

        Marker hamburg = map.addMarker(new MarkerOptions()
                .position(HAMBURG)
                .title("Hamburg")
                .snippet("Hamburg is cool too"));

        Marker kiel = map.addMarker(new MarkerOptions()
                .position(KIEL)
                .title("Kiel")
                .snippet("Kiel is cool"));

        // Move the camera instantly to hamburg with a zoom of 15.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(HAMBURG, 15));

        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
    }

    private void initList() {
        mListView = (ListView) findViewById(R.id.list);

        array.add(new Restaurant("Alto Cinco","Mexican", 1.6));
        array.add(new Restaurant("Pastabilities", "Italian", 2.0));
        array.add(new Restaurant("Dinosaur Bar-B-Que", "Southern BBQ", 2.3));
        array.add(new Restaurant("The Mission Restaurant", "Mexican", 2.9));
        array.add(new Restaurant("Francesca's Cucina", "American", 2.9));
        array.add(new Restaurant("Tully's Good Times", "American", 3.0));
        array.add(new Restaurant("Lemon Grass", "Thai", 3.3));
        array.add(new Restaurant("Stella's Diner", "American", 3.7));

        adapter = new MyAdapter(getApplicationContext(), R.layout.listview_item, array);

        mListView.setAdapter(adapter);

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //INTENT TO RESACTIVITY
                return true;
            }
        });


    }
}
