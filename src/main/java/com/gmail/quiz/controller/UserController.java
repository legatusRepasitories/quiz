package com.gmail.quiz.controller;

import com.gmail.quiz.service.UserService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@Slf4j
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService service) {
        this.userService = service;
    }


    @GetMapping(path = "/user/{id}")
    public String getUserById(@PathVariable("id") final long id, Model model) {
        log.info("getUserById, GET");
        log.debug("id:{}", id);

        val user = userService.getUserById(id);

        log.debug("user returned by service: {}", user);

        if ( user == null) {
            log.info("user == null");

            return "errors/wrongArgument";

        }

        model.addAttribute("user", user);
        return "user/user";
    }

    @GetMapping("/users")
    public String getAllUsers() {
        log.info("getAllUsers, GET");
        return "user/users";
    }

}
