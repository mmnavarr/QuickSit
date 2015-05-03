package com.cyberplays.quicksit;

import android.os.Parcelable;
import android.os.Parcel;
import android.os.IBinder;
import android.location.*;

// A user object
public class User implements Parcelable{

    protected int partySize;
    protected int range;
    protected Location location;

    public User(int pSize, int rng, Location l) {
        this.partySize = pSize;
        this.range = rng;
        this.location = l;
    }

    public User(Parcel in){
        this.partySize = in.readInt();
        this.range = in.readInt();
        this.location = new Location ((Location) in.readValue(Location.class.getClassLoader()));
    }

    // Getters
    public int getSize() {
        return this.partySize;
    }

    public int getRange() {
        return this.range;
    }

    public Location getLocation() {
        return this.location;
    }

    // Setters
    public void setSize(int sz) {
        this.partySize = sz;
    }

    public void setRange(int rng){
        this.range = rng;
    }

    public void setLocation(Location l){
        this.location = l;
    }


    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(partySize);
        out.writeInt(range);
        out.writeValue(location);
    }
    //required for implementing parcelable
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
