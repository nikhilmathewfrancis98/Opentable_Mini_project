package com.example.opentable;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class EditProfile extends AppCompatActivity {
    Button update;
    ImageView profileImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        profileImg = findViewById(R.id.profilePhoto);
        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();            }
        });
        update=findViewById(R.id.upload);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Uploading values to database



    ////////////////////////////////////////////////////////
                Toast.makeText(EditProfile.this, "Edit Successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(EditProfile.this, ProfileActivity.class));
            }
        });

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

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}