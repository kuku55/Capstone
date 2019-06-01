package com.capstone.message;


public class Message {
    private String message_id;
    private String user_id;
    private String contact_id;
    private String message;
    private String date_sent;
    private String subject;
    private String isRead;

    public Message(){

    }

    public Message(String message_id, String user_id, String contact_id, String message, String date_sent, String subject, String isRead) {
        this.setMessage_id(message_id);
        this.setUser_id(user_id);
        this.setContact_id(contact_id);
        this.setMessage(message);
        this.setDate_sent(date_sent);
        this.setSubject(subject);
        this.setIsRead(isRead);
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getContact_id() {
        return contact_id;
    }

    public void setContact_id(String contact_id) {
        this.contact_id = contact_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate_sent() {
        return date_sent;
    }

    public void setDate_sent(String date_sent) {
        this.date_sent = date_sent;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getIsRead() { return isRead; }

    public void setIsRead(String isRead) { this.isRead = isRead;}
}
