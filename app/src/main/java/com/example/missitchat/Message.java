package com.example.missitchat;

public class Message {
    private String messageBody;
    private User user;
    private Long timestamp;
    private boolean received;
    private MissItSuggestions missIt;

    public Message(String messageBody, User user, boolean received, Long timestamp, MissItSuggestions suggestions) {
        this.messageBody = messageBody;
        this.user = user;
        this.received = received;
        this.timestamp = timestamp;
        this.missIt = suggestions;
    }

    public Message(String messageBody, User user, boolean received, Long timestamp) {
        this(messageBody, user, received, timestamp, null);
    }

    public String getMessageBody() {
        return messageBody;
    }

    public User getUser() {
        return user;
    }

    public MissItSuggestions getMissItSuggestions() {
        return missIt;
    }

    public boolean isReceived() {
        return received;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageBody='" + messageBody + '\'' +
                ", user=" + user.toString() +
                ", received=" + received +
                '}';
    }
}
