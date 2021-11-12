package com.example.opentable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    Intent intent;
    ImageView mProfilePhoto,edit,logOut;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;

    TextView name, bio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Profile");
        setContentView(R.layout.activity_profile);
        edit=findViewById(R.id.editProfile);
        logOut=findViewById(R.id.logoutOption);
        name = findViewById(R.id.name);
        bio = findViewById(R.id.textbio);

        firebaseAuth = FirebaseAuth.getInstance(); // getting firebase instance


        // --------------------- firebase data fetching -----------------------------------

        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ; // gettting details of current user


         db= FirebaseFirestore.getInstance(); // gettting instence of firebase

        // accessing a table with id = current user id
        DocumentReference docRef = db.collection("user_profile").document(currentFirebaseUser.getUid());

        // now fetch data from that table
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                // if the fetching is successfull then
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult(); //get the results

                    // print the data
                    if (document.exists()) {
                        name.setText(document.get("Name").toString());
                        bio.setText(document.get("Username").toString());

                    } else {
                        Toast.makeText(ProfileActivity.this, "No data!", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(ProfileActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // --------------------- firebase -----------------------------------



        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, EditProfile.class));
            }
        });
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlert();
            }
        });

        //-------------------------- Bottom navigation ------------------------------------------
        bottomNavigationView=findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int item_id = item.getItemId();
                switch (item_id){
                    case R.id.ad_rev :
                        intent=new Intent(ProfileActivity.this,AddNewActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.home :
                        intent=new Intent(ProfileActivity.this,HomeActivity.class);
                        startActivity(intent);
                        break;


                }
                return true;
            }
        });
        //-------------------------- Bottom navigation ends here------------------------------------------




    }

    private void showAlert() {

        LayoutInflater li = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        View view = li.inflate(R.layout.logout, null);
        Button logOutButton = view.findViewById(R.id.logoutButton);

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            }
        });
//        LinearLayout contentLayout = view.findViewById(R.id.logoutOption);
//        contentLayout.setVisibility(View.GONE);


        ab.setView(view);
        AlertDialog alert = ab.create();

        alert.show();// show the dialog box

        // upon clicking create button
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
    }


    @Override
    protected void onStart() {
        bottomNavigationView.getMenu().findItem(R.id.acc).setChecked(true);
        super.onStart();
    }

//
//    private void openFileChooser() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(intent,40);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 40 && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            Uri imageUri = data.getData();
            mProfilePhoto.setImageURI(imageUri);

        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    }
