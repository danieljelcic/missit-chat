package com.example.missitchat;

public class Message {
    private String messageBody;
    private User user;
    private Long timestamp;
    private boolean received;
    private boolean isMissit;

    public Message(String messageBody, User user, boolean received, Long timestamp) {
        this.messageBody = messageBody;
        this.user = user;
        this.received = received;
        this.timestamp = timestamp;
        this.isMissit = false;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public User getUser() {
        return user;
    }

    public boolean isMissit() {
        return isMissit;
    }

    public boolean isReceived() {
        return received;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setMissit(boolean missit) {
        isMissit = missit;
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
