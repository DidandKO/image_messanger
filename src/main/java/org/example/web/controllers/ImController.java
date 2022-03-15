package org.example.web.controllers;

import org.example.app.exceptions.MyNoSuchUserException;
import org.example.app.exceptions.MyNullMessageException;
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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@Controller
@RequestMapping(value = "/im")
public class ImController extends TextWebSocketHandler {

    final String TOMCAT_DIR = System.getProperty("catalina.home") + File.separator + "drawings" + File.separator;
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
    private String getImPage(@NotNull Model model, @NotNull HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("login_user");
        if (user == null) {
            return "redirect:/main";
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
                               @NotNull HttpServletRequest request,
                               @NotNull @ModelAttribute("new_dialog") Dialog new_dialog) throws MyNoSuchUserException {
        User user = (User) request.getSession().getAttribute("login_user");
        logger.info("session user: " + user);
        if (user == null) {
            return "redirect:/main";
        }
        try {
            User partner = imService.getUserByLogin(new_dialog.getPartner().getEmail());
            Dialog dialog = imService.createDialog(user, partner, new_dialog.getSubject());
            user.addDialog(dialog);
            dialog.getPartner().addDialog(dialog);
            model.addAttribute("dialog", dialog);
            logger.info("new dialog: " + dialog);
        } catch (Exception exception) {
            throw new MyNoSuchUserException("Пользователь с таким именем не зарегистрирован");
        }
        return "redirect:/im";
    }

    @GetMapping("/{dialog_id}")
    public ModelAndView openDialog(@PathVariable("dialog_id") String dialog_id,
                             @NotNull Model model,
                             @NotNull HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("chat_field :: chat_field_fragment");
        User user = (User) request.getSession().getAttribute("login_user");
        if (user == null) {
            return new ModelAndView("main");
        }
        Dialog dialog = imService.getCurrentDialogById(Integer.parseInt(dialog_id), user);
        assert dialog != null;
        dialog.setNewMessagesCount(0);
        List<Message> messageList = imService.getDialogMessageList(Integer.parseInt(dialog_id), user);

        modelAndView.addObject("user", user);
        modelAndView.addObject("dialog", dialog);
        modelAndView.addObject("messageList", messageList);
        modelAndView.addObject("tomcatDir", TOMCAT_DIR);

        logger.info("opened dialog: " + dialog);
        logger.info("message list: " + messageList);
        return modelAndView;
    }

    @GetMapping("/send_message/{dialog_id}/{message_body}")
    @ResponseStatus(value = HttpStatus.OK)
    public ModelAndView sendMessage(@NotNull Model model,
                                    @NotNull HttpServletRequest request,
                                    @PathVariable("dialog_id") String dialog_id,
                                    @PathVariable("message_body") String messageBody) throws IOException {
        User user = (User) request.getSession().getAttribute("login_user");
        ModelAndView modelAndView = new ModelAndView("chat_field :: chat_field_fragment");
        if (user == null) {
            return new ModelAndView("main");
        }
        Dialog dialogToUpdate = imService.getCurrentDialogById(Integer.parseInt(dialog_id), user);
        assert dialogToUpdate != null;
        Message message = imService.createMessage(messageBody, dialogToUpdate, user);
        dialogToUpdate.addMessage(message);

        modelAndView.addObject("dialog", dialogToUpdate);
        modelAndView.addObject("messageList", dialogToUpdate.getMessageList());
        modelAndView.addObject("tomcatDir", TOMCAT_DIR);

        request.getSession().setAttribute("current_dialog", dialogToUpdate);
        return modelAndView;
    }

    @RequestMapping("/send_message/{dialog_id}")
    public void nullMessage(@PathVariable("dialog_id") String dialog_id) throws MyNullMessageException {
        throw new MyNullMessageException("Сообщение не может быть пустым");
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
                                @SessionAttribute("login_user") User user) {
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

    @ExceptionHandler(MyNoSuchUserException.class)
    public String handleError(@NotNull Model model, @NotNull MyNoSuchUserException exception) {
        model.addAttribute("errorMessage", exception.getMessage());
        return "errors/400";
    }

    @ExceptionHandler(MyNullMessageException.class)
    public String handleError(@NotNull Model model, @NotNull MyNullMessageException exception) {
        model.addAttribute("errorMessage", exception.getMessage());
        return "errors/400";
    }
}