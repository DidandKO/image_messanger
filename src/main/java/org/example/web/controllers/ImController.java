package org.example.web.controllers;

import org.example.app.services.ImService;
import org.example.web.dto.Dialog;
import org.example.web.dto.Message;
import org.example.web.dto.User;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@Controller
@RequestMapping(value = "/im")
public class ImController extends TextWebSocketHandler {

    private ImService imService;
    Logger logger = Logger.getLogger("logger");

    @Autowired
    public ImController(ImService imService) {
        this.imService = imService;
    }

    public ImController() {}

    @NotNull
    @Contract(pure = true)
    @GetMapping()
    private String getImPage(@NotNull Model model,
                             @SessionAttribute("login_user") User user,
                             HttpServletRequest request) {
        if (user == null) {
            return "login_page";
        }
        model.addAttribute("user", user);
        logger.info("session user: " + user);
        model.addAttribute("dialog_list", user.getDialogs());
        logger.info("dialog_list: " + user.getDialogs());
        model.addAttribute("new_dialog", new Dialog());
        Dialog currentDialog = (Dialog) request.getSession().getAttribute("current_dialog");
        if (currentDialog != null) {
            model.addAttribute("dialog", currentDialog);
            logger.info("dialog: " + currentDialog);
            model.addAttribute("messageList", currentDialog.getMessageList());
            logger.info("messageList: " + currentDialog.getMessageList());
        }
        return "im";
    }

    @PostMapping("/create_dialog")
    public String createDialog(@NotNull Model model,
                               @SessionAttribute("login_user") User user,
                               @NotNull @ModelAttribute("new_dialog") Dialog new_dialog)
    {
        logger.info("session user: " + user);
        if (user == null) {
            return "login_page";
        }
        User partner = imService.getUserByLogin(new_dialog.getPartner().getEmail());
        Dialog dialog = imService.createDialog(user, partner, new_dialog.getSubject());
        user.addDialog(dialog);
        dialog.getPartner().addDialog(dialog);
        model.addAttribute("dialog", dialog);
        logger.info("new dialog: " + dialog);
        return "redirect:/im";
    }

    @GetMapping("/{dialog_id}")
    public String openDialog(@PathVariable("dialog_id") String dialog_id,
                             @NotNull Model model,
                             @NotNull HttpServletRequest request,
                             @SessionAttribute("login_user") User user) {
        if (user == null) {
            return "login_page";
        }
        Dialog dialog = imService.getCurrentDialogById(Integer.parseInt(dialog_id), user);
        assert dialog != null;
        dialog.setNewMessagesCount(0);
        List<Message> messageList = imService.getDialogMessageList(Integer.parseInt(dialog_id), user);
        model.addAttribute("user", user);
        model.addAttribute("dialog", dialog);
        model.addAttribute("messageList", messageList);
        logger.info("opened dialog: " + dialog);
        logger.info("message list: " + messageList);
        model.addAttribute("temp_message", new Message());
        return "chat_field :: chat_field_fragment";
    }

    @GetMapping("/send_message/{dialog_id}/{message_body}")
    @ResponseStatus(value = HttpStatus.OK)
    public String sendMessage(@NotNull Model model,
                            @NotNull HttpServletRequest request,
                            @PathVariable("dialog_id") String dialog_id,
                            @PathVariable("message_body") String messageBody,
                            @SessionAttribute("login_user") User user) throws IOException
    {
        if (user == null) {
            return "login_page";
        }
        Dialog dialogToUpdate = imService.getCurrentDialogById(Integer.parseInt(dialog_id), user);
        logger.info("dialog to update: " + dialogToUpdate);
        assert dialogToUpdate != null;
        Message message = imService.createMessage(messageBody, dialogToUpdate, user);
        logger.info("message body: " + messageBody);
        logger.info("message to send: " + message);
        dialogToUpdate.addMessage(message);
        logger.info("user dialogs: " + user.getDialogs());
        logger.info("partner dialogs: " + dialogToUpdate.getPartner().getDialogs());
        model.addAttribute("dialog", dialogToUpdate);
        request.getSession().setAttribute("current_dialog", dialogToUpdate);
//        return "chat_field :: chat_field_fragment";
//        return "redirect:/im/" + dialog_id;
        return "redirect:/im";
    }

    @PostMapping("/delete_dialog")
    public String deleteDialog(@NotNull Model model, @SessionAttribute("login_user") User user) {
        if (user == null) {
            return "login_page";
        }
        Dialog dialogToDelete = imService.getCurrentDialogById(Integer.parseInt(getDialogId(model)), user);
        user.deleteDialog(dialogToDelete);
        return "redirect:/im";
    }

    @PostMapping("/delete_message")
    public String deleteMessage(@NotNull Model model,
                                @ModelAttribute("messageToDelete") Message messageToDelete,
                                @SessionAttribute("login_user") User user)
    {
        if (user == null) {
            return "login_page";
        }
        Dialog newDialog = imService.getCurrentDialogById(Integer.parseInt(getDialogId(model)), user);
        assert newDialog != null;
        newDialog.deleteMessage(messageToDelete);
        return "redirect:/im/" + getDialogId(model);
    }

    @NotNull
    private static String getDialogId(@NotNull Model model) {
        Dialog dialog = (Dialog) model.getAttribute("dialog");
        assert dialog != null;
        return String.valueOf(dialog.getDialog_id());
    }
}