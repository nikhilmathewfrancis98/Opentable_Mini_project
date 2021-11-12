package com.example.opentable;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                // Check if user is signed in (non-null). If in, then goto home page
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if(currentUser != null){
                    Intent i = new Intent(SplashScreen.this, HomeActivity.class);
                    startActivity(i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                }
                else {
                    Intent i = new Intent(SplashScreen.this, LoginActivity.class);
                    startActivity(i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                }



                //This method will be executed once the timer is over
                // Start your app main activity
//                Intent i = new Intent(SplashScreen.this, LoginActivity.class);
//                startActivity(i);
                // close this activity
                finish();
            }
        }, 1000);
    }

//    @Override
//    public void onStart() {
//        // Check if user is signed in (non-null). If in, then goto home page
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        if(currentUser != null){
//            Intent i = new Intent(SplashScreen.this, HomeActivity.class);
//            startActivity(i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
//        }
//        else {
//            Intent i = new Intent(SplashScreen.this, LoginActivity.class);
//            startActivity(i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
//        }
//        super.onStart();
//    }


}
