package com.cyberplays.quicksit;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import static com.google.android.gms.common.GooglePlayServicesUtil.isGooglePlayServicesAvailable;


public class MainActivity extends ActionBarActivity {

    TextView title, rangeTxt, pSizeTxt;
    Button find;
    EditText addr;
    ImageButton currLocation;
    SeekBar rangeBar, pSizeBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            setContentView(R.layout.activity_main);

            initViews();

            //SET ACTION BAR COLOR
            ActionBar bar = getSupportActionBar();
            bar.hide();
        }

    }

    //INITIALIZE THE VIEWS
    private void initViews(){
        //int b = isGooglePlayServicesAvailable(getApplicationContext());


        title = (TextView) findViewById(R.id.title);

        addr = (EditText) findViewById(R.id.address);

        currLocation = (ImageButton) findViewById(R.id.currLocation);


        rangeTxt = (TextView) findViewById(R.id.rangeTxt);
        rangeBar = (SeekBar) findViewById(R.id.range);
        rangeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d("view DEBUG", Integer.toString(progress));
                rangeTxt.setText(Integer.toString(progress) + "mi");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        pSizeTxt = (TextView) findViewById(R.id.pSizeTxt);
        pSizeBar = (SeekBar) findViewById(R.id.pSize);
        pSizeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pSizeTxt.setText(Integer.toString(progress) + "peeps");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        find = (Button) findViewById(R.id.find);
        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ListActivity.class);
                startActivity(i);
                Log.d("view DEBUG","FIND!");
            }
        });
    }

    public LatLng getCurrentLoc() {
        return null;
    }

}
