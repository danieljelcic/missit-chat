package com.example.missitchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText usernameInput;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private Button createAccountButton;
    private FirebaseAuth auth;
    private DatabaseReference database;
    private ProgressDialog connectionProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        connectionProgress = new ProgressDialog(this);
        connectionProgress.setTitle("Creating Account");
        connectionProgress.setMessage("Please wait while we create your account and log you in");
        connectionProgress.setCanceledOnTouchOutside(false);

        usernameInput = findViewById(R.id.registerInputUsername);
        emailInput = findViewById(R.id.registerInputEmail);
        passwordInput = findViewById(R.id.registerInputPassword);
        createAccountButton = findViewById(R.id.registerCreateAccountButton);

        createAccountButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String username = usernameInput.getText().toString();
                final String email = emailInput.getText().toString();
                final String password = passwordInput.getText().toString();

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this, "Please fill out all the fields", Toast.LENGTH_SHORT).show();
                } else {

                    database.child("Usernames").child(username).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            Toast.makeText(RegisterActivity.this, dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
                            if(dataSnapshot.getValue() == null) {
                                Toast.makeText(RegisterActivity.this, "Registering for " + username + ": " + email + " and " + password, Toast.LENGTH_SHORT).show();
                                connectionProgress.show();
                                registerFirebaseUser(username, email, password);
                            } else {
                                Toast.makeText(RegisterActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
            }
        });
    }

    // need to work on this and make sure i understand it !!!!!!!
    private void registerFirebaseUser(final String username, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    connectionProgress.dismiss();
                    final String TAG = "registerFirebaseUser:";
                    if (task.isSuccessful()) {
                        // adding user data to database
                        final String uid = auth.getCurrentUser().getUid();
                        HashMap<String, String> userData = new HashMap<>();
                        userData.put("username", username);
                        userData.put("color", User.generateRandomColor());
                        database.child("Users").child(uid).setValue(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                // upon completion, go to main activity or display err toast
                                if(task.isSuccessful()) {
                                    // proceed to main activity
                                    Log.d(TAG, "createUserWithEmail:success");

                                    database.child("Usernames").child(username).setValue(uid).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task2) {
                                            if (task2.isSuccessful()) {
                                                Intent registerToMainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                                registerToMainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(registerToMainIntent);
                                                finish();
                                            } else {
                                                // if registration of user to database fails, display a message to the user
                                                Toast.makeText(RegisterActivity.this, "Firebase database failure", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    // if registration of user to database fails, display a message to the user
                                    Toast.makeText(RegisterActivity.this, "Firebase database failure", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    } else {
                        // If authentication fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(RegisterActivity.this, "Firebase authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }
}