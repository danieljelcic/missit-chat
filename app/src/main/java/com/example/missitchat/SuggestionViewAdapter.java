package com.example.missitchat;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class SuggestionViewAdapter extends RecyclerView.Adapter<SuggestionViewAdapter.SuggestionViewHolder> {

    private static final String TAG = "SuggestionViewAdapter";
    
    public static final int TYPE_SENT = 0;
    public static final int TYPE_RECEIVED = 1;

    private Context context;
    private MissItSuggestions suggestions;
    private int type;
    private SuggestedResponseSendListener listener;

    public SuggestionViewAdapter(Context context, MissItSuggestions suggestions, int type, SuggestedResponseSendListener listener) {

        Log.d(TAG, "SuggestionViewAdapter: constructing adapter for suggestions: " + suggestions.getSuggestions().toString() + ", size: " + suggestions.getSuggestions().size());
        
        this.context = context;
        this.suggestions = suggestions;
        this.type = type;
        this.listener = listener;
    }

    public SuggestionViewAdapter(Context context, MissItSuggestions suggestions, int type) {
        this(context, suggestions, type, null);
    }

    @Override
    public int getItemViewType(int position) {
        return this.type;
    }

    @NonNull
    @Override
    public SuggestionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View suggestionsView;

        if (i == TYPE_SENT) {
            Log.d(TAG, "onCreateViewHolder: creating sent suggestion view holder");
            suggestionsView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.missit_suggestion_sent, viewGroup, false);
            return new SentSuggestionViewHolder(suggestionsView);
        } else {
            Log.d(TAG, "onCreateViewHolder: creating received suggestion view holder");
            suggestionsView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.missit_suggestion_received, viewGroup, false);
            return new ReceivedSuggestionViewHolder(suggestionsView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionViewHolder suggestionViewHolder, int i) {
        suggestionViewHolder.bindSuggestionViewHolderData(i);
    }

    @Override
    public int getItemCount() {
        return suggestions.getSuggestions().size();
    }

     class SuggestionViewHolder extends RecyclerView.ViewHolder {

        protected TextView suggestionBody;
        protected TextView suggestionNr;

        public SuggestionViewHolder(@NonNull View itemView) {

            super(itemView);
            Log.d(TAG, "SuggestionViewHolder: creating sent suggestion view holder");

            suggestionBody = itemView.findViewById(R.id.suggestionText);
            suggestionNr = itemView.findViewById(R.id.suggestionNumber);
        }

        
        public void bindSuggestionViewHolderData(int i) {

            Log.d(TAG, "bindSuggestionViewHolderData: binding sent suggestion data");

            String suggestion = suggestions.getSuggestionAt(i);

            suggestionNr.setText(String.valueOf(i+1));
            suggestionBody.setText(suggestion);
        }
     }

     class SentSuggestionViewHolder extends SuggestionViewHolder {

         public SentSuggestionViewHolder(@NonNull View itemView) {
             super(itemView);
             Log.d(TAG, "SentSuggestionViewHolder: creating sent suggestion view holder");
         }

         @Override
         public void bindSuggestionViewHolderData(int i) {
             super.bindSuggestionViewHolderData(i);
             Log.d(TAG, "bindSentSuggestionViewHolderData: binding sent suggestion data");

             ColorManager.setDrawableBackgroundColor(super.suggestionBody, ColorManager.getThemeColor(ColorManager.SECONDARY, context));
         }
     }

    class ReceivedSuggestionViewHolder extends SuggestionViewHolder {

        private ImageButton sendButton;
        private View suggestionContainer;
        private ProgressBar progressBar;

        public ReceivedSuggestionViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "ReceivedSuggestionViewHolder: creating received suggestion view holder");

            this.sendButton = itemView.findViewById(R.id.suggestionSendBttn);
            this.suggestionContainer = itemView.findViewById(R.id.receivedSuggestionContainer);
            this.progressBar = itemView.findViewById(R.id.progressBar);

            sendButton.setVisibility(View.INVISIBLE);
            suggestionContainer.setTranslationX(0);
            progressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        public void bindSuggestionViewHolderData(int i) {
            super.bindSuggestionViewHolderData(i);

            suggestionBody.setFocusableInTouchMode(true);

            suggestionBody.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (suggestionBody.hasFocus()) {
                        suggestionBody.clearFocus();
                    } else {
                        suggestionBody.requestFocus();
                    }

                    Log.d(TAG, "onClick: clicked suggestion #" + suggestionNr.getText());
                }
            });

            suggestionBody.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        Log.d(TAG, "onFocusChange: suggestion #" + suggestionNr.getText() + " has focus");
                        sendButton.setVisibility(View.VISIBLE);
                        suggestionContainer.setTranslationX(context.getResources().getDimension(R.dimen.received_suggestion_onclick_translation));
                    } else {
                        Log.d(TAG, "onFocusChange: suggestion #" + suggestionNr.getText() + " lost focus");
                        sendButton.setVisibility(View.INVISIBLE);
                        suggestionContainer.setTranslationX(0);
                    }
                }
            });

            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Should send suggestion #" + suggestionNr.getText() + ": " + suggestionBody.getText(), Toast.LENGTH_SHORT).show();
                    listener.OnSuggestedResponseSendClick(Integer.parseInt(suggestionNr.getText().toString()) - 1);
                    sendButton.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    interface SuggestedResponseSendListener {
        void OnSuggestedResponseSendClick(int res_code);
    }
}
