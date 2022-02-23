package org.example.web.controllers;

import org.example.app.exceptions.MyLoginException;
import org.example.app.services.LoginService;
import org.example.web.dto.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class LoginController {

    private final LoginService loginService;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @NotNull
    @GetMapping("/login")
    public String getLoginPage(@NotNull Model model) {
        model.addAttribute("user", new User());
        return "login_page";
    }

    @NotNull
    @PostMapping("/login/auth")
    public String authenticate(@ModelAttribute("login_user") User user, HttpServletRequest request) throws MyLoginException {
        if (loginService.authenticate(user)) {
            request.getSession().setAttribute("login_user", user); // session user
            return "redirect:/im";
        } else {
            throw new MyLoginException("Invalid username or password");
        }
    }

    @NotNull
    @GetMapping("/register")
    public String getRegisterPage(@NotNull Model model) {
        model.addAttribute("user", new User());
        return "register_page";
    }

    @PostMapping("/register/auth")
    public String register(@ModelAttribute("login_user") User user, @NotNull HttpServletRequest request) throws MyLoginException {
        if (loginService.register(user)) {
            // registration fail back to login
            throw new MyLoginException("User with this e-mail has already been registered");
        } else {
            request.getSession().setAttribute("login_user", user); // session user
            // registration OK redirect to im
            return "redirect:/im";
        }
    }
}