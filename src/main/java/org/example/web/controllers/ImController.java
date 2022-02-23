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
        return "im";
    }

    @PostMapping("/create_dialog")
    public String createDialog(@NotNull Model model, @SessionAttribute("login_user") User user) {
        Dialog dialog = imService.createDialog(user);
        user.addDialog(dialog);
        model.addAttribute("dialog", dialog);
        return "redirect:/im/" + dialog.getDialog_id();
    }

    @PostMapping("/open_dialog")
    public String openDialog(@NotNull Model model) {
        return "redirect:/im/" + getDialogId(model);
    }

    @GetMapping("/{dialog_id}")
    public void dialog(@PathVariable int dialog_id, @NotNull Model model, @SessionAttribute("login_user") User user) {
        Dialog dialog = imService.getCurrentDialogById(dialog_id, user);
        assert dialog != null;
        dialog.setNewMessagesCount(0);
        model.addAttribute("message_list", imService.getDialogMessageList(dialog_id, user));
        model.addAttribute("dialog", dialog);
        // js onClick() - open dialog fragment
    }

    @PostMapping("/delete_dialog")
    public String deleteDialog(@NotNull Model model, @SessionAttribute("login_user") User user) {
        Dialog dialogToDelete = imService.getCurrentDialogById(getDialogId(model), user);
        user.deleteDialog(dialogToDelete);
        return "redirect:/im";
    }

    @PostMapping("/send_message")
    public String sendMessage(@NotNull Model model,
                              @ModelAttribute("messageBody") String messageBody,
                              @SessionAttribute("login_user") User user) {
        Dialog dialogToUpdate = imService.getCurrentDialogById(getDialogId(model), user);
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
                                @SessionAttribute("login_user") User user) {
        Dialog dialogToUpdate = imService.getCurrentDialogById(getDialogId(model), user);
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

    private static int getDialogId(@NotNull Model model) {
        return (model.getAttribute("dialog_id") != null ? (int) model.getAttribute("dialog_id") : 0);
    }
}