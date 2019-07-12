package com.example.missitchat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConversationActivity extends AppCompatActivity /* implements RoomListener */ {

    private FirebaseAuth auth;
    private DatabaseReference database;
    private EditText messageEdit;
    private MessageViewAdapter messageAdapter;
    private RecyclerView messagesView;
    private MemberData otherUser;
    private String otherUid;

    // setup

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        messageEdit = (EditText) findViewById(R.id.messageEdit);
        messagesView = (RecyclerView) findViewById(R.id.messagesView);
        messageAdapter = new MessageViewAdapter(this);
        messagesView.setAdapter(messageAdapter);
        messagesView.setLayoutManager(new LinearLayoutManager(this));

        otherUid = getIntent().getStringExtra("otherUid");

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        database.child("Users").child(otherUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                otherUser = new MemberData(dataSnapshot.child("username").getValue().toString(),  dataSnapshot.child("color").getValue().toString());

                Toolbar toolbar = findViewById(R.id.toolbar);
                toolbar.setTitle(otherUser.getName());
                setSupportActionBar(toolbar);

                database.child("Conversations").child(auth.getCurrentUser().getUid()).child(otherUid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot messages) {
                        List messagesList = new ArrayList<Message>();
                        for(DataSnapshot message : messages.getChildren()) {
                            Message messageItem = new Message(message.child("body").getValue().toString(), otherUser, Boolean.parseBoolean(message.child("is_received").getValue().toString()));
                            messagesList.add(messageItem);
                        }
                        messageAdapter.loadMessages(messagesList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void sendMessage(View view) {
        String messageBody = messageEdit.getText().toString();
        final long timestamp = new Timestamp(System.currentTimeMillis()).getTime();

        final HashMap<String, String> message = new HashMap<>();
        message.put("body", messageBody);
        message.put("is_received", "false");

        database.child("Conversations").child(auth.getCurrentUser().getUid())
            .child(otherUid).child(String.valueOf(timestamp)).setValue(message)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        message.replace("is_received", "true");
                        database.child("Conversations").child(otherUid)
                            .child(auth.getCurrentUser().getUid()).child(String.valueOf(timestamp)).setValue(message)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    // add message to the view
                                }
                            });
                    }
                }
            });

        messageEdit.setText("");
    }

    public void toast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ConversationActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
