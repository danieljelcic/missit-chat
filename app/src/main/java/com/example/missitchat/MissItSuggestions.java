package com.example.missitchat;

import java.util.ArrayList;

public class MissItSuggestions {

    public final static int RESPONSE_NO_RESPONSE = -1;
    public final static int STATUS_RESPONSE_SUCCESS = 0;
    public final static int STATUS_RESPONSE_TRANSMITTING = 1;
    public final static int STATUS_RESPONSE_NOT_ATTEMPTED = 2;
    public final static int STATUS_RESPONSE_FAILED = 3;
    public final static int STATUS_RESPONSE_ESTABLISHING = 4;
    public final static int TIMESTAMP_NO_TIMESTAMP = 5;

    private ArrayList<String> suggestions;
    private int statusCode;
    private int responseCode;
    private long responseTimestamp;

    public MissItSuggestions(ArrayList<String> suggestions) {
        this.suggestions = suggestions;
        statusCode = STATUS_RESPONSE_NOT_ATTEMPTED;
        responseCode = RESPONSE_NO_RESPONSE;
        responseTimestamp = TIMESTAMP_NO_TIMESTAMP;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public ArrayList<String> getSuggestions() {
        return suggestions;
    }

    public String getSuggestionAt(int position) {

        if (position < 4 && position < suggestions.size()) {
            return suggestions.get(position);
        } else {
            return null;
        }
    }

    public long getResponseTimestamp() {
        return responseTimestamp;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public void setResponseTimestamp(long responseTimestamp) {
        this.responseTimestamp = responseTimestamp;
    }

    @Override
    public String toString() {
        return suggestions.toString();
    }
}
