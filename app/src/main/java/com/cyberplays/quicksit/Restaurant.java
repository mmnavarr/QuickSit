package com.cyberplays.quicksit;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by malcolmnavarro on 2/24/15.
 */
public class Restaurant {

    protected String name;
    protected String type;
    protected double dist;
    protected LatLng loc;

    public Restaurant(String name, String type, double dist, LatLng loc) {
        this.name = name;
        this.type = type;
        this.dist = dist;
        this.loc = loc;
    }

    public Restaurant(String name, String type, double dist) {
        this.name = name;
        this.type = type;
        this.dist = dist;
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

    public double getDist() {
        return dist;
    }
    public void setDist(double dist) {
        this.dist = dist;
    }

    public LatLng getLoc() {
        return loc;
    }
    public void setLoc(LatLng loc) {
        this.loc = loc;
    }
}
