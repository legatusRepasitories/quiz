package com.gmail.quiz.controller;

import com.gmail.quiz.model.User;
import com.gmail.quiz.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
public class MainController {

    private final UserService userService;

    @Autowired
    public MainController(UserService service) {
        this.userService = service;
    }

    @GetMapping("/")
    public String home() {
        log.info("home, GET");
        return "home";
    }

    @GetMapping("/quiz")
    public String quiz() {
        log.info("quiz, GET");
        return "quiz";
    }

    @GetMapping("/login")
    public String login( @RequestParam(value = "error", required = false) String error, Model model) {

        log.info("login, GET");
        log.debug("error {}", error);
        model.addAttribute("error", error != null);
        return "login";
    }

    @GetMapping("/registration")
    public String registration() {
        log.info("registration, GET");
        return "registration";
    }

    @PostMapping(value = "/registration")
    public String createUser(User user, Model model) {

        log.info("createUser, POST");
        log.debug("{}", user);


        boolean isCreated = userService.createUser(user);

        if (!isCreated) {
            log.info("User was not created");
            String message = String.format("User with name - %s exists!", user.getUserName());
            model.addAttribute("message", message);
            return "/registration";
        }

        log.info("User was created");
        return "redirect:/login";

    }

    @GetMapping("/errors/accessDenied")
    public String accessDenied() {
        log.info("accessDenied, GET");
        return "errors/accessDenied";
    }

}
