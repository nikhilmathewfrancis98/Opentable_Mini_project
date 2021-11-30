package com.example.opentable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    Intent intent;
    List<ModalPost> postList;
    SearchView searchCategory;
    ImageView searchViewButton;
    ImageView locationIcon;
    TextView pageTitle;
    HomePostAdapter homePostAdapter;
    CollectionReference listOfPosts;
    FirebaseFirestore db;
    RecyclerView recyclerView;

    // Reference to Firebase Storage
    StorageReference storageReference;
    StorageReference st;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setTitle("OpenTable - Home");
        setContentView(R.layout.activity_home);


        // initializing the list for posts
        postList = new ArrayList<>();
// --------------------------------- BOTTOM NAVIGATION VIEW --------------------------------------------------

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int item_id = item.getItemId();
                switch (item_id) {
                    case R.id.ad_rev:
                        intent = new Intent(HomeActivity.this, AddNewActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.acc:
                        intent = new Intent(HomeActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });

//------------------------------------ RECYCLER VIEW ADAPTER ---------------------------------------------


        // setting up the recycler view
        recyclerView = (RecyclerView) findViewById(R.id.posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                false));
        homePostAdapter = new HomePostAdapter(postList, this);
//        recyclerView.setAdapter(homePostAdapter);

// ------------------------------------ SEARCH BUTTON ---------------------------------------------------

        searchViewButton = findViewById(R.id.searchIcon);
        searchCategory = findViewById(R.id.searchCategory); // (hidden searchView - search bar)
        pageTitle = findViewById(R.id.pageTitle); // page title (hides when search is clicked)

        // upon search query text change
        searchCategory.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override // upon search text change
            public boolean onQueryTextChange(String newText) {
                // filter the list using new search keyword
//                adapter.getFilter().filter(newText);
                return true;
            }
        });

        // display the search bar and hide other things with some transitions
        searchViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchCategory.startAnimation(AnimationUtils.loadAnimation(searchCategory.getContext(),
                        R.anim.slide_from_right));
                searchCategory.setIconified(false);
                searchCategory.setVisibility(View.VISIBLE);
                searchViewButton.setVisibility(View.GONE);

                pageTitle.startAnimation(AnimationUtils.loadAnimation(searchCategory.getContext(),
                        R.anim.slide_to_left));
                pageTitle.setVisibility(View.GONE);
            }
        });

        // upon closing the searchView, hide it and display other icons and page title
        searchCategory.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                pageTitle.startAnimation(AnimationUtils.loadAnimation(searchCategory.getContext(),
                        R.anim.slide_from_left));
                searchViewButton.startAnimation(AnimationUtils.loadAnimation(searchCategory.getContext(),
                        R.anim.slide_from_left));
                searchCategory.setVisibility(View.GONE);
                searchViewButton.setVisibility(View.VISIBLE);
                pageTitle.setVisibility(View.VISIBLE);
                return false;
            }
        });


        // upon search query text change
        searchCategory.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
//            Log.d("hi", "ji");
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter the list using new search keyword
//                homePostAdapter.getFilter().filter(query); ----------------------- Note this
                return false;
            }

            @Override // upon search text change
            public boolean onQueryTextChange(String newText) {
                // filter the list using new search keyword
//                homePostAdapter.getFilter().filter(newText); ------------------------- Note this
                return true;
            }
        });

// --------------------------------  LOCATION BUTTON AT THE TOP -----------------------------------
        locationIcon = findViewById(R.id.locationIcon);
        locationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, NearByRestaurantsActivity.class);
                startActivity(intent);
            }
        });

//---------------------------------- ONCREATE ENDS HERE ----------------------------------------------
    }

    @Override
    protected void onStart() {
        bottomNavigationView.getMenu().findItem(R.id.home).setChecked(true);

//        Check if user is signed in (non-null). If in, then goto home page
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null){
            Intent i = new Intent(HomeActivity.this, LoginActivity.class);
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
        handleRecords(new Callback() {
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
                        containsUser
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
    public void handleRecords(final Callback callback)
    {
        db = FirebaseFirestore.getInstance();
        db.collection("posts")
                .orderBy("likesCount", Query.Direction.DESCENDING)
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

//                            homePostAdapter = new HomePostAdapter(postList, HomeActivity.this);
                            recyclerView.setAdapter(homePostAdapter);
                        }
                        else
                        {
                            Log.d("Details", "Error getting documents: ", task.getException());
                        }
                    }
                });
        }

}
