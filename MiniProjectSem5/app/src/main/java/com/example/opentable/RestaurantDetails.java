package com.example.opentable;

import java.util.ArrayList;

public class RestaurantDetails {
    public String restaurant_name, description, tags;
    public double latitude, longitude;
    public int likesCount, reportCount;
    public String title, location;
    public boolean badge;
    public String userName;
    public ArrayList<String> likedUsers, reportedUsers;

    public RestaurantDetails(String restaurant_name, String description,
                             String tags, double latitude, double longitude,
                             ArrayList<String> likedUsers, ArrayList<String> reportedUsers,
                             String location,
                             String userName)
    {
        this.description = description;
        this.restaurant_name = restaurant_name;
        this.tags = tags;
        this.latitude = latitude;
        this.longitude = longitude;
        this.likesCount = this.reportCount = 0;
        this.badge = false;
        this.likedUsers = likedUsers;
        this.reportedUsers = reportedUsers;

        // title - this field must be added in the ui
        this.title = "title --";
        this.location = location;
        this.userName = userName;
    }

    // the required default constructor
    public RestaurantDetails(){}

}
