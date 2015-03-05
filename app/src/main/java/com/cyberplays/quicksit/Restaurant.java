package com.cyberplays.quicksit;

import android.location.Location;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by malcolmnavarro on 2/24/15.
 */
public class Restaurant {

    protected String name;
    protected String type;

    protected LatLng loc;
    protected Location userLocation;

    public Restaurant(String name, String type,LatLng loc) {
        this.name = name;
        this.type = type;

        this.loc = loc;
        this.userLocation = new Location("User");
        userLocation.setLatitude(loc.latitude);
        userLocation.setLongitude(loc.longitude);

    }

    public Restaurant(String name, String type) {
        this.name = name;
        this.type = type;

    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public double getDist(Location l) {
        return round((userLocation.distanceTo(l)/1609.34),2);
    }

    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public LatLng getLoc() {
        return loc;
    }
    public void setLoc(LatLng loc) {
        this.loc = loc;
    }
}
