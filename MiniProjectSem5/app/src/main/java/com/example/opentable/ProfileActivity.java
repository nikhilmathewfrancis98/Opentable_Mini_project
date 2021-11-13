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

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    Intent intent;
    ImageView mProfilePhoto,edit,logOut;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    StorageReference storageReference;
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
        mProfilePhoto = findViewById(R.id.profilePhoto);
        firebaseAuth = FirebaseAuth.getInstance(); // getting firebase instance


// ---------------------------------------- firebase data fetching -----------------------------------------------------------

        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ; // getting details of current user


         db = FirebaseFirestore.getInstance(); // getting instance of firebase

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

// ------------------------------------------------------------ firebase datafetch ends here -------------------------------------


//--------------------------------------------------------------------------------------------------------------------------------
        //                  FIREBASE - LOAD PROFILE IMAGE
//--------------------------------------------------------------------------------------------------------------------------------

        // Reference to Firebase Storage
        storageReference =
                FirebaseStorage.getInstance().getReference();
        // getting reference to appropriate file location in firebase storage
        StorageReference st = storageReference.child("OpenTable/Images/ProfilePicture/profile");

//        storageReference.child("OpenTable/Images/ProfilePicture/profile").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                Toast.makeText(ProfileActivity.this, "Got it", Toast.LENGTH_SHORT).show();
//                // Got the download URL for 'users/me/profile.png'
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // File not found
//                Toast.makeText(ProfileActivity.this, exception.toString(), Toast.LENGTH_LONG).show();
//            }
//        });

        // getting url of the required image and upon success, display the image in the image view using the received url
        st.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String imageURL = uri.toString();
                Glide.with(getApplicationContext()).load(imageURL).into(mProfilePhoto); // display image using url
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(ProfileActivity.this, "Failed to load profile image", Toast.LENGTH_SHORT).show();
            }
        });

//----------------------------------------------------------------------------------------------
        //                  FIREBASE - LOAD PROFILE IMAGE ENDS HERE
//----------------------------------------------------------------------------------------------



        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, EditProfile.class));
                finish();
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
                finish();
            }
        });


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
