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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ConversationViewAdapter extends RecyclerView.Adapter<ConversationViewAdapter.ConversationHolder> {

    private static final String TAG = "ConversationViewHolder";

    private List<Conversation> conversations;
    private Context context;

    public ConversationViewAdapter(Context context) {
        this.conversations = new ArrayList<>();
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
        viewHolder.bindConversationData(conversations.get(i));
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

        public View avatar;
        public TextView username;
        public TextView message;

        public ConversationHolder(@NonNull View itemView) {
            super(itemView);

            avatar = itemView.findViewById(R.id.listReceivedAvatar);
            username = itemView.findViewById(R.id.listReceivedUsername);
            message = itemView.findViewById(R.id.listReceivedMessage);

            username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String otherUid = ((TextView) v).getText().toString();
                    Intent mainToConversationIntent = new Intent(context, ConversationActivity.class);
                    mainToConversationIntent.putExtra("otherUid", otherUid);
                    context.startActivity(mainToConversationIntent);
                }
            });
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        public void bindConversationData(Conversation conversation) {

            // get the circle drawable and set its background to user's color
            GradientDrawable avatar = (GradientDrawable) this.avatar.getBackground();
            avatar.setColor(Color.parseColor(conversation.getUser().getColor()));

            // set view holder properties
            this.avatar.setBackground(avatar);
            this.username.setText(conversation.getUser().getName());
            this.message.setText(conversation.getLatestMessage());
        }

    }
}
