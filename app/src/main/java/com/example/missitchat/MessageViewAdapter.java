package com.example.missitchat;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MessageViewAdapter extends RecyclerView.Adapter<MessageViewAdapter.MessageHolder> {

    private static final String TAG = "MessageViewAdapter";

    private static final int TYPE_RECEIVED = 0;
    private static final int TYPE_SENT = 1;
    private static final int TYPE_MISSIT_RECEIVED_UNANSWERED = 2;
    private static final int TYPE_MISSIT_RECEIVED_ANSWERED = 3;
    private static final int TYPE_MISSIT_SENT_UNANSWERED = 4;
    private static final int TYPE_MISSIT_SENT_ANSWERED = 5;

    private List<Message> messages;
    private Context context;
    private MissItResponseListener missItResponseListener;

    MessageViewAdapter(Context context, MissItResponseListener missItResponseListener) {
        this.messages = new ArrayList<>();
        this.context = context;
        this.missItResponseListener = missItResponseListener;
    }

    @Override
    public int getItemViewType(int position) {

        int type;
        Message message = messages.get(position);

        if (message.getMissItSuggestions() == null) {
            if (message.isReceived()) {
                type = TYPE_RECEIVED;
            } else {
                type = TYPE_SENT;
            }

            Log.d(TAG, "getItemViewType: getting item type " + type + " for:"
                    + " isMissit = " + (message.getMissItSuggestions() != null)
                    + ", isReceived = " + message.isReceived());
        } else {

            boolean isUnanswered = message.getMissItSuggestions().getStatusCode() == MissItSuggestions.STATUS_RESPONSE_NOT_ATTEMPTED
                    || message.getMissItSuggestions().getStatusCode() == MissItSuggestions.STATUS_RESPONSE_FAILED;

            if (message.isReceived()) {
                if (isUnanswered) {
                    type = TYPE_MISSIT_RECEIVED_UNANSWERED;
                } else {
                    type = TYPE_MISSIT_RECEIVED_ANSWERED;
                }
            } else {
                if (isUnanswered) {
                    type = TYPE_MISSIT_SENT_UNANSWERED;
                } else {
                    type = TYPE_MISSIT_SENT_ANSWERED;
                }
            }

            Log.d(TAG, "getItemViewType: getting item type " + type + " for:"
                    + " isMissit = " + (message.getMissItSuggestions() != null)
                    + ", isReceived = " + message.isReceived()
                    + ", isUnanswered = " + isUnanswered);
        }

        return type;
    }

    // creates a new (here, empty) ViewHolder based on a layout passed into the layout inflater
    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View messageView;

        Log.d(TAG, "onCreateViewHolder: creating view of type " + i);

        if (i == TYPE_RECEIVED) {
//            Log.d(TAG, "onCreateViewHolder: received");
            // inflate a new message_received layout and pass it to the ViewHolder constructor
            messageView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_received, viewGroup, false);
            return new ReceivedMessageHolder(messageView);
        } else if (i == TYPE_SENT) {
//            Log.d(TAG, "onCreateViewHolder: sent");
            // inflate a new message_sent layout and pass it to the ViewHolder constructor
            messageView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_sent, viewGroup, false);
            return new SentMessageHolder(messageView);
        } else if (i == TYPE_MISSIT_SENT_UNANSWERED) {
            Log.d(TAG, "onCreateViewHolder: creating missit sent unanswered message");
            messageView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.missit_message_sent_unanswered, viewGroup, false);
            return new SentMissitUnansweredMessageHolder(messageView);
        } else if (i == TYPE_MISSIT_RECEIVED_UNANSWERED) {
            Log.d(TAG, "onCreateViewHolder: creating missit received unanswered message");
            messageView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.missit_message_received_unanswered, viewGroup, false);
            return new ReceivedMissitUnansweredMessageHolder(messageView);
        } else if (i == TYPE_MISSIT_SENT_ANSWERED) {
            Log.d(TAG, "onCreateViewHolder: creating missit sent answered message");
            messageView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.missit_message_sent_answered, viewGroup, false);
            return new SentMissitAnsweredMessageHolder(messageView);
        } else if (i == TYPE_MISSIT_RECEIVED_ANSWERED) {
            Log.d(TAG, "onCreateViewHolder: creating missit received answered message");
            messageView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.missit_message_received_answered, viewGroup, false);
            return new ReceivedMissitAnsweredMessageHolder(messageView);
        } else {
            Log.d(TAG, "onCreateViewHolder: creating fallback empty message");
            return new MessageHolder(new View(context));
        }
    }


    // binds data to the ViewHolder corresponding to position i in the data set
    // here, since there are two different view holder types, there's a conditional statement
    // that sets different things in different cases
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(@NonNull MessageHolder messageHolder, int i) {
        messageHolder.bindMessageData(messages.get(i));
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

    public void loadMessages(List<Message> messages) {

        this.messages = messages;
        notifyDataSetChanged();

        // DEBUG
        printMessages();
    }

    // outputs the entire messages data set to the log
    // used for debugging
    private void printMessages() {
        for (Message message : messages) {
//            Log.d("MESSAGE: ", message.toString());
        }
    }

    // implements a view holder as a basic message holder with just the message body
    class MessageHolder extends RecyclerView.ViewHolder {

        protected TextView messageBody;
        protected TextView timestamp;
        protected View parentLayout;

        public MessageHolder(@NonNull View itemView) {
            super(itemView);
            messageBody = itemView.findViewById(R.id.currMessageBody);
            timestamp = itemView.findViewById(R.id.timestamp);
            parentLayout = itemView;
        }

        public void bindMessageData(Message message) {
            this.messageBody.setText(message.getMessageBody());

            String time = new SimpleDateFormat("EEE • HH:mm").format(new Date(message.getTimestamp()));

            this.timestamp.setText(time);
        }
    }


    final class SentMessageHolder extends MessageHolder {

        public SentMessageHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void bindMessageData(Message message) {
            super.bindMessageData(message);

            ColorManager.setDrawableBackgroundColor(this.messageBody, context.getResources().getColor(R.color.colorMessageLight));
        }
    }


    // extends the message holder with name and avatar attributes for displaying received messages
    final class ReceivedMessageHolder extends MessageHolder {

        public ReceivedMessageHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void bindMessageData(Message message) {
            super.bindMessageData(message);
            ColorManager.setDrawableBackgroundColor(this.messageBody, ColorManager.getThemeColor(ColorManager.SECONDARY, context));
        }
    }

    class MissitMessageHolder extends MessageHolder {

        public MissitMessageHolder(@NonNull View itemView) {
            super(itemView);

            View currMessageContainer = itemView.findViewById(R.id.currMessageContainer);

            this.messageBody = currMessageContainer.findViewById(R.id.currMessageBody);
            this.timestamp = currMessageContainer.findViewById(R.id.timestamp);
        }
    }


    class MissitUnansweredMessageHolder extends MissitMessageHolder {

        protected View suggestionsLayout;
        protected RecyclerView suggestionView;

        public MissitUnansweredMessageHolder(@NonNull View itemView) {
            super(itemView);

            suggestionsLayout = itemView.findViewById(R.id.suggestionsLayout);
            suggestionView = itemView.findViewById(R.id.suggestionView);
        }
    }


    final class SentMissitUnansweredMessageHolder extends MissitUnansweredMessageHolder {

        public SentMissitUnansweredMessageHolder(@NonNull View itemView) {
            super(itemView);


            ColorManager.setDrawableBackgroundColor(this.messageBody, context.getResources().getColor(R.color.colorMessageLight));
            ColorManager.setDrawableBackgroundColor(this.suggestionsLayout, ColorManager.getThemeColor(ColorManager.PRIMARY, context));
        }

        @Override
        public void bindMessageData(Message message) {
            super.bindMessageData(message);

            SuggestionViewAdapter adapter = new SuggestionViewAdapter(context, message.getMissItSuggestions(), SuggestionViewAdapter.TYPE_SENT);

            super.suggestionView.setAdapter(adapter);
            super.suggestionView.setLayoutManager(new LinearLayoutManager(context));

            adapter.notifyDataSetChanged();
        }
    }


    final class ReceivedMissitUnansweredMessageHolder extends MissitUnansweredMessageHolder {

        public ReceivedMissitUnansweredMessageHolder(@NonNull View itemView) {
            super(itemView);

            ColorManager.setDrawableBackgroundColor(this.messageBody, ColorManager.getThemeColor(ColorManager.SECONDARY, context));
            ColorManager.setDrawableBackgroundColor(this.suggestionsLayout, ColorManager.getThemeColor(ColorManager.PRIMARY, context));
        }

        @Override
        public void bindMessageData(final Message message) {
            super.bindMessageData(message);


            SuggestionViewAdapter adapter = new SuggestionViewAdapter(context, message.getMissItSuggestions(),
                    SuggestionViewAdapter.TYPE_RECEIVED, new SuggestionViewAdapter.SuggestedResponseSendListener() {
                @Override
                public void OnSuggestedResponseSendClick(int res_code) {
                    missItResponseListener.OnSendMissitResponse(message, res_code);
                }
            });

            super.suggestionView.setAdapter(adapter);
            super.suggestionView.setLayoutManager(new LinearLayoutManager(context));

            adapter.notifyDataSetChanged();
        }
    }

    class MissitAnsweredMessageHolder extends MissitMessageHolder {

        protected TextView responseBody;
        protected TextView responseTimestamp;

        public MissitAnsweredMessageHolder(@NonNull View itemView) {
            super(itemView);

            View missitResponseContainer = itemView.findViewById(R.id.missitResponseContainer);

            responseBody = missitResponseContainer.findViewById(R.id.currMessageBody);
            responseTimestamp = missitResponseContainer.findViewById(R.id.timestamp);
        }

        @Override
        public void bindMessageData(Message message) {
            super.bindMessageData(message);

            String text = "";

            if (message.getMissItSuggestions().getStatusCode() == MissItSuggestions.STATUS_RESPONSE_TRANSMITTING
                    && !message.isReceived()) {
                text = "Receiving response...";
            } else if (message.getMissItSuggestions().getResponseCode() != MissItSuggestions.RESPONSE_NO_RESPONSE) {
                text = message.getMissItSuggestions().getSuggestionAt(message.getMissItSuggestions().getResponseCode());
            } else {
                text = "Something went wrong with this one.";
            }

            this.responseBody.setText(text);

            if (message.getMissItSuggestions().getStatusCode() == MissItSuggestions.STATUS_RESPONSE_SUCCESS) {
                String time = new SimpleDateFormat("EEE • HH:mm").format(new Date(message.getMissItSuggestions().getResponseTimestamp()));

                this.responseTimestamp.setText(time);
            } else {
                responseTimestamp.setVisibility(View.GONE);
            }
        }
    }


    final class ReceivedMissitAnsweredMessageHolder extends MissitAnsweredMessageHolder {

        private View progressContainer;
        private ProgressBar sendingProgressBar;
        private TextView progressText;

        public ReceivedMissitAnsweredMessageHolder(@NonNull View itemView) {
            super(itemView);

            progressContainer = itemView.findViewById(R.id.missitResponseContainer).findViewById(R.id.sendingProgressContainer);
            sendingProgressBar = progressContainer.findViewById(R.id.sendingProgressBar);
            progressText = progressContainer.findViewById(R.id.remainingText);
        }

        @Override
        public void bindMessageData(Message message) {
            super.bindMessageData(message);

            ColorManager.setDrawableBackgroundColor(this.messageBody, ColorManager.getThemeColor(ColorManager.SECONDARY, context));

            if (message.getMissItSuggestions().getStatusCode() == MissItSuggestions.STATUS_RESPONSE_ESTABLISHING) {
                progressContainer.setVisibility(View.VISIBLE);
                progressText.setText("Connecting");
                progressText.setVisibility(View.VISIBLE);
            } else if (message.getMissItSuggestions().getStatusCode() == MissItSuggestions.STATUS_RESPONSE_TRANSMITTING) {
                // the whole transmitting thing with the progress bar

                final int maxTime = message.getMissItSuggestions().getResponseCode() + 1;
                final int[] currTime = {0};

                // max time in miliseconds
                sendingProgressBar.setMax(maxTime * 3000);
                sendingProgressBar.setProgress(currTime[0]);
                progressText.setText(maxTime + " sec");

                sendingProgressBar.setVisibility(View.VISIBLE);
                progressText.setVisibility(View.VISIBLE);
                progressContainer.setVisibility(View.VISIBLE);

                final Timer timer = new Timer();
                final Handler handler = new Handler();

                final Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        // set progress and visibility

                        if (currTime[0] <= sendingProgressBar.getMax()) {
                            sendingProgressBar.setProgress(currTime[0]);

                            // if current time is a full second
                            if (currTime[0] % 3000 == 0) {
                                // set the progress text to remaining time
                                progressText.setText(maxTime - currTime[0] / 3000 + " sec");
                            }
                        } else {
                            timer.cancel();
                            sendingProgressBar.setVisibility(View.GONE);
                            progressText.setVisibility(View.GONE);
                            progressContainer.setVisibility(View.GONE);

                        }
                    }
                };

                // iterator; iterates i by 10 every 100 milliseconds, which gives a frame rate of 10/s
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        currTime[0] += 10;
                        handler.post(runnable);
                    }
                }, 0, 100);


            } else if (message.getMissItSuggestions().getStatusCode() == MissItSuggestions.STATUS_RESPONSE_SUCCESS) {

                ColorManager.setDrawableBackgroundColor(this.responseBody, ColorManager.getThemeColor(ColorManager.PRIMARY, context));
                this.responseBody.setTextColor(context.getResources().getColor(R.color.white));

                sendingProgressBar.setVisibility(View.GONE);
                progressText.setVisibility(View.GONE);
                progressContainer.setVisibility(View.GONE);
            }
        }
    }

    final class SentMissitAnsweredMessageHolder extends MissitAnsweredMessageHolder {

        private ProgressBar receivingProgressBar;

        public SentMissitAnsweredMessageHolder(@NonNull View itemView) {
            super(itemView);

            receivingProgressBar = itemView.findViewById(R.id.missitResponseContainer).findViewById(R.id.receivingProgressBar);
        }

        @Override
        public void bindMessageData(Message message) {
            super.bindMessageData(message);


            ColorManager.setDrawableBackgroundColor(this.messageBody, context.getResources().getColor(R.color.colorMessageLight));
            ColorManager.setDrawableBackgroundColor(this.responseBody, ColorManager.getThemeColor(ColorManager.PRIMARY, context));

            if (message.getMissItSuggestions().getStatusCode() == MissItSuggestions.STATUS_RESPONSE_TRANSMITTING) {
                receivingProgressBar.setVisibility(View.VISIBLE);
            } else {
                receivingProgressBar.setVisibility(View.GONE);
            }
        }
    }

    interface MissItResponseListener {
        void OnSendMissitResponse(Message message, int res_code);
    }
}