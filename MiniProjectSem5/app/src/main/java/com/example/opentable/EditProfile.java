package com.example.opentable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class EditProfile extends AppCompatActivity {


    Button update;
    ImageView profileImg;
    FirebaseFirestore db;
    FirebaseUser currentFirebaseUser;
    FirebaseAuth firebaseAuth;
    DocumentReference docRef;
    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;
    Uri filePathURI;
    BottomNavigationView bottomNavigationView;
    Intent intent;
    private ProgressDialog dialog;


    EditText edName, userName, password, confirmPassword, bio;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
// --------------------------------- BOTTOM NAVIGATION VIEW --------------------------------------------------

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int item_id = item.getItemId();
                switch (item_id) {
                    case R.id.ad_rev:
                        intent = new Intent(EditProfile.this, AddNewActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.home:
                        intent = new Intent(EditProfile.this, HomeActivity.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });

        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading... please wait.");
        dialog.show();


        firebaseAuth = FirebaseAuth.getInstance(); // getting firebase instance

        // getting required references
        edName = findViewById(R.id.edname);
        userName = findViewById(R.id.username);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirm_password);
        profileImg = findViewById(R.id.profilePhoto);
        bio = findViewById(R.id.bio);
        update=findViewById(R.id.upload);

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ; // gettting details of current user
        userId = currentFirebaseUser.getUid(); // getting user id

        // upon clicking profile image
        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

// ---------------------------------------------------------------------------------------------------------------------------
        //                      FIREBASE : UPDATION OF DATA
// ---------------------------------------------------------------------------------------------------------------------------

        // upon clicking update button
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(
                     edName.getText().toString().trim().equals("") ||
                     userName.getText().toString().trim().equals("") ||
                     password.getText().toString().trim().equals("") ||
                     confirmPassword.getText().toString().trim().equals("") ||
                     bio.getText().toString().trim().equals("")

                )
                {
                    Toast.makeText(EditProfile.this, "Fields must not be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(password.getText().toString().length()<6)
                {
                    Toast.makeText(EditProfile.this, "Password must have atleast 6 characters", Toast.LENGTH_SHORT).show();
                    return;

                }

                if(!password.getText().toString().trim().equals(confirmPassword.getText().toString().trim()))
                {
                    Toast.makeText(EditProfile.this, "Password dosen't match", Toast.LENGTH_SHORT).show();
                    return;
                }


//            Uploading values to database

                // creating new User object, which contains (represents) the details of a user
                // unlike the method of updating each and every field one by one,
                // this is a cleaner approach to reset all the values of a user at once
                User user = new User(
                        edName.getText().toString(),
                        userName.getText().toString(),
                        password.getText().toString(),
                        bio.getText().toString()
                );

                // resetting entire field values of current user
                docRef = db.collection("user_profile").document(currentFirebaseUser.getUid());
                docRef.set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() { // on successful resetting of values
                    @Override
                    public void onSuccess(Void aVoid) {
//                        Toast.makeText(EditProfile.this, "Updated successfully !!", Toast.LENGTH_SHORT).show();
                        // now move back
                        updateProfileImage(); // updating profile image

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfile.this, "Some error happened!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

// ----------------------------------------------------------------------------------------------------------------------------------------
//                      FIREBASE : UPDATION OF DATA ENDS HERE
// ----------------------------------------------------------------------------------------------------------------------------------------

// --------------------- Fetching data from firebase to display them in the fields when the page loads -----------------------------------

        db = FirebaseFirestore.getInstance(); // gettting instence of firebase

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
                        edName.setText(document.get("Name").toString());
                        userName.setText(document.get("Username").toString());
                        password.setText(document.get("Password").toString());
                        bio.setText(document.get("Bio").toString());

                    } else {
                        Toast.makeText(EditProfile.this, "No data!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EditProfile.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
                if(dialog.isShowing()) dialog.dismiss();
            }
        });


        // fetching profile image from firebase


        // Reference to Firebase Storage
        storageReference =
                FirebaseStorage.getInstance().getReference();
        // getting reference to appropriate file location in firebase storage
        StorageReference st = storageReference.child("OpenTable/Images/ProfilePicture/"
                +currentFirebaseUser.getUid().trim()+"/profile");

        // getting url of the required image and upon success, display the image in the image view using the received url
        st.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String imageURL = uri.toString();
                Glide.with(getApplicationContext()).load(imageURL).into(profileImg); // display image using url
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(EditProfile.this, "Failed to load profile image", Toast.LENGTH_SHORT).show();
            }
        });

// --------------------- firebase data fetching ends her -----------------------------------
        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

    }


    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,40);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 40 && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            Uri imageUri = data.getData();
            profileImg.setImageURI(imageUri);

            filePathURI = data.getData();

        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    void updateProfileImage()
    {
        if (filePathURI != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Updating...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref
                    = storageReference
                    .child("OpenTable/Images/ProfilePicture/"+currentFirebaseUser.getUid().trim()+"/profile");

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePathURI)
            .addOnSuccessListener(
                    new OnSuccessListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onSuccess(
                                UploadTask.TaskSnapshot taskSnapshot)
                        {

                            // Image uploaded successfully
                            // Dismiss dialog
                            progressDialog.dismiss();


                            Toast
                                    .makeText(EditProfile.this,
                                            "Image Uploaded!!",
                                            Toast.LENGTH_SHORT)
                                    .show();
//                                    startActivity(new Intent(EditProfile.this, ProfileActivity.class));
//                                    finish();
                        }
                    })

            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e)
                {

                    // Error, Image not uploaded
                    progressDialog.dismiss();
                    Toast.makeText(EditProfile.this,
                                    "Failed " + e.getMessage(),
                                    Toast.LENGTH_SHORT)
                            .show();
                }
            })
            .addOnProgressListener(
                    new OnProgressListener<UploadTask.TaskSnapshot>() {

                        // Progress Listener for loading
                        // percentage on the dialog box
                        @Override
                        public void onProgress(
                                UploadTask.TaskSnapshot taskSnapshot)
                        {
                            double progress
                                    = (100.0
                                    * taskSnapshot.getBytesTransferred()
                                    / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage(
                                    "Uploaded "
                                            + (int)progress + "%");
                        }
                    });
        }
    }

    @Override
    protected void onStart() {
        bottomNavigationView.getMenu().findItem(R.id.acc).setChecked(true);

//        Check if user is signed in (non-null). If in, then goto home page
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Intent i = new Intent(EditProfile.this, LoginActivity.class);
            startActivity(i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }
        super.onStart();
    }
}