package org.example.web.controllers;

import org.example.app.services.ImService;
import org.example.app.services.SessionAttributes;
import org.example.web.dto.User;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

@Controller
@RequestMapping(value = "/im")
public class ImController {

    private ImService imService;

    @Autowired
    public ImController(ImService imService) {
        this.imService = imService;
    }

    @NotNull
    @Contract(pure = true)
    @GetMapping()
    private String getIm(@NotNull HttpServletRequest request, @NotNull Model model) {
        User user = (User) SessionAttributes.getSessionAttribute(request,"login_user");
        model.addAttribute("user", user);
        model.addAttribute("dialog_list", user.getDialogs());
        return "im";
    }

    @PostMapping("/open_dialog")
    public String getDialog(@NotNull Model model) {
        return "redirect:/im/" + getDialogId(model);
    }

    @GetMapping("/{dialog_id}")
    public void dialog(@PathVariable int dialog_id, @NotNull HttpServletRequest request, @NotNull Model model) {
        User user = (User) SessionAttributes.getSessionAttribute(request,"login_user");
        model.addAttribute("message_list", imService.messageList(dialog_id, user));
        model.addAttribute("dialog", imService.getCurrentDialog(dialog_id, user));
        // js onClick() - open dialog fragment
    }

    @PostMapping("")
    public String sendMessage(@NotNull Model model) {
        String newMessage = (String) model.getAttribute("newMessage");
        byte[] imageData = imService.convertTextIntoImage(newMessage);
        if (imageData == null) {
            return "redirect:/im/" + getDialogId(model);
        }

        ImageIcon imageIcon = new ImageIcon(imageData);
        Image image = imageIcon.getImage();
        model.addAttribute("image", image);
        return "redirect:/im/" + getDialogId(model);
    }

    private static int getDialogId(@NotNull Model model) {
        return (model.getAttribute("dialog_id") != null ? (int) model.getAttribute("dialog_id") : 0);
    }
}
