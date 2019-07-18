package com.example.missitchat;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
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
import java.util.Collections;
import java.util.List;

public class ConversationListActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference database;
    private User currUser;
    private RecyclerView conversationsView;
    private ConversationViewAdapter conversationViewAdapter;
    private ProgressBar progressBar;
    private NewConversationDialogFragment newConvDialog;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(ColorManager.getThemeId(getIntent().getStringExtra("username"), this));

        super.onCreate(savedInstanceState);

        // set up basic layout
        setContentView(R.layout.activity_conversation_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(ColorManager.getThemeColor(ColorManager.PRIMARY, ConversationListActivity.this));
        toolbar.setTitle("MissIt Chat");
        setSupportActionBar(toolbar);

        newConvDialog = new NewConversationDialogFragment();

        // set up floating action button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newConvDialog.show(getSupportFragmentManager(), "NewConversationDialogFragment");
            }
        });

        // Firebase setup
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
        currUser = new User(getIntent().getStringExtra("username"), getIntent().getStringExtra("uid"), User.generateRandomColor());

        Bundle args = new Bundle();
        args.putString("currUsername", currUser.getName());
        newConvDialog.setArguments(args);

        // set up basic user-related ui elems

        View currAvatar = findViewById(R.id.currAvatar);
        TextView currUsernameDisplay = findViewById(R.id.currUsernameDisplay);

        GradientDrawable avatar = (GradientDrawable) currAvatar.getBackground();
        avatar.setColor(ColorManager.getThemeColor(ColorManager.SECONDARY, ConversationListActivity.this));

        currAvatar.setBackground(avatar);
        currUsernameDisplay.setText(currUser.getName());


        conversationsView = findViewById(R.id.conversationsView);
        conversationViewAdapter = new ConversationViewAdapter(this, new ConversationViewAdapter.OnConversationItemClickListener() {
            @Override
            public void onItemClick(Conversation conversation) {
                Intent mainToConversationIntent = new Intent(ConversationListActivity.this, ConversationActivity.class);
                mainToConversationIntent.putExtra("username", conversation.getUser().getName());
                mainToConversationIntent.putExtra("uid", conversation.getUser().getId());
                startActivity(mainToConversationIntent);
            }
        });
        conversationsView.setAdapter(conversationViewAdapter);
        conversationsView.setLayoutManager(new LinearLayoutManager(this));

        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    protected void onStart() {
        super.onStart();

        progressBar.setVisibility(View.VISIBLE);

        // check whether there's an auth instance
        checkLogInStatus(ConversationListActivity.this);


        database.child("Users").child(auth.getCurrentUser().getUid()).child("conversation-list")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot conversationsSnapshot) {

                // clears the no conversations notice
                if (conversationsSnapshot.hasChildren()) {

                    final List conversations = new ArrayList<Conversation>();
                    final Long[] size = {conversationsSnapshot.getChildrenCount()};

                    for(DataSnapshot conversationSnapshot : conversationsSnapshot.getChildren()) {
                        final String otherUid = conversationSnapshot.getKey();
                        final String messageTimestamp = conversationSnapshot.getValue().toString();

                        database.child("Users").child(otherUid).child("username")
                                .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot usernameSnapshot) {
                                final User user = new User(usernameSnapshot.getValue().toString(), otherUid, User.generateRandomColor());

                                database.child("Conversations").child(auth.getCurrentUser().getUid())
                                        .child(otherUid).child(messageTimestamp)
                                        .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot messageSnapshot) {
                                        String body = messageSnapshot.child("body").getValue().toString();
                                        Boolean is_received = Boolean.parseBoolean(messageSnapshot.child("is_received").getValue().toString());

                                        Conversation conversation = new Conversation(user, new Message(body, user, is_received, Long.parseLong(messageTimestamp)), true);
                                        conversations.add(conversation);
                                        size[0]--;
                                        if (size[0] == 0) {
                                            Collections.sort(conversations);
                                            progressBar.setVisibility(View.GONE);
                                            conversationViewAdapter.loadConversations(conversations);
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        toast("Failed loading conversations: " + databaseError.getMessage());
                                    }
                                });
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                toast("Failed loading conversations: " + databaseError.getMessage());
                            }
                        });
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    ((TextView) findViewById(R.id.noConversationNotice)).setText(getResources().getString(R.string.noConversationNoticeText));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                toast("Failed loading conversations: " + databaseError.getMessage());
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
                checkLogInStatus(ConversationListActivity.this);
            }
        }

        return true;
    }


    public void toast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ConversationListActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
