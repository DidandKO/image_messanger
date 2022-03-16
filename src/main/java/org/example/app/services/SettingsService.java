package org.example.app.services;

import org.example.web.dto.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.awt.*;

@Service
public class SettingsService {

    public SettingsService() {}

    public void changePersonalData(@NotNull String email, String password, String name, String lastName, @NotNull User user) {
        if (!user.getEmail().equals(email.trim())) {
            user.setEmail(email.trim());
        }
        if (!user.getPassword().equals(password.trim())) {
            user.setPassword(password.trim());
        }
        if (!user.getName().equals(name.trim())) {
            user.setName(name.trim());
        }
        if (!user.getLastName().equals(lastName.trim())) {
            user.setLastName(lastName.trim());
        }
    }
}