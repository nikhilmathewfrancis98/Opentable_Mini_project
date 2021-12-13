package com.example.opentable;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {

    ImageView logo;
    TextView appName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        logo = findViewById(R.id.logo);
        appName = findViewById(R.id.appname);

        Animation fadeDown = AnimationUtils.loadAnimation(SplashScreen.this, R.anim.zoom_in_author);
        Animation fadeUp = AnimationUtils.loadAnimation(SplashScreen.this, R.anim.fade_up_splash);
        logo.startAnimation(fadeDown);
        appName.startAnimation(fadeUp);
//        textView3.setVisibility(View.INVISIBLE);

        fadeUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
//                Animation custom = AnimationUtils.loadAnimation(SplashScreen.this, R.anim.zoom_in_author);
//                textView3.setVisibility(View.VISIBLE);
//                textView3.startAnimation(custom);
            }
        });

        int TimeOut = 1800; // the timeout interval

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



                overridePendingTransition(R.anim.zoom_in, R.anim.static_animation);
                finish(); // the current activity wil get finished, will be popped out from activity stack
            }
        }, TimeOut);
    }
}
