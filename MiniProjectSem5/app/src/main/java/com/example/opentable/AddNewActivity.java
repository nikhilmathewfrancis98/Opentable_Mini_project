package com.example.opentable;



import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class AddNewActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

    private static final int REQUEST_CODE_CHOOSE = 23;
    ImageView imgThumb;
    private UriAdapter mAdapter;
    BottomNavigationView bottomNavigationView;
    GoogleMap googleMap;
    FusedLocationProviderClient client;
    SupportMapFragment supportMapFragment;
    Button upload;
    double lat, longi;
    FirebaseFirestore db;
    HashMap<String, String> postMap;
    FirebaseStorage storage;
    StorageReference storageReference;
    List<Uri> globalImageUris;



    EditText restName, description, tags, location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);

        findViewById(R.id.imgBtn).setOnClickListener(this);
//        findViewById(R.id.videoBtn).setOnClickListener(this);

        restName = findViewById(R.id.Resname);
        description = findViewById(R.id.desc);
        tags = findViewById(R.id.tags);
        location = findViewById(R.id.location);


//        imgThumb = findViewById(R.id.videoThumb);
        globalImageUris = new ArrayList<>();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,
                false));
        recyclerView.setAdapter(mAdapter = new UriAdapter());

        bottomNavigationView=findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                int item_id = item.getItemId();
                switch (item_id){
                    case R.id.home :
                        intent=new Intent(AddNewActivity.this,HomeActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.acc :
                        intent=new Intent(AddNewActivity.this,ProfileActivity.class);
                        startActivity(intent);
                        break;


                }
                return true;
            }
        });


//----------------------------------         google map    -----------------------------------------------
        supportMapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.googleMapAddNew);

        //  permission granding
        client= LocationServices.getFusedLocationProviderClient(this);

        if(ActivityCompat.checkSelfPermission(AddNewActivity.this,
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



//----------------------------------  FIREBASE CODE FOR ADDING NEW POST ---------------------------------------------------

        // Getting current user
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ; // getting details of current user
        upload = findViewById(R.id.upload); // upload button
        postMap = new HashMap<>();



        storage = FirebaseStorage.getInstance();



        // upon clicking update button
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(restName.getText().toString().equals(" ") ||
                        restName.getText().toString().equals("") ||
                        description.getText().toString().equals("") || description.getText().toString().equals(" ") ||
                        tags.getText().toString().equals("") || tags.getText().toString().equals(" ")
                        || location.getText().toString().equals(""))
                {
                    Toast.makeText(AddNewActivity.this, "Fields must not be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }


                ProgressDialog dialog = new ProgressDialog(AddNewActivity.this);
                dialog.setMessage("Uploading... please wait.");
                dialog.show();

                db = FirebaseFirestore.getInstance(); // gettting instence of firebase



                // now fetch data from that table
                db.collection("user_profile")
                        .document(currentFirebaseUser.getUid()).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        // if the fetching is successfull then
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult(); //get the results
                            String userName = "";
                           userName = document.getData().get("Name").toString();

//                            Uploading values to database

                            // creating new Restaurant object, which contains (represents) the details of a post
                            // unlike the method of updating each and every field one by one,
                            // this is a cleaner approach to reset all the values of a user at once
                            RestaurantDetails restaurantDetails = new RestaurantDetails(
                                    restName.getText().toString().trim(),
                                    description.getText().toString().trim(),
                                    tags.getText().toString(),
                                    lat,
                                    longi,
                                    new ArrayList<String>(),
                                    new ArrayList<String>(),
                                    location.getText().toString().trim(),
                                    userName
                            );

                            // add new post in the posts collection with required details
                            db.collection("posts")
                                    .add(restaurantDetails) // add a record with randomly generated post id value
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {

                                            // now get newly inserted random id and add it to the user_profile collection's post document
                                            String newPostId = documentReference.getId();
//                        ArrayList<String> arrayList = new ArrayList<>();
//                        arrayList.add(newPostId);

                                            db.collection("user_profile").document(currentFirebaseUser.getUid())
                                                    .update("posts", FieldValue.arrayUnion(newPostId) )
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            storageReference = storage.getReference().child("OpenTable/posts/"+newPostId+"/images/");

                                                            // for each uri in the globalImageUris arraylist, upload it into firebase
                                                            int i = 1;
                                                            for (Uri uri : globalImageUris) {
                                                                StorageReference storageReference2 = storageReference.child("/"+(i++));
                                                                storageReference2.putFile(uri)
                                                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                            @Override
                                                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                                                // if task is successfull, then move from current activity
                                                                                startActivity(new Intent(AddNewActivity.this, HomeActivity.class));
                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                Toast.makeText(AddNewActivity.this, "Error while uploading images", Toast.LENGTH_SHORT).show();

                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {

                                                        }
                                                    });

                                            ModalForRestaurantTable modalForRestaurantTable = new ModalForRestaurantTable(
                                                    new GeoPoint(lat, longi),
                                                    location.getText().toString().trim(),
                                                    restName.getText().toString().trim(),
                                                    new ArrayList<String>( Arrays.asList(newPostId))
                                            );

                                            db.collection("restaurant").get()
                                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                                            boolean flag = true;
                                                            String restId = "";

                                                            for (DocumentSnapshot docsnap :
                                                                    list) {

                                                                if(docsnap.getData().get("name").equals(restName.getText().toString()))
                                                                {
                                                                    flag = false;
                                                                    restId = docsnap.getId();
                                                                }
                                                            }



                                                            if(flag)
                                                            {
                                                                // if restaurant dosen't exists, then create a new one
                                                                db.collection("restaurant")
                                                                        .add(modalForRestaurantTable)
                                                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                            @Override
                                                                            public void onSuccess(DocumentReference documentReference) {

                                                                            }
                                                                        }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(AddNewActivity.this,
                                                                                "Failed to add restaurant details", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                            }
                                                            else //else update the post's array only
                                                            {
                                                                // if restaurant dosen't exists, then create a new one
                                                                db.collection("restaurant").document(restId)
                                                                        .update("posts", FieldValue.arrayUnion(newPostId))
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {

                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                Toast.makeText(AddNewActivity.this,
                                                                                        "Failed to add restaurant details to existing one", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });
                                                            }
                                                            if(dialog.isShowing()) dialog.dismiss();
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(AddNewActivity.this, "Failed to insert data", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        } else {
                            Toast.makeText(AddNewActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }


                    }
                });


//




            }
        });



    }

    // <editor-fold defaultstate="collapsed" desc="onClick">
    @SuppressLint("CheckResult")
    @Override
    public void onClick(final View v) {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        startAction(v);
                    } else {
                        Toast.makeText(AddNewActivity.this, "Permission denied", Toast.LENGTH_LONG)
                                .show();
                    }
                }, Throwable::printStackTrace);
    }
    // </editor-fold>

    private void startAction(View v) {
        switch (v.getId()) {
            case R.id.imgBtn:
                Matisse.from(AddNewActivity.this)
                        .choose(MimeType.ofImage(), false)
                        .countable(true)
                        .capture(true)
                        .captureStrategy(
                                new CaptureStrategy(true, "com.zhihu.matisse.sample.fileprovider", "test"))
                        .maxSelectable(5)
                        .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                        .gridExpectedSize(
                                getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                        .thumbnailScale(0.85f)
                        .imageEngine(new GlideEngine())
                        .showSingleMediaType(true)
                        .originalEnable(true)
                        .maxOriginalSize(10)
                        .autoHideToolbarOnSingleTap(true)
                        .forResult(REQUEST_CODE_CHOOSE);
                break;
//            case R.id.videoBtn:
//
//                Matisse.from(AddNewActivity.this)
//                        .choose(MimeType.ofVideo(), false)
//                        .countable(true)
//                        .capture(true)
//                        .captureStrategy(
//                                new CaptureStrategy(true, "com.zhihu.matisse.sample.fileprovider", "test"))
//                        .maxSelectable(1)
//                        .showSingleMediaType(true)
//                        .originalEnable(true)
//                        .maxOriginalSize(10)
//                        .autoHideToolbarOnSingleTap(true)
//                        .forResult(12);
//                break;
            default:
                break;
        }
        mAdapter.setData(null, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {

            mAdapter.setData(Matisse.obtainResult(data), Matisse.obtainPathResult(data));
            globalImageUris = Matisse.obtainResult(data);
        }else if (requestCode == 12 && resultCode == RESULT_OK) {


            List<String> mSelected = Matisse.obtainPathResult(data);
//            Toast.makeText(this, mSelected.get(0), Toast.LENGTH_SHORT).show();

//            Glide.with(this)
//                    .load(Matisse.obtainResult(data).get(0))
//                    .into(imgThumb);
//            imgThumb.setVisibility(View.VISIBLE);
        }
    }


    // adapter for image
    private  static class UriAdapter extends RecyclerView.Adapter<UriAdapter.UriViewHolder> {
        List<Uri> mUris ;
        private List<String> mPaths;

        void setData(List<Uri> uris, List<String> paths) {
            mUris = uris;
            mPaths = paths;
            notifyDataSetChanged();
        }

        @Override
        public UriViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new UriViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.uri_item, parent, false));
        }

        @Override
        public void onBindViewHolder(UriViewHolder holder, int position) {
            holder.imageView.setImageURI(mUris.get(position));
        }

        @Override
        public int getItemCount() {
            return mUris == null ? 0 : mUris.size();
        }

        static class UriViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            UriViewHolder(View contentView) {
                super(contentView);
                imageView = contentView.findViewById(R.id.image);
            }
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
                            lat = location.getLatitude();
                            longi = location.getLongitude();

                            MarkerOptions options= new MarkerOptions().position(latLng).title("You are here");
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
                            googleMap.addMarker(options);
                        }
                    });
                }
            }
        });

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        this.googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(latLng.latitude+", "+latLng.longitude);
                lat = latLng.latitude;
                longi = latLng.longitude;

                googleMap.clear();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                googleMap.addMarker(markerOptions);
            }
        });
    }
    @Override
    protected void onStart() {
        bottomNavigationView.getMenu().findItem(R.id.ad_rev).setChecked(true);
        super.onStart();
    }

}

