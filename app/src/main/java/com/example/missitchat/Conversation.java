package com.example.missitchat;

class Conversation {

    private MemberData user;
    private String latestMessage;
    private boolean read;

    public Conversation(MemberData user, String latestMessage, boolean read) {
        this.user = user;
        this.latestMessage = latestMessage;
        this.read = read;
    }

    public MemberData getUser() {
        return user;
    }

    public String getLatestMessage() {
        return latestMessage;
    }

    public boolean isRead() {
        return read;
    }

    public void setLatestMessage(String latestMessage) {
        this.latestMessage = latestMessage;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "user=" + user +
                ", latestMessage='" + latestMessage + '\'' +
                ", read=" + read +
                '}';
    }
}
