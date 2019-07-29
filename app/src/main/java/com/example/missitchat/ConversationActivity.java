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
import java.util.concurrent.TimeUnit;

public class ConversationActivity extends AppCompatActivity implements SuggestionEditDialogFragment.SuggestionEditListener {

    private static final String TAG = "ConversationActivity";
    private FirebaseAuth auth;
    private DatabaseReference database;
    private EditText messageEdit;
    private MessageViewAdapter messageAdapter;
    private RecyclerView messagesView;
    private SuggestionEditDialogFragment suggestionsDialogFragment;
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
        messageAdapter = new MessageViewAdapter(this, new MessageViewAdapter.MissItResponseListener() {
            @Override
            public void OnSendMissitResponse(Message message, int res_code) {
                sendMissitResponse(message, res_code);
            }
        });
        messagesView.setAdapter(messageAdapter);
        messagesView.setLayoutManager(new LinearLayoutManager(this));


        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        otherUid = getIntent().getStringExtra("uid");
        otherUser = new User(getIntent().getStringExtra("username"), getIntent().getStringExtra("uid"), User.generateRandomColor());

        suggestionsDialogFragment = new SuggestionEditDialogFragment();
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

        missitButton.setColorFilter(ColorManager.getThemeColor(ColorManager.SECONDARY, ConversationActivity.this));
        sendButton.setColorFilter(ColorManager.getThemeColor(ColorManager.PRIMARY, ConversationActivity.this));

        database.child("Conversations").child(auth.getCurrentUser().getUid()).child(otherUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot messages) {

                if (messages.getValue() != null) {
                    List messagesList = new ArrayList<Message>();
                    for (DataSnapshot message : messages.getChildren()) {
                        Message messageItem;

                        if (message.child("missit").getValue() != null) {
                            ArrayList<String> suggestionsList = new ArrayList();

                            for (int i = 0; i < 4; i++) {

                                if(message.child("missit").hasChild("suggestion_" + (i+1))) {
                                    String suggestion = message.child("missit").child("suggestion_" + (i+1)).getValue().toString();
                                    suggestionsList.add(suggestion);
                                }

                            }
                            MissItSuggestions suggestions = new MissItSuggestions(suggestionsList);
                            suggestions.setResponseCode(Integer.parseInt(message.child("missit").child("res_code").getValue().toString()));
                            suggestions.setStatusCode(Integer.parseInt(message.child("missit").child("status_code").getValue().toString()));

                            if (message.child("missit").hasChild("res_timestamp")) {
                                suggestions.setResponseTimestamp(Long.parseLong(message.child("missit").child("res_timestamp").getValue().toString()));
                            }

                            messageItem = new Message(message.child("body").getValue().toString(), otherUser, Boolean.parseBoolean(message.child("is_received").getValue().toString()), Long.parseLong(message.getKey().toString()), suggestions);

                            Log.d(TAG, "onDataChange: downloaded message {" + messageItem.getMessageBody()
                                    + "with suggestions: " + messageItem.getMissItSuggestions().getSuggestions().toString() + ", size: " + messageItem.getMissItSuggestions().getSuggestions().size());
                        } else {
                            messageItem = new Message(message.child("body").getValue().toString(), otherUser, Boolean.parseBoolean(message.child("is_received").getValue().toString()), Long.parseLong(message.getKey().toString()));

                            Log.d(TAG, "onDataChange: downloaded message {" + messageItem.getMessageBody()
                                    + "with no missit suggestions");
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

                String currMessage = messageEdit.getText().toString();

                if (currMessage.isEmpty()) {
                    return;
                }

                Bundle args = new Bundle();

                for (int i = 0; i < currSuggestions.size(); i++) {
                    if (currSuggestions.get(i) != null) {
                        args.putString("suggestion_" + (i + 1), currSuggestions.get(i));
                    }
                }

                args.putString("currMessage", currMessage);

                suggestionsDialogFragment.setArguments(args);
                suggestionsDialogFragment.show(getSupportFragmentManager(), "SuggestionEditDialog");
            }
        });
    }

    private void sendMissitResponse(Message message, final int res_code) {

        final DatabaseReference currentConv = database.child("Conversations")
                .child(auth.getCurrentUser().getUid()).child(otherUid);
        final DatabaseReference otherConv = database.child("Conversations")
                .child(otherUid).child(auth.getCurrentUser().getUid());
        final String timestamp = message.getTimestamp().toString();
        final DatabaseReference currStatusCode = currentConv.child(timestamp + "/missit/status_code");
        final DatabaseReference otherStatusCode = otherConv.child(timestamp + "/missit/status_code");;
        final DatabaseReference currResCode = currentConv.child(timestamp + "/missit/res_code");;
        final DatabaseReference otherResCode = otherConv.child(timestamp + "/missit/res_code");;
        final DatabaseReference currResTimestamp = otherConv.child(timestamp + "/missit/res_timestamp");;
        final DatabaseReference otherResTimestamp = otherConv.child(timestamp + "/missit/res_timestamp");;

        currResCode.setValue(res_code).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "sendMissitResponse: set curr res code to " + res_code);
            currStatusCode.setValue(MissItSuggestions.STATUS_RESPONSE_ESTABLISHING).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d(TAG, "sendMissitResponse: set curr status code to ESTABLISHING");
                    otherStatusCode.setValue(MissItSuggestions.STATUS_RESPONSE_TRANSMITTING).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d(TAG, "sendMissitResponse: set other status code to TRANSMITTING");
                            currStatusCode.setValue(MissItSuggestions.STATUS_RESPONSE_TRANSMITTING).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.d(TAG, "sendMissitResponse: set curr status code to TRANSMITTING");

                                            try {
                                                TimeUnit.SECONDS.sleep((res_code + 1) * 3);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }

                                            final long timestamp = new Timestamp(System.currentTimeMillis()).getTime();

                                            otherResCode.setValue(res_code).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Log.d(TAG, "sendMissitResponse: set other res code to " + res_code);
                                                    currResTimestamp.setValue(timestamp).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Log.d(TAG, "sendMissitResponse: set curr res timestamp to " + timestamp);
                                                            currStatusCode.setValue(MissItSuggestions.STATUS_RESPONSE_SUCCESS).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    Log.d(TAG, "sendMissitResponse: set curr status code to SUCCESS");
                                                                    otherResTimestamp.setValue(timestamp).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            Log.d(TAG, "sendMissitResponse: set other res timestamp to " + timestamp);
                                                                            otherStatusCode.setValue(MissItSuggestions.STATUS_RESPONSE_SUCCESS).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    Log.d(TAG, "sendMissitResponse: set other status code to SUCCESS");
                                                                                }
                                                                            });
                                                                    }
                                                                });
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    public void sendMessage(View view) {

        Log.d(TAG, "sendMessage: called");

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
            missit.put("res_code", String.valueOf(MissItSuggestions.RESPONSE_NO_RESPONSE));
            missit.put("status_code", String.valueOf(MissItSuggestions.STATUS_RESPONSE_NOT_ATTEMPTED));
            missit.put("res_timestamp", String.valueOf(MissItSuggestions.TIMESTAMP_NO_TIMESTAMP));
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
                                                            missitButton.setColorFilter(ColorManager.getThemeColor(ColorManager.SECONDARY, ConversationActivity.this));
                                                            sendButton.setColorFilter(ColorManager.getThemeColor(ColorManager.PRIMARY, ConversationActivity.this));
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

        View messageEditContainer = findViewById(R.id.messageEditContainer);

        currSuggestions = suggestionTexts;


        if (currSuggestions.isEmpty()) {
            messageEditContainer.setBackgroundResource(0);
            missitButton.setColorFilter(ColorManager.getThemeColor(ColorManager.SECONDARY, ConversationActivity.this));
            sendButton.setColorFilter(ColorManager.getThemeColor(ColorManager.PRIMARY, ConversationActivity.this));
        } else {
            messageEditContainer.setBackgroundResource(R.drawable.message_edit_container_bg);
            ColorManager.setDrawableBackgroundColor(messageEditContainer, ColorManager.getThemeColor(ColorManager.PRIMARY, ConversationActivity.this));
            missitButton.setColorFilter(getResources().getColor(R.color.white));
            sendButton.setColorFilter(getResources().getColor(R.color.white));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void OnSend(ArrayList<String> suggestionTexts) {
        Log.d(TAG, "OnSend: called");
        suggestionsDialogFragment.dismiss();
        sendButton.callOnClick();
    }
}
