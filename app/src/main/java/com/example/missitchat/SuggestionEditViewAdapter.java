package com.example.missitchat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

// need to add keyboard hider!

public class SuggestionEditViewAdapter extends RecyclerView.Adapter<SuggestionEditViewAdapter.SuggestionEditHolder> {

    private Context context;
    private SuggestionEditRelayer relayer;
    private List<Suggestion> suggestions;

    public SuggestionEditViewAdapter(Context context, SuggestionEditRelayer relayer) {
        this.context = context;
        this.relayer = relayer;
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
        viewHolder.bindSuggestionEditData(suggestions.get(i), i, this.relayer);
    }

    @Override
    public int getItemCount() {
        return this.suggestions.size();
    }

    public void addSuggestion(Suggestion suggestion) {
        this.suggestions.add(suggestion);
    }

    public void removeSuggestion(int position) {
        this.suggestions.remove(position);
    }

    public void editSuggestion(Suggestion newSuggestion, int position) {
        this.suggestions.set(position, newSuggestion);
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

        public void bindSuggestionEditData(final Suggestion suggestion, final int position, final SuggestionEditRelayer relayer) {

            this.suggestionNr.setText(String.valueOf(position + 1));
            this.suggestionText.setText(suggestion.getBody());

            final View.OnClickListener resetDoneButton = new View.OnClickListener() {
                @Override
                public void onClick(View button) {
                    relayer.removeSuggestion(position);
                    ((ImageButton) button).setImageResource(R.drawable.ic_remove_circle_black_24dp);
                }
            };

            button.setOnClickListener(resetDoneButton);

            suggestionText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View textEdit) {

                    button.setImageResource(R.drawable.ic_check_circle_black_24dp);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View button) {
                            Suggestion newSuggestion = new Suggestion();
                            newSuggestion.setBody(suggestionText.getText().toString());
                            relayer.editSuggestion(newSuggestion, position);
                            suggestionText.clearFocus();
                            button.setOnClickListener(resetDoneButton);
                        }
                    });

                }
            });

        }
    }

    interface SuggestionEditRelayer {
        public void removeSuggestion(int position);
        public void editSuggestion(Suggestion suggestion, int position);
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
    }
}
