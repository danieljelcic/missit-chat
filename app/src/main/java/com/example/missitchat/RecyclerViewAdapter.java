package com.example.missitchat;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/*

Issues

- received avatar square and not round   ✔
- recycler view doesn't take up the whole screen   ✔
- when only sending messages, they are all displayed, but are not scrollable   ✔
- very strange spacing between received messages   ✔
- crashes when receives a message after sending any   ✔
- displays sent messages as received after receiving any   ✔


 */



public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MessageHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    private List<Message> messages;
    private Context context;

    RecyclerViewAdapter(Context context) {
        this.messages = new ArrayList<>();
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isReceived() ? 0 : 1;
    }

    // creates a new (here, empty) ViewHolder based on a layout passed into the layout inflater
    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View messageView;

        if (i == 0) {
            Log.d(TAG, "onCreateViewHolder: received");
            // inflate a new message_received layout and pass it to the ViewHolder constructor
            messageView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_received, viewGroup, false);
            return new ReceivedMessageHolder(messageView);
        } else {
            Log.d(TAG, "onCreateViewHolder: sent");
            // inflate a new message_sent layout and pass it to the ViewHolder constructor
            messageView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_sent, viewGroup, false);
            return new MessageHolder(messageView);
        }
    }


    // binds data to the ViewHolder corresponding to position i in the data set
    // here, since there are two different view holder types, there's a conditional statement
    // that sets different things in different cases
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(@NonNull MessageHolder messageHolder, int i) {

        if (messages.get(i).isReceived()) {
            Log.d(TAG, "onBindViewHolder: received for message [" + i + "] = " + messages.get(i));
            ((ReceivedMessageHolder)messageHolder).bindReceivedMessageData(messages.get(i));
        } else {
            Log.d(TAG, "onBindViewHolder: sent for message [" + i + "] = " + messages.get(i));
            messageHolder.bindMessageData(messages.get(i));
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    // adds a new message to the data set and notifies the adapted about the change
    public void addMessage(Message message) {

        this.messages.add(message);
        notifyItemInserted(messages.size() - 1);

        // DEBUG
        printMessages();
    }

    // outputs the entire messages data set to the log
    // used for debugging
    private void printMessages() {
        for (Message message : messages) {
            Log.d("MESSAGE: ", message.toString());
        }
    }

    // implements a view holder as a basic message holder with just the message body
    class MessageHolder extends RecyclerView.ViewHolder {

        public TextView messageBody;

        public MessageHolder(@NonNull View itemView) {
            super(itemView);
            messageBody = itemView.findViewById(R.id.messageBody);
        }

        public void bindMessageData(Message message) {
            this.messageBody.setText(message.getMessageBody());
        }
    }

    // extends the message holder with name and avatar attributes for displaying received messages
    class ReceivedMessageHolder extends MessageHolder {

        public View avatar;
        public TextView name;

        public ReceivedMessageHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.receivedAvatar);
            name = itemView.findViewById(R.id.receivedUsername);
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        public void bindReceivedMessageData(Message message) {
            this.bindMessageData(message);

            // get the circle drawable and set its background to user's color
            GradientDrawable avatar = (GradientDrawable) this.avatar.getBackground();
            avatar.setColor(Color.parseColor(message.getMemberData().getColor()));

            // set view holder properties
            this.avatar.setBackground(avatar);
            this.name.setText(message.getMemberData().getName());

        }
    }
}