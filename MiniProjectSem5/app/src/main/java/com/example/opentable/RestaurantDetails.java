package com.example.opentable;

public class RestaurantDetails {
    public String restaurant_name, description, tags;
    public double latitude, longitude;
    public int likesCount, reportCount;
    public String title, location;
    public boolean badge;

    public RestaurantDetails(String restaurant_name, String description,
                             String tags, double latitude, double longitude)
    {
        this.description = description;
        this.restaurant_name = restaurant_name;
        this.tags = tags;
        this.latitude = latitude;
        this.longitude = longitude;
        this.likesCount = this.reportCount = 0;
        this.badge = false;


        // title - this field must be added in the ui
        this.title = "title --";
        this.location = "location";
    }

    // the required default constructor
    public RestaurantDetails(){}

}
