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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    Intent intent;
    List<ModalPost> postList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("OpenTable - Home");
        setContentView(R.layout.activity_home);
        bottomNavigationView=findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int item_id = item.getItemId();
                switch (item_id){
                    case R.id.ad_rev :
                       intent=new Intent(HomeActivity.this,AddNewActivity.class);
                       startActivity(intent);
                       break;
                       case R.id.acc :
                       intent=new Intent(HomeActivity.this,ProfileActivity.class);
                       startActivity(intent);
                       break;


                }
                return true;
            }
        });



        // setting up the list with required data
//        postList = new ArrayList<>();
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
        recyclerView.setAdapter(new HomePostAdapter(postList, this));
    }

    @Override
    protected void onStart() {
       bottomNavigationView.getMenu().findItem(R.id.home).setChecked(true);
        super.onStart();
    }
}