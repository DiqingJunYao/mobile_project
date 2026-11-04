package com.example.mobile_project;

public class Message {
    private String sender;
    private String content;
    private long timestamp;
    private boolean isSent;
    private String messageStatus;



    public Message(boolean isSent, long timestamp, String content, String sender, String messageStatus) {
        this.isSent = isSent;
        this.timestamp = timestamp;
        this.content = content;
        this.sender = sender;
        this.messageStatus = messageStatus;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isSent() {
        return isSent;
    }

    public String getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(String messageStatus) {
        this.messageStatus = messageStatus;
    }
}
