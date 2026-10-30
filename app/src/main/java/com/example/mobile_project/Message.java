package com.example.mobile_project;

public class Message {
    private String sender;
    private String content;
    private long timestamp;
    private boolean isSent;

    public Message(boolean isSent, long timestamp, String content, String sender) {
        this.isSent = isSent;
        this.timestamp = timestamp;
        this.content = content;
        this.sender = sender;
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
}
