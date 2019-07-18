package com.example.missitchat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (auth.getCurrentUser() != null) {
            database.child("Users").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Intent mainToConversationListIntent = new Intent(MainActivity.this, ConversationListActivity.class);
                    mainToConversationListIntent.putExtra("username", dataSnapshot.child("username").getValue().toString());
                    mainToConversationListIntent.putExtra("uid", auth.getCurrentUser().getUid());
                    startActivity(mainToConversationListIntent);
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, "Error retrieving user data from Firebase", Toast.LENGTH_SHORT).show();
                    auth.signOut();
                    Intent mainToWelcomeIntent = new Intent(MainActivity.this, WelcomeActivity.class);
                    startActivity(mainToWelcomeIntent);
                }
            });
        } else {
            Intent mainToWelcomeIntent = new Intent(MainActivity.this, WelcomeActivity.class);
            startActivity(mainToWelcomeIntent);
        }
    }
}
