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
import android.widget.TextView;

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
            return new SuggestionViewHolder(suggestionsView);
        } else {
            Log.d(TAG, "onCreateViewHolder: creating received suggestion view holder");
            suggestionsView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.missit_suggestion_sent, viewGroup, false);
            return new ReceivedSuggestionViewHolder(suggestionsView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionViewHolder suggestionViewHolder, int i) {
        if (getItemViewType(i) == TYPE_SENT) {
            suggestionViewHolder.bindSuggestionViewHolderData(i);
        } /*else {
            ((ReceivedSuggestionViewHolder)suggestionViewHolder).bindReceivedSuggestionViewHolderData(i);
        }*/
    }

    @Override
    public int getItemCount() {
        return suggestions.getSuggestions().size();
    }

     class SuggestionViewHolder extends RecyclerView.ViewHolder {

        private TextView suggestionBody;
        private TextView suggestionNr;

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

            ColorManager.setDrawableBackgroundColor(suggestionBody, ColorManager.getThemeColor(ColorManager.SECONDARY, context));
        }
     }

    class ReceivedSuggestionViewHolder extends SuggestionViewHolder {

        public ReceivedSuggestionViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "ReceivedSuggestionViewHolder: creating received suggestion view holder");
        }

        
        public void bindReceivedSuggestionViewHolderData(int i) {
            this.bindSuggestionViewHolderData(i);
            Log.d(TAG, "bindReceivedSuggestionViewHolderData: binding received suggestion data");
        }
    }

    interface SuggestedResponseSendListener {
        void OnSuggestedResponseSend(int res_code);
    }
}
