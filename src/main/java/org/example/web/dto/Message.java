package org.example.web.dto;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.sql.Timestamp;
import java.util.Comparator;

public class Message implements Comparator<Message> {

    private int message_id;
    private Timestamp timestamp;
    private String body;
    private Image imageBody;

    public Image getImageBody() {
        return imageBody;
    }

    public void setImageBody(Image imageBody) {
        this.imageBody = imageBody;
    }

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

    @Override
    public int compare(@NotNull Message m1, @NotNull Message m2) {
        if (m1.getTimestamp().before(m2.getTimestamp())) {
            return -1;
        } else if (m1.getTimestamp().after(m2.getTimestamp())) {
            return 1;
        } else {
            return 0;
        }
    }
}
