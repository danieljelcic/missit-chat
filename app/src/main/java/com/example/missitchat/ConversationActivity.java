package com.example.missitchat;

import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
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

public class ConversationActivity extends AppCompatActivity implements MissItSuggestionsDialogFragment.SuggestionEditListener {

    private static final String TAG = "ConversationActivity";
    private FirebaseAuth auth;
    private DatabaseReference database;
    private EditText messageEdit;
    private MessageViewAdapter messageAdapter;
    private RecyclerView messagesView;
    private MissItSuggestionsDialogFragment suggestionsDialogFragment;
    private ArrayList<String> currSuggestions;
    private ImageButton sendButton;
    private ImageButton missitButton;
    private User otherUser;
    private String otherUid;

    // setup

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(ColorManager.getThemeId(getIntent().getStringExtra("username"), this));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        messageEdit = findViewById(R.id.messageEdit);
        sendButton = findViewById(R.id.messageSendBttn);
        missitButton = findViewById(R.id.missitBttn);

        messagesView = findViewById(R.id.messagesView);
        messageAdapter = new MessageViewAdapter(this);
        messagesView.setAdapter(messageAdapter);
        messagesView.setLayoutManager(new LinearLayoutManager(this));


        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        otherUid = getIntent().getStringExtra("uid");
        otherUser = new User(getIntent().getStringExtra("username"), getIntent().getStringExtra("uid"), User.generateRandomColor());

        suggestionsDialogFragment = new MissItSuggestionsDialogFragment();
        currSuggestions = new ArrayList<>();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setBackgroundColor(ColorManager.getThemeColor(ColorManager.PRIMARY, this));
        setSupportActionBar(toolbar);

        View otherAvatar = findViewById(R.id.otherAvatar);
        GradientDrawable avatar = (GradientDrawable) otherAvatar.getBackground();
        avatar.setColor(ColorManager.getColor(otherUser.getName(), ColorManager.SECONDARY, ConversationActivity.this));
        otherAvatar.setBackground(avatar);
        ((TextView)findViewById(R.id.otherUsernameDisplay)).setText(otherUser.getName());

        database.child("Conversations").child(auth.getCurrentUser().getUid()).child(otherUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot messages) {

                if (messages.getValue() != null) {
                    List messagesList = new ArrayList<Message>();
                    for (DataSnapshot message : messages.getChildren()) {
                        Message messageItem = new Message(message.child("body").getValue().toString(), otherUser, Boolean.parseBoolean(message.child("is_received").getValue().toString()), Long.parseLong(message.getKey().toString()));

                        if (message.child("missit").getValue() != null) {
                            messageItem.setMissit(true);
                        }

                        messagesList.add(messageItem);
                    }
                    messageAdapter.loadMessages(messagesList);
                    messagesView.scrollToPosition(messageAdapter.getItemCount() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        missitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();

                for (int i = 0; i < currSuggestions.size(); i++) {
                    if (currSuggestions.get(i) != null) {
                        args.putString("suggestion_" + (i + 1), currSuggestions.get(i));
                    }
                }

                args.putString("currMessage", messageEdit.getText().toString());

                suggestionsDialogFragment.setArguments(args);
                suggestionsDialogFragment.show(getSupportFragmentManager(), "SuggestionEditDialog");
            }
        });
    }

    public void sendMessage(View view) {
        String messageBody = messageEdit.getText().toString();

        if (messageBody.isEmpty()) {
            return;
        }

        final long timestamp = new Timestamp(System.currentTimeMillis()).getTime();

        final HashMap<String, String> message = new HashMap<>();
        message.put("body", messageBody);
        message.put("is_received", "false");

        HashMap<String, String> missit = null;

        if (!currSuggestions.isEmpty()) {
            missit = new HashMap<>();
            for (int i = 0; i < currSuggestions.size(); i++) {
                missit.put("suggestion_" + (i + 1), currSuggestions.get(i));
            }
            missit.put("res_code", "-1");
        }

        // adding to current user's conversation
        final HashMap<String, String> finalMissit = missit;
        database.child("Conversations").child(auth.getCurrentUser().getUid())
                .child(otherUid).child(String.valueOf(timestamp)).setValue(message)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            database.child("Conversations").child(auth.getCurrentUser().getUid())
                                    .child(otherUid).child(String.valueOf(timestamp))
                                    .child("missit").setValue(finalMissit).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    message.remove("is_received");
                                    message.put("is_received", "true");
                                    // adding to other user's conversation
                                    database.child("Conversations").child(otherUid)
                                            .child(auth.getCurrentUser().getUid()).child(String.valueOf(timestamp)).setValue(message)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    database.child("Conversations").child(otherUid)
                                                            .child(auth.getCurrentUser().getUid()).child(String.valueOf(timestamp))
                                                            .child("missit").setValue(finalMissit).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            currSuggestions.clear();
                                                            findViewById(R.id.messageEditContainer).setBackgroundResource(0);
                                                            messageEdit.setText("");
                                                            messagesView.scrollToPosition(messageAdapter.getItemCount() - 1);
                                                        }
                                                    });

                                                }
                                            });
                                }
                            });
                        }
                    }
                });

        // adding to current user's conversation list
        database.child("Users").child(auth.getCurrentUser().getUid())
                .child("conversation-list").child(otherUid).setValue(String.valueOf(timestamp))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // adding to other user's conversation list
                database.child("Users").child(otherUid).child("conversation-list")
                        .child(auth.getCurrentUser().getUid()).setValue(String.valueOf(timestamp))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //
                    }
                });
            }
        });
    }

    public void toast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ConversationActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void OnClose(ArrayList<String> suggestionTexts) {

        Log.d(TAG, "OnClose: Suggestions returned:" + suggestionTexts.toString());

        currSuggestions = suggestionTexts;

        if (currSuggestions.isEmpty()) {
            findViewById(R.id.messageEditContainer).setBackgroundResource(0);
            missitButton.setColorFilter(ColorManager.getThemeColor(ColorManager.SECONDARY, ConversationActivity.this));
            sendButton.setColorFilter(ColorManager.getThemeColor(ColorManager.PRIMARY, ConversationActivity.this));
        } else {
            findViewById(R.id.messageEditContainer).setBackgroundResource(R.drawable.message_edit_container_bg);
            missitButton.setColorFilter(getResources().getColor(R.color.white));
            sendButton.setColorFilter(getResources().getColor(R.color.white));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void OnSend(ArrayList<String> suggestionTexts) {
        OnClose(suggestionTexts);
        sendButton.callOnClick();
    }
}
