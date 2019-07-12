package com.example.missitchat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference database;
    private MemberData currUser;
    private RecyclerView conversationsView;
    private ConversationViewAdapter conversationViewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set up basic layout
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set up floating action button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Firebase setup
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        conversationsView = findViewById(R.id.conversationsView);
        conversationViewAdapter = new ConversationViewAdapter(this);
        conversationsView.setAdapter(conversationViewAdapter);
        conversationsView.setLayoutManager(new LinearLayoutManager(this));

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onStart() {
        super.onStart();

        // check whether there's an auth instance
        checkLogInStatus(MainActivity.this);

        database.child("Users").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currUser = new MemberData(dataSnapshot.child("username").getValue().toString(), dataSnapshot.child("color").getValue().toString());

                // set up display of curr user's info
                View currAvatar = findViewById(R.id.currAvatar);
                TextView currUsernameDisplay = findViewById(R.id.currUsernameDisplay);

                GradientDrawable avatar = (GradientDrawable) currAvatar.getBackground();
                avatar.setColor(Color.parseColor(currUser.getColor()));

                currAvatar.setBackground(avatar);
                currUsernameDisplay.setText(currUser.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                toast("Error retrieving user data from Firebase");
            }
        });

        database.child("Conversations").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List conversations = new ArrayList<Conversation>();

                // clears the no conversations notice - doesn't work!
                if (dataSnapshot.hasChildren()) {
                    ((TextView) findViewById(R.id.noConversationNotice)).setText("");
                }

                for(DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()) {
                    MemberData user = new MemberData(dataSnapshotChild.getKey().toString(), "#000000");
                    Conversation conversation = new Conversation(user, "New message!", false);
                    conversations.add(conversation);
                }

                conversationViewAdapter.loadConversations(conversations);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // check if user is already logged in,
    // if not, launch the welcome activity
    private void checkLogInStatus(Context context) {
        FirebaseUser currUser = auth.getCurrentUser();
        if (currUser == null) {
            Intent mainToWelcomeIntent = new Intent(context, WelcomeActivity.class);
            startActivity(mainToWelcomeIntent);
            finish();
        }
    }

    // menu in toolbar setup
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    // click listeners for menu items in toolbar menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.logOutMenuItem : {
                auth.signOut();
                checkLogInStatus(MainActivity.this);
            }
        }

        return true;
    }


    public void toast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
