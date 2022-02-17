package org.example.web.dto;

import java.sql.Timestamp;

public class Message {

    private int message_id;
    private Timestamp timestamp;
    private String body;

    public int getMessage_id() {
        return message_id;
    }

    public void setMessage_id(int message_id) {
        this.message_id = message_id;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Message{" +
                "message_id=" + message_id +
                ", timestamp=" + timestamp +
                ", body='" + body + '\'' +
                '}';
    }
}
