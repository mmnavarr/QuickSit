package com.cyberplays.quicksit;

import android.content.Intent;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.location.*;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    TextView title, rangeTxt, pSizeTxt;
    Button find;
    EditText addr;
    ImageButton currLocation;
    Boolean lServices;
    SeekBar rangeBar, pSizeBar;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location lastKnownLocation, currentBestLocation;
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    protected int range, partySize;
//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            setContentView(R.layout.activity_main);

            initViews();
        }

    }
//
    //INITIALIZE THE VIEWS
    private void initViews(){
        title = (TextView) findViewById(R.id.title);
        lServices = false;
        addr = (EditText) findViewById(R.id.address);

        currLocation = (ImageButton) findViewById(R.id.currLocation);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                if (isBetterLocation(location,currentBestLocation)){
                    currentBestLocation = location;
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);

        rangeBar = (SeekBar) findViewById(R.id.range);
        rangeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d("view DEBUG", Integer.toString(progress));
                rangeTxt.setText(Integer.toString(progress));
                range = progress;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        rangeTxt = (TextView) findViewById(R.id.rangeTxt);

        pSizeBar = (SeekBar) findViewById(R.id.pSize);
        pSizeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pSizeTxt.setText(Integer.toString(progress));
                partySize = progress;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        pSizeTxt = (TextView) findViewById(R.id.pSizeTxt);

        currLocation = (ImageButton) findViewById(R.id.currLocation);
        currLocation.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                double latitude = currentBestLocation.getLatitude();
                double longitude = currentBestLocation.getLongitude();
                String address = getAddress(latitude, longitude);
                addr.setHint(address);
                lServices = true;
            }
        });



        find = (Button) findViewById(R.id.find);
        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //stop listening for locations
                locationManager.removeUpdates(locationListener);
                List<Address> addresses;
                if (lServices){
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    String address = addr.getText().toString();
                    try {
                        addresses = geocoder.getFromLocationName(address, 1);
                        Address currentBestAddress = addresses.get(0);

                        currentBestLocation = new Location("");
                        currentBestLocation.setLatitude(currentBestAddress.getLatitude());
                        currentBestLocation.setLongitude(currentBestAddress.getLongitude());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else if (currentBestLocation == null) {
                    currentBestLocation = lastKnownLocation;
                }

                //store relevant information in a parcelable user object
                User user = new User(partySize,range,currentBestLocation);

                Intent i = new Intent(getApplicationContext(), ListActivity.class);
                i.putExtra("User",user);
                startActivity(i);
                Log.d("view DEBUG","FIND!");
            }
        });
    }
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    public String getAddress(double lat, double lng) {
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
                Toast.makeText(getApplicationContext(), "latitude and longitude are null", Toast.LENGTH_LONG).show();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
