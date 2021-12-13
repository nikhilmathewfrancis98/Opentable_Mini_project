package com.example.opentable;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

public class ModalForRestaurantTable {
    public GeoPoint geo;
    public String location;
    public String name;
    ArrayList<String> posts;

    public ModalForRestaurantTable(){}

    public ModalForRestaurantTable(GeoPoint geoPoint, String location, String name, ArrayList<String> posts)
    {
        this.geo = geoPoint;
        this.location = location;
        this.name = name;
        this.posts = posts;

    }


    public String getLocation() {
        return location;
    }

    public ArrayList<String> getPosts() {
        return posts;
    }


    public GeoPoint getGeo() {
        return geo;
    }

    public String getName() {
        return name;
    }


}
