package com.example.missitchat;

class Conversation implements Comparable<Conversation> {

    private User user;
    private Message latestMessage;
    private boolean read;

    public Conversation(User user, Message latestMessage, boolean read) {
        this.user = user;
        this.latestMessage = latestMessage;
        this.read = read;
    }

    public User getUser() {
        return user;
    }

    public Message getLatestMessage() {
        return latestMessage;
    }

    public boolean isRead() {
        return read;
    }

    public void setLatestMessage(Message latestMessage) {
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

    @Override
    public int compareTo(Conversation o) {
        return (int) (o.getLatestMessage().getTimestamp() - this.getLatestMessage().getTimestamp());
    }
}
