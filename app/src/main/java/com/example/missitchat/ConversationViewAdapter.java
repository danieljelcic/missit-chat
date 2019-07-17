package com.example.missitchat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ConversationViewAdapter extends RecyclerView.Adapter<ConversationViewAdapter.ConversationHolder> {

    private static final String TAG = "ConversationViewHolder";

    private List<Conversation> conversations;
    private OnConversationItemClickListener listener;
    private Context context;

    public ConversationViewAdapter(Context context, OnConversationItemClickListener listener) {
        this.conversations = new ArrayList<>();
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public ConversationHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View conversationView  = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.conversation_list_item, viewGroup, false);
        return new ConversationHolder(conversationView);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(@NonNull ConversationHolder viewHolder, int i) {
        viewHolder.bindConversationData(conversations.get(i), listener);
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    public void loadConversations(List<Conversation> conversations) {
        this.conversations = conversations;
        notifyDataSetChanged();
    }



    class ConversationHolder extends RecyclerView.ViewHolder {

        public View parentLayout;
        public TextView avatar;
        public TextView username;
        public TextView message;
        public TextView time;

        public ConversationHolder(@NonNull View itemView) {
            super(itemView);

            parentLayout = itemView;
            avatar = itemView.findViewById(R.id.listReceivedAvatar);
            username = itemView.findViewById(R.id.listReceivedUsername);
            message = itemView.findViewById(R.id.listReceivedMessage);
            time = itemView.findViewById(R.id.listReceivedTimestamp);
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        public void bindConversationData(final Conversation conversation, final OnConversationItemClickListener listener) {

            String time = new SimpleDateFormat("EEE • HH:mm")
                    .format(new Date(conversation.getLatestMessage().getTimestamp() * 1000));

            // get the circle drawable and set its background to user's color
            GradientDrawable avatar = (GradientDrawable) this.avatar.getBackground();
            avatar.setColor(ColorManager.getColor(conversation.getUser().getName(), ColorManager.SECONDARY, context));


            // set view holder properties
            this.avatar.setBackground(avatar);
            this.avatar.setText(conversation.getUser().getName().substring(0, 1).toUpperCase());
            this.username.setText(conversation.getUser().getName());
            this.time.setText(time);

            if (conversation.getLatestMessage().isReceived()) {
                this.message.setText(conversation.getLatestMessage().getMessageBody());
            } else {
                this.message.setText("You: " + conversation.getLatestMessage().getMessageBody());
            }

            this.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(conversation);
                }
            });
        }

    }

    public interface OnConversationItemClickListener {
        public void onItemClick(Conversation conversation);
    }
}
