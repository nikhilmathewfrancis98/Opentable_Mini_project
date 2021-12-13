package com.example.opentable;

import com.google.android.gms.maps.model.LatLng;

public class ModalRestaurantBin {

    LatLng latLng;
    String restaurantName;
    String docId;

    public ModalRestaurantBin(LatLng latLng, String name, String docId)
    {
        this.latLng = latLng;
        this.restaurantName = name;
        this.docId = docId;
    }


    public LatLng getLatLng() {
        return latLng;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getDocId() {
        return docId;
    }
}
