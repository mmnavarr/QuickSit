package com.cyberplays.quicksit;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class ResActivity extends ActionBarActivity {

    TextView name, addr, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            setContentView(R.layout.activity_res);

            initViews();
        }

    }


    protected void initViews() {
        name = (TextView) findViewById(R.id.res_name);

        addr = (TextView) findViewById(R.id.res_addr);

        phone = (TextView) findViewById(R.id.res_phone);
    }


    //MENU BUTTON ONCLICKLISTENER
    private void menuClick(View v) {
        //Intent webIntent = Intent();
    }

    //YELP BNUTTON ONCLICKLISTENER
    private void yelpClick(View v) {

    }

    //MAKE RESERVATION BUTTON ONCLICKLISTENER
    private void makeClick(View v) {

    }


}
