package com.cyberplays.quicksit;

import android.location.Location;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.google.android.gms.maps.model.LatLng;

/**
 *
 */
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;
import java.math.RoundingMode;



/**
 *
 */
public class Restaurant implements Parcelable{

    protected String name;
    protected String type;
    private String password;
    private double latitude;
    private double longitude;
    private int waitTime;

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
    public void setWait(int wait){this.waitTime = wait;}
    public int getWait(){return this.waitTime;}

    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Restaurant> CREATOR = new Parcelable.Creator<Restaurant>() {
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(waitTime);
        out.writeString(name);
        out.writeString(type);
        out.writeDouble(latitude);
        out.writeDouble(longitude);
        out.writeValue(userLocation);
    }

    public Restaurant(Parcel in){
        this.waitTime = in.readInt();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.name = in.readString();
        this.type = in.readString();
        this.userLocation = new Location ((Location) in.readValue(Location.class.getClassLoader()));
    }

}
