package org.example.web.dto;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class User {

    private int user_id;
    private String email;
    private String password;
    private String name;
    private String lastName;
    private List<Dialog> dialogs;
    private int offlineTimeInMinutes;
    private Image avatar;

    private void isEmpty() {
        if(dialogs == null) {
            dialogs = new ArrayList<>();
        }
    }

    public Image getAvatar() {
        return avatar;
    }

    public void setAvatar(Image avatar) {
        this.avatar = avatar;
    }

    public void addDialog(Dialog dialog) {
        isEmpty();
        dialogs.add(dialog);
    }

    public void deleteDialog(Dialog dialogToDelete) {
        isEmpty();
        dialogs.remove(dialogToDelete);
    }

    public int getOfflineTimeInMinutes() {
        return offlineTimeInMinutes;
    }

    public void setOfflineTimeInMinutes(int offlineTimeInMinutes) {
        this.offlineTimeInMinutes = offlineTimeInMinutes;
    }

    public List<Dialog> getDialogs() {
        isEmpty();
        return dialogs;
    }

    public void setDialogs(List<Dialog> dialogs) {
        this.dialogs = dialogs;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "User{" +
                "user_id=" + user_id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", offlineTimeInMinutes=" + offlineTimeInMinutes +
                '}';
    }
}
