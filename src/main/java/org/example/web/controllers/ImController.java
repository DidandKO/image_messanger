package org.example.web.controllers;

import org.example.app.services.ImService;
import org.example.app.services.SessionAttributes;
import org.example.web.dto.Dialog;
import org.example.web.dto.Message;
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

    @PostMapping("/create_dialog")
    public String createDialog(@NotNull Model model, @NotNull HttpServletRequest request) {
        User user = (User) SessionAttributes.getSessionAttribute(request,"login_user");
        Dialog dialog = imService.createDialog(user);
        user.addDialog(dialog);
        request.getSession().setAttribute("login_user", user);
        model.addAttribute("dialog", dialog);
        return "redirect:/im/" + dialog.getDialog_id();
    }

    @PostMapping("/open_dialog")
    public String openDialog(@NotNull Model model) {
        return "redirect:/im/" + getDialogId(model);
    }

    @GetMapping("/{dialog_id}")
    public void dialog(@PathVariable int dialog_id, @NotNull HttpServletRequest request, @NotNull Model model) {
        User user = (User) SessionAttributes.getSessionAttribute(request,"login_user");
        model.addAttribute("message_list", imService.messageList(dialog_id, user));
        model.addAttribute("dialog", imService.getCurrentDialog(dialog_id, user));
        // js onClick() - open dialog fragment
    }

    @PostMapping("/delete_dialog")
    public String deleteDialog(@NotNull Model model, @NotNull HttpServletRequest request) {
        User user = (User) SessionAttributes.getSessionAttribute(request,"login_user");
        Dialog dialogToDelete = imService.getCurrentDialog(getDialogId(model), user);
        user.deleteDialog(dialogToDelete);
        return "redirect:/im";
    }

    @PostMapping("/send_message")
    public String sendMessage(@NotNull Model model, @NotNull HttpServletRequest request) {
        User user = (User) SessionAttributes.getSessionAttribute(request,"login_user");
        String messageBody = (String) model.getAttribute("messageBody");
        Message message = imService.createMessage(messageBody);

        Dialog dialogToUpdate = imService.getCurrentDialog(getDialogId(model), user);
        Dialog newDialog = dialogToUpdate;
        assert newDialog != null;
        newDialog.addMessage(message);
        user.updateDialog(dialogToUpdate, newDialog);
        request.getSession().setAttribute("login_user", user);
        return "redirect:/im/" + newDialog.getDialog_id();
    }

    @PostMapping("/delete_message")
    public String deleteMessage(@NotNull Model model, @NotNull HttpServletRequest request) {
        User user = (User) SessionAttributes.getSessionAttribute(request,"login_user");
        Message messageToDelete = (Message) model.getAttribute("messageToDelete");

        Dialog dialogToUpdate = imService.getCurrentDialog(getDialogId(model), user);
        Dialog newDialog = dialogToUpdate;
        assert newDialog != null;
        newDialog.deleteMessage(messageToDelete);
        user.updateDialog(dialogToUpdate, newDialog);
        request.getSession().setAttribute("login_user", user);
        return "redirect:/im/" + getDialogId(model);
    }

    private static int getDialogId(@NotNull Model model) {
        return (model.getAttribute("dialog_id") != null ? (int) model.getAttribute("dialog_id") : 0);
    }
}