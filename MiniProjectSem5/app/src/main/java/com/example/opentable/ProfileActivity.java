package com.example.opentable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    Intent intent;
    ImageView mProfilePhoto,edit,logOut;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    StorageReference storageReference;
    TextView name, bio;
    private ProgressDialog dialog;
    RecyclerView recyclerView;
    UserPostAdapter homePostAdapter;
    List<ModalPost> postList;
    TextView noPost;

    // Reference to Firebase Storage
    StorageReference st;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        edit=findViewById(R.id.editProfile);
        logOut=findViewById(R.id.logoutOption);
        name = findViewById(R.id.name);
        bio = findViewById(R.id.textbio);
        recyclerView = findViewById(R.id.postsRecycler);
        noPost = findViewById(R.id.noPosts);

//------------------------------------ RECYCLER VIEW ADAPTER ----------------------------------------------------------------

            postList = new ArrayList<>();

        // setting up the recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                false));
        homePostAdapter = new UserPostAdapter(postList, this);


        mProfilePhoto = findViewById(R.id.profilePhoto);
        firebaseAuth = FirebaseAuth.getInstance(); // getting firebase instance


// ---------------------------------------- firebase data fetching -----------------------------------------------------------

        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ; // getting details of current user


         db = FirebaseFirestore.getInstance(); // getting instance of firebase

        // accessing a table with id = current user id
        DocumentReference docRef = db.collection("user_profile").document(currentFirebaseUser.getUid());


        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading... please wait.");
//        dialog.show();


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
        storageReference =  FirebaseStorage.getInstance().getReference();
        // getting reference to appropriate file location in firebase storage
        StorageReference st = storageReference.child("OpenTable/Images/ProfilePicture/"
                +currentFirebaseUser.getUid().trim()+"/profile");


        // getting url of the required image and upon success, display the image in the image view using the received url
        st.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String imageURL = uri.toString();
                Glide.with(getApplicationContext()).load(imageURL).into(mProfilePhoto); // display image using url
//                if(dialog.isShowing()) dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(ProfileActivity.this, exception.toString(), Toast.LENGTH_SHORT).show();
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



//-------------------------------- FIREBASE CODE FOR DISPLAYING POSTS ---------------------------------------

        storageReference = FirebaseStorage.getInstance().getReference();
        postList.clear();
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading... please wait.");
//        dialog.show();

        // it is important to note that by just getting the records from firebase and adding it to an arraylist
        // wont result in desired output
        // this is because, the onComplete listener is an aynchronus method
        // So arraylist will contain nothing after this method. Or in other words, the rest of the code will gets
        // executed before the onComplete method gets executed, resulting in an empty arraylist
        // the solution is to use callback method
        // useful link: https://stackoverflow.com/questions/57330766/why-does-my-function-that-calls-an-api-return-an-empty-or-null-value
        handleRecords(new ProfileActivity.Callback() {
            @Override
            public void myResponseCallback(DocumentSnapshot document) {



                Map<String, Object> map = document.getData();
                ArrayList<String> userPosts = (ArrayList<String>) map.get("posts");

                if(userPosts == null || userPosts.isEmpty())
                {
                    noPost.setVisibility(View.VISIBLE);
//                    Toast.makeText(ProfileActivity.this, "Emtpy list", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    noPost.setVisibility(View.GONE);

                }

                for (String postId : userPosts) {

                    db.collection("posts").document(postId.trim()).get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {

                                    Map<String, Object> mp = documentSnapshot.getData();
//                                    Toast.makeText(ProfileActivity.this, mp.toString(), Toast.LENGTH_SHORT).show();
                                    ArrayList<String> likedUsers = (ArrayList<String>) mp.get("likedUsers");

                                    String uId = FirebaseAuth.getInstance().getCurrentUser().getUid().trim();


                                    boolean containsUser = false;

                                    if(!(likedUsers == null))
                                        containsUser = likedUsers.contains(uId);

                                    st = storageReference.child("OpenTable/posts/"+documentSnapshot.getId()+"/images/1");
                                    postList.add(new ModalPost(
                                            mp.get("restaurant_name").toString(),
                                            mp.get("location").toString(),
                                            st,
                                            false,
                                            Integer.parseInt(mp.get("likesCount").toString()),
                                            mp.get("description").toString(),
                                            mp.get("tags").toString(),
                                            FirebaseAuth.getInstance().getCurrentUser().getUid(), // current user id
                                            documentSnapshot.getId(),// current post id
                                            Integer.parseInt(mp.get("reportCount").toString()), // report count for this post
                                            containsUser,
                                            mp.get("userName").toString()
                                    ));
                                    recyclerView.setAdapter(homePostAdapter);
                                }
                            });
                }
            }
        });

        super.onStart();
    }



    // ---------------------- CALLBACK INTERFACE ------------------------------------
    interface Callback {
        void myResponseCallback(DocumentSnapshot document);//whatever your return type is: string, integer, etc.
    }


    // function to handle returned rows
    public void handleRecords(final ProfileActivity.Callback callback)
    {
        db = FirebaseFirestore.getInstance();

        db.collection("user_profile").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        callback.myResponseCallback(documentSnapshot);

                    }
                });
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
