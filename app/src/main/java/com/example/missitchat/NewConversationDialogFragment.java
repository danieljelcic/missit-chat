package com.example.missitchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.support.constraint.Constraints.TAG;

public class NewConversationDialogFragment extends DialogFragment {

    private TextInputLayout usernameInput;
    private Button newConversationButton;

    private String username;
    private String currUsername;
    private DatabaseReference database;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_new_conversation, container, false);

        usernameInput = view.findViewById(R.id.newConversationUsernameInput);
        newConversationButton = view.findViewById(R.id.newConversationStartConversationButton);
        currUsername = getArguments().getString("currUsername");

        database = FirebaseDatabase.getInstance().getReference();

        newConversationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usernameInput.getEditText().getText().toString();

                Log.d(TAG, "onClick: username = " + username);

                if (username.isEmpty()) {
                    usernameInput.setError(getResources().getString(R.string.NewConversationDialogErrorEmpty));
                } else if (username.equals(currUsername)) {
                    usernameInput.setError(getResources().getString(R.string.NewConversationDialogErrorOwnUsername));
                } else {
                    database.child("Usernames").child(username).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {

                                Intent newConversationDialogToConversationIntent = new Intent(getContext(), ConversationActivity.class);
                                newConversationDialogToConversationIntent.putExtra("username", username);
                                newConversationDialogToConversationIntent.putExtra("uid", dataSnapshot.getValue().toString());
                                startActivity(newConversationDialogToConversationIntent);
                                getDialog().dismiss();

                            } else {
                                usernameInput.setError(getResources().getString(R.string.NewConversationDialogErrorUsernameDoesntExist));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            usernameInput.setError(getResources().getString(R.string.NewConversationDialogErrorConnection));
                        }
                    });
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // set proper size for the dialog window
        int width = getContext().getResources().getDisplayMetrics().widthPixels;
        int height = getDialog().getWindow().getAttributes().height;
        getDialog().getWindow().setLayout(width, height);
    }
}
