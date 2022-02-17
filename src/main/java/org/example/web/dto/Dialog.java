package org.example.web.dto;

import java.util.List;

public class Dialog {

    private int dialog_id;
    private User dialogOwner;
    private User partner;
    protected List<Message> messageList;
    private String subject;

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
                ", numberOfMessages=" + messageList.size() +
                '}';
    }
}
