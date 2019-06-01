package com.capstone.message;

public class Inbox {
    private String sender;
    private Message message;

    public Inbox()
    {

    }

    public Inbox(String sender, Message message) {
        this.setSender(sender);
        this.setMessage(message);

    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
