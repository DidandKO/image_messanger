package org.example.web.dto;

import java.awt.*;
import java.sql.Timestamp;

public class Message {

    private int message_id;
    private int dialog_id;
    private Timestamp timestamp;
    private String body;
    private Image imageBody;
    private byte[] byte_code;
    private String imageSrc;
    private User sender;

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public int getDialog_id() {
        return dialog_id;
    }

    public void setDialog_id(int dialog_id) {
        this.dialog_id = dialog_id;
    }

    public byte[] getByte_code() {
        return byte_code;
    }

    public void setByte_code(byte[] byte_code) {
        this.byte_code = byte_code;
    }

    public String getImageSrc() {
        return imageSrc;
    }

    public void setImageSrc(String imageSrc) {
        this.imageSrc = imageSrc;
    }

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
        return this.body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Message{" +
                "sender=" + sender +
                ", message_id=" + message_id +
                ", byte_code=" + byte_code +
                ", timestamp=" + timestamp +
                ", imageSrc=" + imageSrc +
                ", body='" + body + '\'' +
                '}';
    }
}
