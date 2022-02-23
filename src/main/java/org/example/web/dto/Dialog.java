package org.example.web.dto;

import java.util.ArrayList;
import java.util.List;

public class Dialog {

    private int dialog_id;
    private User dialogOwner;
    private User partner;
    protected List<Message> messageList;
    private String subject;
    private int newMessagesCount;

    public Dialog() {
        this.newMessagesCount = 0;
    }

    public int getNewMessagesCount() {
        return newMessagesCount;
    }

    public void setNewMessagesCount(int newMessagesCount) {
        this.newMessagesCount = newMessagesCount;
    }

    public void incNewMessagesCount() {
        this.newMessagesCount++;
    }

    private void isEmpty() {
        if(messageList == null) {
            messageList = new ArrayList<>();
        }
    }

    public void addMessage(Message message) {
        isEmpty();
        messageList.add(message);
    }

    public void deleteMessage(Message messageToDelete) {
        isEmpty();
        messageList.remove(messageToDelete);
    }

    public int getDialog_id() {
        return dialog_id;
    }

    public void setDialog_id(int dialog_id) {
        this.dialog_id = dialog_id;
    }

    public User getDialogOwner() {
        return dialogOwner;
    }

    public void setDialogOwner(User dialogOwner) {
        this.dialogOwner = dialogOwner;
    }

    public User getPartner() {
        return partner;
    }

    public void setPartner(User partner) {
        this.partner = partner;
    }

    public List<Message> getMessageList() {
        isEmpty();
        return messageList;
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        return "Dialog{" +
                "dialog_id=" + dialog_id +
                ", dialogOwner=" + dialogOwner +
                ", partner=" + partner +
                ", subject='" + subject + '\'' +
                ", numberOfMessages=" + getMessageList().size() +
                '}';
    }
}
