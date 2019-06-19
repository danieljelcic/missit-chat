package com.example.missitchat;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/*

Issues

- received avatar square and not round
- recycler view doesn't take up the whole screen
  - when only sending messages, they are all displayed, but are not scrollable
  - very strange spacing between received messages
- crashes when receives a message after sending any
- displays sent messages as received after receiving any


 */



public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    private List<Message> messages;
    private Context context;

    RecyclerViewAdapter(Context context) {
        this.messages = new ArrayList<Message>();
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View messageView;
        if(messages.get(i).isReceived()) {
            messageView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_received, viewGroup, false);
        } else {
            messageView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_sent, viewGroup, false);
        }

        return new ViewHolder(messageView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        Log.d(TAG, "onBindViewHolder: called");

        if(messages.get(i).isReceived()) {
            viewHolder.messageHolder.avatar.setBackgroundColor(Color.parseColor(messages.get(i).getMemberData().getColor()));
            viewHolder.messageHolder.name.setText(messages.get(i).getMemberData().getName());
            viewHolder.messageHolder.messageBody.setText(messages.get(i).getMessageBody());
        } else {
            viewHolder.messageHolder.messageBody.setText(messages.get(i).getMessageBody());
        }
        
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void addMessage(Message message) {
        this.messages.add(message);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private MessageViewHolder messageHolder = new MessageViewHolder();
        private ConstraintLayout parentLayout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            messageHolder.avatar = itemView.findViewById(R.id.receivedAvatar);
            messageHolder.name = itemView.findViewById(R.id.receivedUsername);
            messageHolder.messageBody = itemView.findViewById(R.id.messageBody);
            parentLayout = itemView.findViewById(R.id.messageReceivedLayout);
        }
    }

}

