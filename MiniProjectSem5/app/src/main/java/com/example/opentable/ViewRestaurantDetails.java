package com.example.opentable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.encoders.proto.Protobuf;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.grpc.Context;

public class ViewRestaurantDetails extends AppCompatActivity {


        BottomNavigationView bottomNavigationView;
        Intent intent;
        List<ModalPost> postList;
        TextView restNameText;
        HomePostAdapter homePostAdapter;
        FirebaseFirestore db;
        RecyclerView recyclerView;
        TextView relatedPosts, restLocation;

        // Reference to Firebase Storage
        StorageReference storageReference;
        StorageReference st;
        private SliderAdapter adapter;
        private ArrayList<SliderData> sliderDataArrayList;
        private SliderView sliderView;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_view_restaurant_details);
            restNameText = findViewById(R.id.restName);
            relatedPosts = findViewById(R.id.relatedPosts);
            restLocation = findViewById(R.id.restLocation);

            // initializing the list for posts
            postList = new ArrayList<>();

// --------------------------------- BOTTOM NAVIGATION VIEW --------------------------------------------------

            bottomNavigationView = findViewById(R.id.bottom_nav);
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int item_id = item.getItemId();
                    switch (item_id) {
                        case R.id.home:
                            intent = new Intent(ViewRestaurantDetails.this, HomeActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.ad_rev:
                            intent = new Intent(ViewRestaurantDetails.this, AddNewActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.acc:
                            intent = new Intent(ViewRestaurantDetails.this, ProfileActivity.class);
                            startActivity(intent);
                            break;
                    }
                    return true;
                }
            });

//------------------------------------ RECYCLER VIEW ADAPTER ---------------------------------------------


            // setting up the recycler view
            recyclerView = (RecyclerView) findViewById(R.id.postsRecycler);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                    false));
            homePostAdapter = new HomePostAdapter(postList, this);

// ------------------------------------ SEARCH BUTTON ---------------------------------------------------

            // creating a new array list fr our array list.
            sliderDataArrayList = new ArrayList<>();

            // initializing our slider view and
            // firebase firestore instance.
            sliderView = findViewById(R.id.slider);


            // calling our method to load images.
            loadImages();


//---------------------------------- ONCREATE ENDS HERE ----------------------------------------------
        }

        @Override
        protected void onStart() {
            bottomNavigationView.getMenu().findItem(R.id.home).setChecked(true);

//        Check if user is signed in (non-null). If in, then goto home page
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if(currentUser == null){
                Intent i = new Intent(ViewRestaurantDetails.this, LoginActivity.class);
                startActivity(i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }


//-------------------------------- FIREBASE CODE FOR DISPLAYING POSTS ---------------------------------------

            storageReference = FirebaseStorage.getInstance().getReference();
            postList.clear();
            // it is important to note that by just getting the records from firebase and adding it to an arraylist
            // wont result in desired output
            // this is because, the onComplete listener is an aynchronus method
            // So arraylist will contain nothing after this method. Or in other words, the rest of the code will gets
            // executed before the onComplete method gets executed, resulting in an empty arraylist
            // the solution is to use callback method
            // useful link: https://stackoverflow.com/questions/57330766/why-does-my-function-that-calls-an-api-return-an-empty-or-null-value
            handleRecords(new ViewRestaurantDetails.Callback() {
                @Override
                public void myResponseCallback(QueryDocumentSnapshot document) {

                    Map<String, Object> mp = document.getData();



                    ArrayList<String> likedUsers = (ArrayList<String>) mp.get("likedUsers");
                    String uId = FirebaseAuth.getInstance().getCurrentUser().getUid().trim();
                    boolean containsUser = likedUsers.contains(uId);

                    st = storageReference.child("OpenTable/posts/"+document.getId()+"/images/1");
                    postList.add(new ModalPost(
                            mp.get("restaurant_name").toString(),
                            mp.get("location").toString(),
                            st,
                            false,
                            Integer.parseInt(mp.get("likesCount").toString()),
                            mp.get("description").toString(),
                            mp.get("tags").toString(),
                            FirebaseAuth.getInstance().getCurrentUser().getUid(), // current user id
                            document.getId(),// current post id
                            Integer.parseInt(mp.get("reportCount").toString()), // report count for this post
                            containsUser,
                            mp.get("userName").toString()
                    ));
                }
            });
            super.onStart();
        }

        // ---------------------- CALLBACK INTERFACE ------------------------------------
        interface Callback {
            void myResponseCallback(QueryDocumentSnapshot document);//whatever your return type is: string, integer, etc.
        }


        // function to handle returned rows
        public void handleRecords( ViewRestaurantDetails.Callback callback)
        {
            db = FirebaseFirestore.getInstance();

            String docId = getIntent().getStringExtra("docId");

            db.collection("restaurant").document(docId.trim()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> map = documentSnapshot.getData();
                        String restName = map.get("name").toString();
                        restNameText.setText(restName);
                        restLocation.setText(map.get("location").toString());

//                        Toast.makeText(ViewRestaurantDetails.this, restName, Toast.LENGTH_SHORT).show();

                        db.collection("posts")
//                                .whereEqualTo("restaurant_name", restName.trim())
                                .orderBy("likesCount", Query.Direction.DESCENDING)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful())
                                        {
                                            boolean flag = true;
                                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult()))
                                            {
                                                if(document.getData().get("restaurant_name").toString().equals(restName.trim()))
                                                {
                                                    callback.myResponseCallback(document);
                                                    flag = false;
                                                }
                                            }
                                            if(flag)
                                            {
                                                relatedPosts.setText("No Related Posts Yet");
                                                relatedPosts.setGravity(Gravity.CENTER );
                                            }


                                            recyclerView.setAdapter(homePostAdapter);
                                        }
                                        else
                                        {
                                            Log.d("Details", "Error getting documents: ", task.getException());
                                        }
                                    }
                                });
                    }
                });


        }



    private void loadImages() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String docId = getIntent().getStringExtra("docId");

        // getting data from our collection and after
        // that calling a method for on success listener.
        SliderData model = new SliderData();

        db.collection("restaurant").document(docId.trim()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> map = documentSnapshot.getData();

                        ArrayList<String> posts = (ArrayList<String>) map.get("posts");




                        for (String postId :
                                posts) {



                            // getting reference to appropriate file location in firebase storage
                            StorageReference storage = storageReference.child("OpenTable/posts/"+postId.trim()+"/images/");

                            storage.listAll()
                                    .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                        @Override
                                        public void onSuccess(ListResult listResult) {

                                            for (StorageReference item : listResult.getItems()) {

                                                item.getDownloadUrl()
                                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri uri) {
                                                                model.setImgUrl(uri.toString());

                                                                sliderDataArrayList.add(model);

                                                                // after adding data to our array list we are passing
                                                                // that array list inside our adapter class.
                                                                adapter = new SliderAdapter(ViewRestaurantDetails.this, sliderDataArrayList);

                                                                // belows line is for setting adapter
                                                                // to our slider view
                                                                sliderView.setSliderAdapter(adapter);

                                                                // below line is for setting animation to our slider.
                                                                sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);

                                                                // below line is for setting auto cycle duration.
                                                                sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);

                                                                // below line is for setting
                                                                // scroll time animation
                                                                sliderView.setScrollTimeInSec(2);

                                                                // below line is for setting auto
                                                                // cycle animation to our slider
                                                                sliderView.setAutoCycle(true);

                                                                // below line is use to start
                                                                // the animation of our slider view.
                                                                sliderView.startAutoCycle();


                                                            }
                                                        });
                                                // All the items under listRef.
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ViewRestaurantDetails.this, "Uh-oh, an error occurred!", Toast.LENGTH_SHORT).show();

                                        }
                                    });


                        }

                    }
                });

        }


    }
