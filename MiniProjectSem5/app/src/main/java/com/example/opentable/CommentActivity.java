package com.example.opentable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends AppCompatActivity {
    List<ModelComment> commentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);


        // setting up the list
        commentList = new ArrayList<>();
        commentList.add(new ModelComment(
                "First comment",
                "profileUrl",
                "user 001",
                20,
                true
        ));commentList.add(new ModelComment(
                "Second comment",
                "profileUrl",
                "user 002",
                20,
                false
        ));
        commentList.add(new ModelComment(
                "Third comment",
                "profileUrl",
                "user 003",
                20,
                false
        ));
        commentList.add(new ModelComment(
                "First comment",
                "profileUrl",
                "user 004",
                2,
                false
        ));
        RecyclerView recyclerView = findViewById(R.id.commentsRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new AdapterComments(commentList, this));
    }
}