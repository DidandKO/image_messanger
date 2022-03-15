package org.example.web.controllers;

import org.example.app.exceptions.MyLoginException;
import org.example.app.services.LoginService;
import org.example.web.dto.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

@Controller
public class LoginController {

    private final LoginService loginService;
    Logger logger = Logger.getLogger("logger");

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @NotNull
    @GetMapping("/login")
    public String login(@NotNull Model model) {
        model.addAttribute("user", new User());
        return "login_page";
    }

    @NotNull
    @PostMapping("/login/auth")
    public String authenticate(@ModelAttribute("user") User user, HttpServletRequest request) throws MyLoginException {
        logger.info("try login with user: " + user);
        if (loginService.authenticate(user)) {
            user = loginService.findRegisteredUserByLogin(user.getEmail());
            request.getSession().setAttribute("login_user", user); // session user
            logger.info("login user: " + user);
            return "redirect:/im";
        } else {
            throw new MyLoginException("Invalid username or password");
        }
    }

    @NotNull
    @GetMapping("/register")
    public String register(@NotNull Model model) {
        model.addAttribute("user", new User());
        return "register_page";
    }

    @PostMapping("/register/auth")
    public String register(@ModelAttribute("user") User user, @NotNull HttpServletRequest request) throws MyLoginException {
        if (loginService.register(user)) {
            logger.info("registration fail back to login");
            throw new MyLoginException("User with this e-mail has already been registered");
        } else {
            user.setUser_id(user.hashCode());
            request.getSession().setAttribute("login_user", user);
            logger.info("registration OK redirect to im");
            logger.info("new user: " + user);
            return "redirect:/im";
        }
    }

    @ExceptionHandler(MyLoginException.class)
    public String handleError(@NotNull Model model, @NotNull MyLoginException exception) {
        model.addAttribute("errorMessage", exception.getMessage());
        return "errors/404";
    }
}