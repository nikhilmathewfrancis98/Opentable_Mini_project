package com.example.opentable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {


    /********************************************************************************************
        NOTE THAT when user sings up for the first time, we need to create a folder in firebase storage to store
        the user profile image.
     ********************************************************************************************/



    FirebaseAuth firebaseAuth;
//    FirebaseUser user;
    String user_name, password,name, user_email, confirm_password;
    EditText userMail, passWord, userName, confirmPassword;
    Button signUP;
    FirebaseFirestore firestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        setTitle("OpenTable");
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        userName = findViewById(R.id.editname);
        userMail = findViewById(R.id.usrname);
        passWord = findViewById(R.id.password);
        signUP = findViewById(R.id.signUp);
        confirmPassword = findViewById(R.id.confpswd);

        signUP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                user_email = userName.getText().toString(); // must be a valid email
                password = passWord.getText().toString(); // must be >6 chars
                user_name = userMail.getText().toString();
                confirm_password = confirmPassword.getText().toString();
                if(user_email.trim().equals("")||
                        password.trim().equals("")||
                        user_name.trim().equals("") ||
                        confirm_password.trim().equals("")
                ){
                    Toast.makeText(SignupActivity.this, "Fields must not be empty", Toast.LENGTH_SHORT).show();
                    return;

                }

                if(!password.equals(confirm_password))
                {
                    Toast.makeText(SignupActivity.this, "Password dosen't match", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(password.length()<6)
                {
                    Toast.makeText(SignupActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                    return;

                }

                ProgressDialog dialog = new ProgressDialog(SignupActivity.this);
                dialog.setMessage("Please wait...");
                dialog.show();

                firebaseAuth.createUserWithEmailAndPassword(user_email, password)
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String userID = firebaseAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = firestore.collection("user_profile").document(userID);

                            // making a key:value pair of required data
                            Map<String, Object> user = new HashMap<>();
                            user.put("Name", user_name);
                            user.put("Username", user_email);
                            user.put("Password", password);
                            user.put("Bio", "");

                            // now send the details to firebase
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {

                                @Override
                                public void onSuccess(Void aVoid) {

                                    // creating folder in the firebase storage with newly created user id for storing profile image
                                    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                                    StorageReference storageReference = firebaseStorage.getReference();

                                    // the default image to be stored at the time of user signup
                                    Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.drawable.profile);

                                    // now put the default image into the firebase storage
                                    storageReference
                                            .child("OpenTable/Images/ProfilePicture/"+userID+"/profile")
                                            .putFile(uri)
                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {                                                    
                                                    Toast.makeText(SignupActivity.this, "User profile created successfully", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
                                                    startActivity(intent);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(SignupActivity.this, "Error in signup !!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                    if(dialog.isShowing()) dialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    if(dialog.isShowing()) dialog.dismiss();
                                    Toast.makeText(SignupActivity.this, "Failed to create user profile", Toast.LENGTH_SHORT).show();
                                }
                            });


                        } else
                        {
                            if(dialog.isShowing()) dialog.dismiss();
                            Toast.makeText(SignupActivity.this, "Error in sign up, check the values", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });

    }

    @Override
    public void onStart () {
        super.onStart();
        // Check if user is signed in (non-null). If in, then goto home page
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            currentUser.reload();
        }
    }
}
