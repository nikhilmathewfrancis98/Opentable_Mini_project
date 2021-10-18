package com.example.opentable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.internal.ICameraUpdateFactoryDelegate;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;


public class AddNewActivity extends AppCompatActivity implements OnMapReadyCallback {

    Button addBtn;
    GoogleMap googleMap;
    private static int RESULT_LOAD_IMAGE = 1;
    List<String> imagesEncodedList;
    List<Uri> Imguri;
    FusedLocationProviderClient client;
    SupportMapFragment supportMapFragment;
    String imageEncoded;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);
        Imguri = new ArrayList<>();
        // google map
        supportMapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.googleMap);
        //  permission granding
        client= LocationServices.getFusedLocationProviderClient(this);

        if(ActivityCompat.checkSelfPermission(AddNewActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            // When permission granded
            getCurrentLocation();
        }else {
            // When permission denied
            //  Request permission
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);

        }
        supportMapFragment.getMapAsync(this);


    // for uploading new images
        addBtn = findViewById(R.id.addButton);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED){

                    // ask for file access permision
                    String []permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    requestPermissions(permissions, 1001);

                }
                else // if already permission has been granted
                {
                    pickImage();

                }
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

    public void pickImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), 1);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case 1001: // for picking images
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    pickImage();
                }
                else
                {
                    Toast.makeText(this, "Permission denied !!", Toast.LENGTH_SHORT).show();
                }
            case 44: // for map
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    getCurrentLocation();
                }
                else
                {
                    Toast.makeText(this, "Permission denied !!", Toast.LENGTH_SHORT).show();
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    // after selecting an image from gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (resultCode == RESULT_OK && requestCode == 1){
            Imguri.clear();
            Uri imageUri = data.getData();
            Imguri.add(imageUri);

            LinearLayout layout = (LinearLayout) findViewById(R.id.linear);
            for (int i = 0; i < Imguri.size(); i++) {
                ImageView imageView = new ImageView(this);
                imageView.setId(i);
                imageView.setPadding(2, 2, 2, 2);
                imageView.setImageURI(Imguri.get(i));
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                layout.addView(imageView);
                imageView.getLayoutParams().height = 250;
                imageView.getLayoutParams().width = 250;
                imageView.requestLayout();
            }
        }



//        try {
//            // When an Image is picked
//            if (requestCode == 1 && resultCode == RESULT_OK
//                    && null != data) {
//                // Get the Image from data
//
//                String[] filePathColumn = { MediaStore.Images.Media.DATA };
//                imagesEncodedList = new ArrayList<String>();
//                if(data.getData()!=null){
//
//                    Uri mImageUri=data.getData();
//
//                    // Get the cursor
//                    Cursor cursor = getContentResolver().query(mImageUri,
//                            filePathColumn, null, null, null);
//                    // Move to first row
//                    cursor.moveToFirst();
//
//                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                    imageEncoded  = cursor.getString(columnIndex);
//                    cursor.close();
//
//                            Toast.makeText(this, "On Time", Toast.LENGTH_SHORT).show();
//                }
//                else
//                {
//                    if (data.getClipData() != null) {
//                        ClipData mClipData = data.getClipData();
//                        ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
//                        for (int i = 0; i < mClipData.getItemCount(); i++) {
//
//                            ClipData.Item item = mClipData.getItemAt(i);
//                            Uri uri = item.getUri();
//                            mArrayUri.add(uri);
//                            // Get the cursor
//                            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
//                            // Move to first row
//                            cursor.moveToFirst();
//
//                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                            imageEncoded  = cursor.getString(columnIndex);
//                            imagesEncodedList.add(imageEncoded);
//                            cursor.close();
//                        }
////                        Log.v("LOG_TAG", "Selected Images" + mArrayUri.get(0));
//
//                        LinearLayout layout = (LinearLayout) findViewById(R.id.linear);
//                        for (int i = 0; i < mArrayUri.size(); i++) {
//                            ImageView imageView = new ImageView(this);
//                            imageView.setId(i);
//                            imageView.setPadding(2, 2, 2, 2);
//                            imageView.setImageURI(mArrayUri.get(i));
//                            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//                            layout.addView(imageView);
//                            imageView.getLayoutParams().height = 250;
//                            imageView.getLayoutParams().width = 250;
//                            imageView.requestLayout();
//                        }
////                        Log.d("hello", imagesEncodedList.toString());
//                        Toast.makeText(this, Integer.toString(mArrayUri.size()), Toast.LENGTH_SHORT).show();
//                    Log.d("theSize", Integer.toString(mArrayUri.size()));
//                    }
//                }
//            } else {
//                Toast.makeText(this, "You haven't picked Image",
//                        Toast.LENGTH_LONG).show();
//            }
//        } catch (Exception e) {
//            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
//                    .show();
//            Log.d("blah", e.toString());
//
//        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        this.googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(latLng.latitude+", "+latLng.longitude);
                googleMap.clear();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                googleMap.addMarker(markerOptions);
            }
        });
    }
}