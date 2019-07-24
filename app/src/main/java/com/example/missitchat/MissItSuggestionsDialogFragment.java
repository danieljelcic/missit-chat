package com.example.missitchat;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class MissItSuggestionsDialogFragment extends DialogFragment {

    public static final String TAG = "SuggestionsDialog: ";

    private TextView currMessageView;
    private ImageButton closeButton;
    private ImageButton addSuggestionButton;
    private ImageButton sendButton;
    private RecyclerView suggestionsView;

    private String currMessage;
    private SuggestionEditViewAdapter suggestionsViewAdapter;
    private SuggestionEditListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_missit_suggestions, container, false);

        // widgets
        currMessageView = view.findViewById(R.id.currMessageBody);
        closeButton = view.findViewById(R.id.closeButton);
        addSuggestionButton = view.findViewById(R.id.addSuggestionButton);
        sendButton = view.findViewById(R.id.messageSendBttn);
        suggestionsView = view.findViewById(R.id.suggestionEditView);

        // retrieve and display current message
        currMessage = getArguments().getString("currMessage");
        Log.d(TAG, "onCreateView: current message is " + currMessage);
        currMessageView.setText(currMessage);

        // set up recycler view
        suggestionsViewAdapter = new SuggestionEditViewAdapter(getContext(), new SuggestionEditViewAdapter.SuggestionEditListener() {
            @Override
            public void OnRemoveSuggestionButtonClick(int position) {
                suggestionsViewAdapter.removeSuggestion(position);
                suggestionsViewAdapter.notifyDataSetChanged();
                Log.d(TAG, "removeSuggestion: at [" + position + "]");
            }

            @Override
            public void OnEditSuggestion(SuggestionEditViewAdapter.Suggestion suggestion, int position) {
                suggestionsViewAdapter.editSuggestion(suggestion, position);
//                suggestionsViewAdapter.notifyDataSetChanged();
                Log.d(TAG, "editSuggestion: at [" + position + "] -> " + suggestion.getBody());
            }
        });
        suggestionsView.setAdapter(suggestionsViewAdapter);
        suggestionsView.setLayoutManager(new LinearLayoutManager(getContext()));

        // add passed in suggestions, if available
        for (int i = 0; i < 4; i++) {
            String suggestion = getArguments().getString("suggestion_" + (i + 1));
            if (suggestion != null) {
                suggestionsViewAdapter.addSuggestion(new SuggestionEditViewAdapter.Suggestion(suggestion));
                suggestionsViewAdapter.notifyDataSetChanged();
            }
        }

        // set up click listeners for buttons
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        addSuggestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.requestFocus();
                suggestionsViewAdapter.addSuggestion(new SuggestionEditViewAdapter.Suggestion());

                // this might be a problem
                suggestionsViewAdapter.notifyDataSetChanged();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
                listener.OnSend(suggestionsViewAdapter.getSuggestionTexts());
            }
        });


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            this.listener = (SuggestionEditListener) context;
            listener.OnClose(new ArrayList<String>());
        } catch (ClassCastException e) {
            Log.d(TAG, "onAttach: " + e.getMessage());
        }
    }

    interface SuggestionEditListener {
        void OnClose(ArrayList<String> suggestionTexts);
        void OnSend(ArrayList<String> suggestionTexts);
    }


    @Override
    public void onResume() {
        super.onResume();

        // set proper size for the dialog window
        int width = getContext().getResources().getDisplayMetrics().widthPixels;
//        int height = getContext().getResources().getDisplayMetrics().heightPixels;
        int height = getDialog().getWindow().getAttributes().height;
        getDialog().getWindow().setLayout(width, height);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        View currFocus = getActivity().getCurrentFocus();
        if (currFocus != null) currFocus.clearFocus();

        listener.OnClose(suggestionsViewAdapter.getSuggestionTexts());
    }
}