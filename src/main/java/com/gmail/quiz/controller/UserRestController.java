package com.gmail.quiz.controller;

import com.gmail.quiz.model.User;
import com.gmail.quiz.service.UserService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/user")
@Slf4j
public class UserRestController {

    private UserService userService;

    @Autowired
    public UserRestController(UserService service) {
        this.userService = service;
    }

    @GetMapping("/all")
    public List<User> getUsers() {
        log.info("getUsers, GET");

        return userService.getAllUsers();
    }
}
