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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    Intent intent;
    List<ModalPost> postList;
    SearchView searchCategory;
    ImageView searchViewButton;
    ImageView locationIcon;
    TextView pageTitle;
    HomePostAdapter homePostAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setTitle("OpenTable - Home");
        setContentView(R.layout.activity_home);
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


        // setting up the list with required data
        postList = new ArrayList<>();
        postList.add(new ModalPost("title 01",
                "tvm", "/storage/3230-3664/01_My Docs/Camera/church.jpg", true,
                23,
                "Something just like this is the description",
                "#kanji, #chicken"));

        postList.add(new ModalPost("title 02",
                "kottayam", "/storage/3230-3664/01_My Docs/Camera/church.jpg", false,
                10,
                "Something just like this is the description",
                "#biriyani, #chicken"));

        postList.add(new ModalPost("title 04",
                "alappuzha", "/storage/3230-3664/01_My Docs/Camera/church.jpg",
                false, 14,
                "Something just like this is the description",
                "#appam, #chicken"));

        postList.add(new ModalPost("title 05",
                "kannur", "/storage/3230-3664/01_My Docs/Camera/church.jpg",
                true, 155,
                "Something just like this is the description",
                "#biriyani, #chicken"));

        postList.add(new ModalPost("title 06",
                "tvm", "/storage/3230-3664/01_My Docs/Camera/church.jpg", false,
                0,
                "Something just like this is the description",
                "#porotta, #chicken"));

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                false));
        homePostAdapter = new HomePostAdapter(postList, this);
        recyclerView.setAdapter(homePostAdapter);


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
                homePostAdapter.getFilter().filter(query);
                return false;
            }

            @Override // upon search text change
            public boolean onQueryTextChange(String newText) {
//                Log.d("hiui", "ji");
                // filter the list using new search keyword
                homePostAdapter.getFilter().filter(newText);
                return true;
            }
        });

        locationIcon = findViewById(R.id.locationIcon);
        locationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, NearByRestaurantsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        bottomNavigationView.getMenu().findItem(R.id.home).setChecked(true);

//        // Check if user is signed in (non-null). If in, then goto home page
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null){
            Intent i = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }
        super.onStart();
    }
    }
