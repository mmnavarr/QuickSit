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

    protected String name;    // Name of the restaurant
    protected String type;    // Type of food the restaurant serves
    private String userName;
    private String yelpURL;   // URL for yelp page
    private String menuURL;   // URL for restaurant menu
    private String password;  // Password for database
    private String phone;     // Restaurant phone number
    private Boolean takesRes; // True if the restaurant takes reservations
    private double latitude;  // Latitude of the restaurant's location
    private double longitude; // Longitude of the restaurant's location
    private int waitTime;     // The estimated wait time to eat at the restaurant
    private int capacity;
    private int customers;

    protected Location resLocation; // The restaurant's geographical location

    public Restaurant (String name, String type, String yelp, String menu,
                       String phone, Boolean res, double lat, double lon, int wait){
        this.name = name;
        this.type = type;
        this.yelpURL = yelp;
        this.menuURL = menu;
        this.phone = phone;
        this.takesRes = res;
        this.waitTime = wait;

        this.latitude = lat;
        this.longitude = lon;
        this.resLocation = new Location("User");
        resLocation.setLatitude(lat);
        resLocation.setLongitude(lon);

    }
    public Restaurant(String name, String type,double lat, double lon) {
        this.name = name;
        this.type = type;
        this.latitude = lat;
        this.longitude = lon;
        this.waitTime = 0;
        this.resLocation = new Location("User");
        resLocation.setLatitude(lat);
        resLocation.setLongitude(lon);

    }

    public Restaurant(String name, String type, String pass, Location loc){
        this.name = name;
        this.type = type;
        this.password = pass;
        this.resLocation = loc;

        this.waitTime = 0;
    }

    public Restaurant(String name, String pass, Location loc){
        this.name = name;
        this.password = pass;
        this.resLocation = loc;

        this.waitTime = 0;
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
        return round((resLocation.distanceTo(l)/1609.34),2);
    }
    public boolean takesReservations(){
        return takesRes;
    }
    public double getLat() {
        return this.latitude;
    }
    public double getLong(){
        return this.longitude;
    }
    public Location getLoc() {
        return resLocation;
    }
    public void setLoc(Location loc) {
        this.resLocation = loc;
    }
    public void setWait(int wait){this.waitTime = wait;}
    public int getWait(){return this.waitTime;}
    public String getYelpURL() {
        return yelpURL;
    }
    public void setYelpURL(String yelpURL) {
        this.yelpURL = yelpURL;
    }
    public String getMenuURL() {
        return menuURL;
    }
    public void setMenuURL(String menuURL) {
        this.menuURL = menuURL;
    }
    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

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
        out.writeString(userName);
        out.writeString(yelpURL);
        out.writeString(menuURL);
        out.writeString(phone);
        out.writeDouble(latitude);
        out.writeDouble(longitude);
        out.writeValue(resLocation);


    }

    public Restaurant(Parcel in){
        this.waitTime = in.readInt();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.yelpURL = in.readString();
        this.menuURL = in.readString();
        this.name = in.readString();
        this.type = in.readString();
        this.resLocation = new Location ((Location) in.readValue(Location.class.getClassLoader()));
    }

}
