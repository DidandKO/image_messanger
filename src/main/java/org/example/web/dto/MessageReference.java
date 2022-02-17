package org.example.web.dto;

public class MessageReference {

    private int mr_id;
    private Message message;
    private User to_;
    private User from_;

    public int getMr_id() {
        return mr_id;
    }

    public void setMr_id(int mr_id) {
        this.mr_id = mr_id;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public User getTo_() {
        return to_;
    }

    public void setTo_(User to_) {
        this.to_ = to_;
    }

    public User getFrom_() {
        return from_;
    }

    public void setFrom_(User from_) {
        this.from_ = from_;
    }

    @Override
    public String toString() {
        return "MessageReference{" +
                "mr_id=" + mr_id +
                ", message_id=" + message.getMessage_id() +
                ", to " + to_ +
                ", from " + from_ +
                '}';
    }
}
