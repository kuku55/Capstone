package com.capstone.message;

public class MessageContact {
    private String message_id;
    private String uid;
    private String subject;
    private String message;
    private String receiver;
    private String datetimeSent;

    public MessageContact(){

    }

    public MessageContact(String message_id, String uid, String subject, String message, String receiver, String datetimeSent) {
        this.message_id = message_id;
        this.uid = uid;
        this.subject = subject;
        this.message = message;
        this.receiver = receiver;
        this.datetimeSent = datetimeSent;
    }

    public String getMessage_id() {
        return message_id;
    }

    public String getUid() {
        return uid;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessage() {
        return message;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getDatetimeSent() {
        return datetimeSent;
    }
}
