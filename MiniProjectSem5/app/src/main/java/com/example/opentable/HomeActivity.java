package com.example.opentable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
    }

    @Override
    protected void onStart() {
       bottomNavigationView.getMenu().findItem(R.id.home).setChecked(true);
        super.onStart();
    }
}