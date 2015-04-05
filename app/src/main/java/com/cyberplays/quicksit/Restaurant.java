package com.cyberplays.quicksit;

import android.location.Location;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.google.android.gms.maps.model.LatLng;

/**
 *
 */
public class Restaurant {

    protected String name;
    protected String type;
    private String password;
    private double latitude;
    private double longitude;

    protected Location userLocation;

    public Restaurant(String name, String type,double lat, double lon) {
        this.name = name;
        this.type = type;
        this.latitude = lat;
        this.longitude = lon;

        this.userLocation = new Location("User");
        userLocation.setLatitude(lat);
        userLocation.setLongitude(lon);

    }

    public Restaurant(String name, String type, String pass, Location loc){
        this.name = name;
        this.type = type;
        this.password = pass;
        this.userLocation = loc;
    }

    public Restaurant(String name, String pass, Location loc){
        this.name = name;
        this.password = pass;
        this.userLocation = loc;
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

    public double getLat() {
        return this.latitude;
    }

    public double getLong(){
        return this.longitude;
    }
    public Location getLoc() {
        return userLocation;
    }
    public void setLoc(Location loc) {
        this.userLocation = loc;
    }
}
