package com.example.missitchat;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
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

    public static final String TAG = "SuggestionsAdapter: ";

    private Context context;
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
        View suggestionEditView  = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.missit_suggestion, viewGroup, false);
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
    public int getItemCount() {
        return this.suggestions.size();
    }

    public void addSuggestion(Suggestion suggestion) {
        this.suggestions.add(suggestion);
        Log.d(TAG, "addSuggestion: Suggestions: " + suggestions.toString());
    }

    public void removeSuggestion(int position) {

        this.suggestions.remove(position);
        Log.d(TAG, "removeSuggestion: Suggestions: " + suggestions.toString());
    }

    public void editSuggestion(Suggestion newSuggestion, int position) {
        this.suggestions.set(position, newSuggestion);
        Log.d(TAG, "editSuggestion: Suggestions: " + suggestions.toString());
    }

    public String getSuggestionTextAt(int i) {
        return this.suggestions.get(i).getBody();
    }

    public ArrayList<String> getSuggestionTexts() {
        ArrayList<String> suggestionsToReturn = new ArrayList<>();

        for (int i = 0; i < getItemCount(); i++) {
            suggestionsToReturn.add(getSuggestionTextAt(i));
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

        public void bindSuggestionEditData(final Suggestion suggestion, final int position, final SuggestionEditListener listener) {

            this.suggestionNr.setText(String.valueOf(position + 1));
            this.suggestionText.setText(suggestion.getBody());



            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View button) {
                    View currFocus = ((Activity) context).getCurrentFocus();
                    if (currFocus != null) currFocus.clearFocus();

                    suggestionText.setOnFocusChangeListener(null);
                    listener.OnRemoveSuggestionButtonClick(position);
                }
            });

//            suggestionText.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                    listener.OnEditSuggestion(new Suggestion(s.toString()), position);
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {}
//            });

            suggestionText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {

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
        public void OnRemoveSuggestionButtonClick(int position);
        public void OnEditSuggestion(Suggestion suggestion, int position);
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
