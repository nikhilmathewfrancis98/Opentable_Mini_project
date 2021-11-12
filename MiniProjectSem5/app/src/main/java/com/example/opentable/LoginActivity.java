package com.example.opentable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth; // firebase auth variable (sign in)
    Button signIn;
    EditText userName, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextView signup= findViewById(R.id.signup);
        mAuth = FirebaseAuth.getInstance(); // getting firebase instance for auth purpose
//        Task<AuthResult> task = mAuth;

        signIn = findViewById(R.id.signInButton);
        userName = findViewById(R.id.username);
        password = findViewById(R.id.password);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(LoginActivity.this,SignupActivity.class); // Intent to signup page

                startActivity(i);
            }
        });


        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_name, pass;
                user_name = userName.getText().toString();
                pass = password.getText().toString();

                mAuth.signInWithEmailAndPassword(user_name, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else
                            Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

//    @Override
//    public void onStart() {
//        // Check if user is signed in (non-null). If in, then goto home page
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser != null){
//            Intent i = new Intent(LoginActivity.this, HomeActivity.class);
//            startActivity(i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
//        }
//        else {
//            Toast.makeText(LoginActivity.this, "User Signed out", Toast.LENGTH_SHORT).show();
//        }
//        super.onStart();
//    }
}
