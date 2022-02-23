package org.example.web.controllers;

import org.example.app.services.SettingsService;
import org.example.web.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.awt.*;

@Controller
@RequestMapping(value = "/settings")
public class SettingsController {

    private final SettingsService settingsService;

    @Autowired
    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @GetMapping()
    public String getSettingsPage() {
        return "settings";
    }

    @PostMapping("/change_personal_data")
    public String changePersonalData(@ModelAttribute("email") String email, @ModelAttribute("password") String password,
                                     @ModelAttribute("name") String name, @ModelAttribute("lastName") String lastName,
                                     @SessionAttribute("login_user") User user) {
        settingsService.changePersonalData(email, password, name, lastName, user);
        return "redirect:/settings";
    }

    @PostMapping("/change_avatar")
    public String changeAvatar(@SessionAttribute("login_user") User user, @ModelAttribute("avatar") Image image) {
        settingsService.changeAvatar(user, image);
        return "redirect:/settings";
    }
}
