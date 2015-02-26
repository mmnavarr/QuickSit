package com.cyberplays.quicksit;

import android.os.Parcelable;
import android.os.Parcel;
import android.os.IBinder;
import android.location.*;

/**
 * Created by Sam on 2/26/15.
 */
public class User implements Parcelable{
//
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

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(partySize);
        out.writeInt(range);
        out.writeValue(location);
    }

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
