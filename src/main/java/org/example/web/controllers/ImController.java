package org.example.web.controllers;

import org.example.app.exceptions.MyNoSuchUserException;
import org.example.app.exceptions.MyNullMessageException;
import org.example.app.exceptions.MyUploadException;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

@Controller
@RequestMapping(value = "/im")
public class ImController extends TextWebSocketHandler {

    private static final Object CHROME_WEB_SERVER_URL = "http://127.0.0.1:8887/";
    final String CATALINA_HOME = System.getProperty("catalina.home") + File.separator + "drawings";
    private final ImService imService;
    Logger logger = Logger.getLogger("logger");

    @Autowired
    public ImController(ImService imService) {
        this.imService = imService;
    }

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
        model.addAttribute("web", CHROME_WEB_SERVER_URL);
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
        if (dialog == null) {
            return new ModelAndView("im");
        }
        List<Message> messageList = imService.getDialogMessageList(Integer.parseInt(dialog_id), user);
        if (messageList != null && dialog.getNewMessagesCount() > 0) {
            if (!messageList.get(messageList.size() - 1).getSender().getEmail().equals(user.getEmail())) {
                dialog.setNewMessagesCount(0);
            }
        }
        modelAndView.addObject("user", user);
        modelAndView.addObject("dialog", dialog);
        modelAndView.addObject("messageList", messageList);
        modelAndView.addObject("web", CHROME_WEB_SERVER_URL);

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
        logger.info("dialog: " + dialogToUpdate);
        assert dialogToUpdate != null;
        Message message = imService.createMessage(messageBody, dialogToUpdate, user);
        dialogToUpdate.addMessage(message);
        dialogToUpdate.setNewMessagesCount(dialogToUpdate.getNewMessagesCount() + 1);
        imService.updateDialogByNewMessagesCount(dialogToUpdate);

        modelAndView.addObject("dialog", dialogToUpdate);
        modelAndView.addObject("messageList", dialogToUpdate.getMessageList());
        modelAndView.addObject("tomcatDir", CATALINA_HOME);
        modelAndView.addObject("web", CHROME_WEB_SERVER_URL);

        return modelAndView;
    }

    @RequestMapping("/send_message/{dialog_id}")
    public void nullMessage(@PathVariable("dialog_id") String dialog_id) throws MyNullMessageException {
        throw new MyNullMessageException("Сообщение не может быть пустым");
    }

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws MyUploadException {
        try {
            String name = file.getOriginalFilename();
            byte[] bytes = file.getBytes();

            //create dir
            File dir = new File(CATALINA_HOME);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            //create file
            assert name != null;
            String path = dir.getAbsolutePath() + File.separator + name.replace(" ", "_");
            File serverFile = new File(path);
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
            stream.write(bytes);
            stream.close();

            logger.info("image saved at: " + serverFile.getAbsolutePath());

            User user = (User) request.getSession().getAttribute("login_user");
            logger.info("login_user: " + user);
            user.setAvatar(path);
//            imService.changeAvatar(user);

            logger.info("avatar changed: " + user.getAvatar());

            return "redirect:/im";
        } catch (Exception exception) {
            throw new MyUploadException("Выберите файл");
        }
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

    @ExceptionHandler(MyUploadException.class)
    public String handleError(@NotNull Model model, @NotNull MyUploadException exception) {
        model.addAttribute("errorMessage", exception.getMessage());
        return "errors/ERR_FILE_NOT_FOUND";
    }
}