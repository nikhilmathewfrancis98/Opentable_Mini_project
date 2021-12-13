package com.example.opentable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NearByRestaurantsActivity extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap googleMap;
    FusedLocationProviderClient client;
    SupportMapFragment supportMapFragment;
    List<ModalRestaurantBin> latLngList;
    FloatingActionButton moreButton;

    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_by_restaurants);

        db = FirebaseFirestore.getInstance();

        latLngList = new ArrayList<>();



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


        GoogleMap gmap = googleMap;

        db.collection("restaurant")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult()))
                            {
                                Map<String, Object> mp = document.getData();
                                GeoPoint geoPoint = (GeoPoint) mp.get("geo");
                                if(geoPoint == null)
                                    return;
                                LatLng latLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());

                                latLngList.add(
                                        new ModalRestaurantBin(
                                                latLng,
                                                mp.get("name").toString(),
                                                document.getId()));

                            }
                            googleMap.clear();
                            for(ModalRestaurantBin latLng: latLngList)
                            {
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(latLng.getLatLng());

                                markerOptions.title(latLng.getDocId());
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng.getLatLng(),15));
                                googleMap.addMarker(markerOptions);
                            }
                        }
                        else
                        {
                            Log.d("Details", "Error getting documents: ", task.getException());
                        }
                    }
                });



        // adding on click listener to marker of google maps.
        gmap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if(marker.getTitle().equals("I am here"))
                    return false;

                Intent intent = new Intent(NearByRestaurantsActivity.this, ViewRestaurantDetails.class);
                intent.putExtra("docId", marker.getTitle());
                startActivity(intent);
                return false;
            }
        });

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


    @Override
    protected void onStart() {









        super.onStart();
    }


    public void handleRecords(Callback callback)
    {

        db.collection("restaurant")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult()))
                            {
                                callback.myResponseCallback(document);

                            }
                        }
                        else
                        {
                            Log.d("Details", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    // ---------------------- CALLBACK INTERFACE ------------------------------------
    interface Callback {
        void myResponseCallback(QueryDocumentSnapshot document);//whatever your return type is: string, integer, etc.
    }


}