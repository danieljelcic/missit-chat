package com.example.missitchat;

public class Message {
    private String messageBody;
    private MemberData memberData;
    private boolean received;

    public Message(String messageBody, MemberData memberData, boolean received) {
        this.messageBody = messageBody;
        this.memberData = memberData;
        this.received = received;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public MemberData getMemberData() {
        return memberData;
    }

    public boolean isReceived() {
        return received;
    }
}
