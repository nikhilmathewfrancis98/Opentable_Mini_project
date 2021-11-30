package com.example.opentable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CommentActivity extends AppCompatActivity {
    List<ModelComment> commentList;

    FirebaseFirestore db;
    String postId;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        commentList = new ArrayList<>();
        postId = getIntent().getStringExtra("postId"); // get the post id of the comment

        recyclerView = findViewById(R.id.commentsRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    protected void onStart() {


//        Check if user is signed in (non-null). If in, then goto home page
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null){
            Intent i = new Intent(CommentActivity.this, LoginActivity.class);
            startActivity(i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }


//-------------------------------- FIREBASE CODE FOR DISPLAYING POSTS ---------------------------------------


        commentList.clear();
        // it is important to note that by just getting the records from firebase and adding it to an arraylist
        // wont result in desired output
        // this is because, the onComplete listener is an aynchronus method
        // So arraylist will contain nothing after this method. Or in other words, the rest of the code will gets
        // executed before the onComplete method gets executed, resulting in an empty arraylist
        // the solution is to use callback method
        // useful link: https://stackoverflow.com/questions/57330766/why-does-my-function-that-calls-an-api-return-an-empty-or-null-value
        handleRecords(new HomeActivity.Callback() {
            @Override
            public void myResponseCallback(QueryDocumentSnapshot document) {

                Map<String, Object> mp = document.getData();
                ArrayList<String> likedUsers = (ArrayList<String>) mp.get("likedUsers");

                String uId = FirebaseAuth.getInstance().getCurrentUser().getUid().trim();
                boolean containsUser = likedUsers.contains(uId);

                // creating new ModelComment object by fetching details of individual comments returned from firebase
                commentList.add(new ModelComment(
                            mp.get("content").toString().trim(),
                            document.getId().trim(),
                            mp.get("userName").toString().trim(),
                            Integer.parseInt(mp.get("likesCount").toString()),
                            containsUser, // current user liked this comment or not
                            (Timestamp)mp.get("time"),
                            postId,
                            Integer.parseInt(mp.get("reportCount").toString())
                        ));

            }
        });

        super.onStart();
    }

    // ---------------------- CALLBACK INTERFACE ------------------------------------
    interface Callback {
        void myResponseCallback(QueryDocumentSnapshot document);//whatever your return type is: string, integer, etc.
    }


    // function to handle returned rows
    public void handleRecords(final HomeActivity.Callback callback)
    {
        db = FirebaseFirestore.getInstance();
        if(postId != null)
        db.collection("posts").document(postId).collection("comments")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult()))
                            {
                                callback.myResponseCallback(document);

                            }

                            recyclerView.setAdapter(new AdapterComments(
                                    commentList,
                                    CommentActivity.this,
                                    FirebaseAuth.getInstance().getCurrentUser().getUid()));
                        }
                        else
                        {
                            Log.d("Details", "Error getting documents: ", task.getException());
                        }
                    }
                });
        else Toast.makeText(CommentActivity.this, "it'snlll", Toast.LENGTH_SHORT).show();
    }


}