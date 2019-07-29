package com.example.missitchat;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;


// need to add keyboard hider!

public class SuggestionEditViewAdapter extends RecyclerView.Adapter<SuggestionEditViewAdapter.SuggestionEditHolder> {

    public static final String TAG = "SuggestionEditAdapter";

    private Context context;
    private RecyclerView parentView;
    private SuggestionEditListener listener;
    private ArrayList<Suggestion> suggestions;

    public SuggestionEditViewAdapter(Context context, SuggestionEditListener relayer) {
        this.context = context;
        this.listener = relayer;
        this.suggestions = new ArrayList<>();
    }

    @NonNull
    @Override
    public SuggestionEditHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View suggestionEditView  = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.missit_suggestion_editable, viewGroup, false);
        return new SuggestionEditHolder(suggestionEditView);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionEditHolder viewHolder, int i) {
        viewHolder.bindSuggestionEditData(suggestions.get(i), i, this.listener);
        if (suggestions.get(i).getBody().isEmpty()) {
            viewHolder.suggestionText.requestFocus();
        }
    }

    @Override
    public void onViewRecycled(@NonNull SuggestionEditHolder holder) {
        super.onViewRecycled(holder);

        holder.suggestionText.setOnFocusChangeListener(null);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.parentView = recyclerView;
    }

    @Override
    public int getItemCount() {
        return this.suggestions.size();
    }

    public void addSuggestion(Suggestion suggestion) {

        consolidateSuggestionEdits();
        this.suggestions.add(suggestion);
        listener.OnSuggestionNumberChanged(getItemCount());
        View lastSuggestion = this.parentView.getLayoutManager().findViewByPosition(getItemCount() - 1);
        if (lastSuggestion != null) lastSuggestion.findViewById(R.id.suggestionText).callOnClick();
        Log.d(TAG, "addSuggestion: lastSuggestion == null = " + (lastSuggestion == null));
        Log.d(TAG, "addSuggestion: Suggestions: " + suggestions.toString());
    }

    public void removeSuggestion(int position) {

        consolidateSuggestionEdits();
        this.suggestions.remove(position);
        listener.OnSuggestionNumberChanged(getItemCount());
        int currSuggestionPosition = (position == getItemCount()) ? position - 1 : position;
        View currSuggestion = this.parentView.getLayoutManager().findViewByPosition(currSuggestionPosition);
        if (currSuggestion != null) currSuggestion.findViewById(R.id.suggestionText).callOnClick();
        Log.d(TAG, "removeSuggestion: currSuggestion == null = " + (currSuggestion == null));
        Log.d(TAG, "removeSuggestion: Suggestions: " + suggestions.toString());
    }

    private void consolidateSuggestionEdits() {
        View firstMessage = this.parentView.getLayoutManager().findViewByPosition(0);
        if (firstMessage != null) firstMessage.requestFocus();
    }

    public void editSuggestion(Suggestion newSuggestion, int position) {
        this.suggestions.set(position, newSuggestion);
        Log.d(TAG, "editSuggestion: Suggestions: " + suggestions.toString());
    }

    public String getSuggestionTextAt(int i) {
        return this.suggestions.get(i).getBody();
    }

    public ArrayList<String> getSuggestionTexts() {

        consolidateSuggestionEdits();


        ArrayList<String> suggestionsToReturn = new ArrayList<>();

        for (int i = 0; i < getItemCount(); i++) {
            String currSuggestionText = getSuggestionTextAt(i);
            if (!currSuggestionText.isEmpty()) {
                suggestionsToReturn.add(getSuggestionTextAt(i));
            }
        }

        return suggestionsToReturn;
    }

    class SuggestionEditHolder extends RecyclerView.ViewHolder {

        private View parentLayout;
        private TextView suggestionNr;
        private EditText suggestionText;
        private ImageButton button;

        public SuggestionEditHolder(@NonNull View itemView) {
            super(itemView);

            this.parentLayout = itemView;
            this.suggestionNr = itemView.findViewById(R.id.suggestionNumber);
            this.suggestionText = itemView.findViewById(R.id.suggestionText);
            this.button = itemView.findViewById(R.id.doneEditingButton);
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        public void bindSuggestionEditData(final Suggestion suggestion, final int position, final SuggestionEditListener listener) {

            this.suggestionNr.setText(String.valueOf(position + 1));
            this.suggestionText.setText(suggestion.getBody());

            GradientDrawable bubble = (GradientDrawable) suggestionText.getBackground();
            bubble.setColor(ColorManager.getThemeColor(ColorManager.SECONDARY, context));
            suggestionText.setBackground(bubble);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View button) {
                    listener.OnRemoveSuggestionButtonClick(position);
                }
            });

            suggestionText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {

                    if (hasFocus) {
                        Log.d(TAG, "onFocusChange: suggestion [" + position + "] has focus");
                    } else {
                        Log.d(TAG, "onFocusChange: suggestion [" + position + "] lost focus");
                    }


                    String editingSuggestionBody = ((EditText) v).getText().toString();
                    if (editingSuggestionBody.isEmpty()) {
//                            button.callOnClick();
                    } else {
                        listener.OnEditSuggestion(new Suggestion(editingSuggestionBody), position);
                    }
                }
            });

        }
    }

    interface SuggestionEditListener {
        void OnRemoveSuggestionButtonClick(int position);
        void OnEditSuggestion(Suggestion suggestion, int position);
        void OnSuggestionNumberChanged(int size);
    }

    public static class Suggestion {

        private String body;
        private boolean isBeingEdited;

        public Suggestion() {
            this.body = "";
            this.isBeingEdited = false;
        }

        public Suggestion(String body) {
            this.body = body;
            this.isBeingEdited = false;
        }


        public String getBody() {
            return body;
        }

        public boolean isBeingEdited() {
            return isBeingEdited;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public void setBeingEdited(boolean beingEdited) {
            isBeingEdited = beingEdited;
        }

        @Override
        public String toString() {
            return body;
        }
    }
}
