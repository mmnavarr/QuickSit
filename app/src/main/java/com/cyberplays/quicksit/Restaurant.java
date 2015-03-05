package com.cyberplays.quicksit;

import android.location.Location;

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
        return userLocation.distanceTo(l);
    }

    public LatLng getLoc() {
        return loc;
    }
    public void setLoc(LatLng loc) {
        this.loc = loc;
    }
}
