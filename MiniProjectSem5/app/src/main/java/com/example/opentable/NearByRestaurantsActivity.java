package com.example.opentable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class NearByRestaurantsActivity extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap googleMap;
    FusedLocationProviderClient client;
    SupportMapFragment supportMapFragment;
    List<LatLng> latLngList;
    FloatingActionButton moreButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_by_restaurants);


        latLngList = new ArrayList<>();
        latLngList.add(new LatLng(8.545337, 76.908380));
        latLngList.add(new LatLng(8.545846, 76.908192));
        latLngList.add(new LatLng(8.545585, 76.908084));
        latLngList.add(new LatLng(8.545547, 76.908418));
//----------------------------------         google map    -----------------------------------------------
        supportMapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.googleMap);

        //  permission granding
        client= LocationServices.getFusedLocationProviderClient(this);

        if(ActivityCompat.checkSelfPermission(NearByRestaurantsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            // When permission granded
            getCurrentLocation();
        }else {
            // When permission denied
            //  Request permission
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);

        }
        supportMapFragment.getMapAsync(this);
//----------------------------------------------------------------------------------------------------------



    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        googleMap.clear();
        for(LatLng latLng: latLngList)
        {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(latLng.latitude+", "+latLng.longitude);
            googleMap.addMarker(markerOptions);
        }
    }

    @SuppressLint("MissingPermission")
    private void  getCurrentLocation() {


        Task<Location> task;

        task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {
                            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                            MarkerOptions options= new MarkerOptions().position(latLng).title("I am here");
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
                            googleMap.addMarker(options);
                        }
                    });
                }
            }
        });

    }


}