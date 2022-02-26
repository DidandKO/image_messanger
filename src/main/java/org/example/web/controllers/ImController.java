package org.example.web.controllers;

import org.example.app.services.ImService;
import org.example.web.dto.Dialog;
import org.example.web.dto.Message;
import org.example.web.dto.User;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequestMapping(value = "/im")
public class ImController {

    private final ImService imService;

    @Autowired
    public ImController(ImService imService) {
        this.imService = imService;
    }

    @NotNull
    @Contract(pure = true)
    @GetMapping()
    private String getImPage(@NotNull Model model, @SessionAttribute("login_user") User user) {
        model.addAttribute("user", user);
        model.addAttribute("dialog_list", user.getDialogs());
        model.addAttribute("new_dialog", new Dialog());
        return "im";
    }

    @PostMapping("/create_dialog")
    public String createDialog(@NotNull Model model,
                               @SessionAttribute("login_user") User user,
                               @NotNull @ModelAttribute("new_dialog") Dialog new_dialog)
    {
        User partner = imService.getUserByLogin(new_dialog.getPartner().getEmail());
        Dialog dialog = imService.createDialog(user, partner, new_dialog.getSubject());
        user.addDialog(dialog);
        model.addAttribute("dialog", dialog);
        return "im";
//        return "redirect:/im/" + dialog.getDialog_id();
    }

//    @PostMapping("/open_dialog")
//    public String openDialog(@NotNull Model model) {
//        return "redirect:/im/" + getDialogId(model);
//    }

    @GetMapping("/{dialog_id}")
    public String dialog(@PathVariable("dialog_id") String dialog_id, @NotNull Model model, @SessionAttribute("login_user") User user) {
        model.addAttribute("user", user);
        model.addAttribute("dialog_list", user.getDialogs());
        model.addAttribute("new_dialog", new Dialog());
        Dialog dialog = imService.getCurrentDialogById(Integer.parseInt(dialog_id), user);
        assert dialog != null;
        dialog.setNewMessagesCount(0);
        model.addAttribute("message_list", imService.getDialogMessageList(Integer.parseInt(dialog_id), user));
        model.addAttribute("dialog", dialog);
        return "chat_field :: chat_field_fragment";
    }

    @PostMapping("/delete_dialog")
    public String deleteDialog(@NotNull Model model, @SessionAttribute("login_user") User user) {
        Dialog dialogToDelete = imService.getCurrentDialogById(Integer.parseInt(getDialogId(model)), user);
        user.deleteDialog(dialogToDelete);
        return "redirect:/im";
    }

    @PostMapping("/send_message")
    public String sendMessage(@NotNull Model model,
                              @ModelAttribute("messageBody") String messageBody,
                              @SessionAttribute("login_user") User user) throws IOException
    {
        Dialog dialogToUpdate = imService.getCurrentDialogById(Integer.parseInt(getDialogId(model)), user);
        assert dialogToUpdate != null;
        Message message = imService.createMessage(messageBody, dialogToUpdate);
        Dialog newDialog = dialogToUpdate;
        newDialog.addMessage(message);
        user.updateDialog(dialogToUpdate, newDialog);
        return "redirect:/im/" + newDialog.getDialog_id();
    }

    @PostMapping("/delete_message")
    public String deleteMessage(@NotNull Model model,
                                @ModelAttribute("messageToDelete") Message messageToDelete,
                                @SessionAttribute("login_user") User user)
    {
        Dialog dialogToUpdate = imService.getCurrentDialogById(Integer.parseInt(getDialogId(model)), user);
        Dialog newDialog = dialogToUpdate;
        assert newDialog != null;
        newDialog.deleteMessage(messageToDelete);
        user.updateDialog(dialogToUpdate, newDialog);
        return "redirect:/im/" + getDialogId(model);
    }

    @PostMapping("/open_settings")
    public String openSettings() {
        return "redirect:/settings";
    }

    @NotNull
    private static String getDialogId(@NotNull Model model) {
        Dialog dialog = (Dialog) model.getAttribute("dialog");
        assert dialog != null;
        return String.valueOf(dialog.getDialog_id());
    }
}