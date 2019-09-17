package com.gmail.quiz.controller;

import com.gmail.quiz.model.User;
import com.gmail.quiz.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<User> insertUser(@RequestBody final User user) {
        return ResponseEntity.ok(service.insertUser(user));
    }

    @GetMapping
    public ResponseEntity<List<User>> selectAllUsers() {
        return ResponseEntity.ok(service.selectAllUsers());
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<User> deleteUserByID(@PathVariable("id") final long id) {
        User userToDelete = service.deleteUserByID(id);
        return userToDelete == null ?
                ResponseEntity.badRequest().body(null) :
                ResponseEntity.ok(userToDelete);
    }

    @GetMapping(path = "/{id}")
    public User getUserById(@PathVariable("id") final long id) {
        return service.getUserById(id)
                .orElse(null);
    }

    @PutMapping(path = "/{id}")
    public User updateUserById(@PathVariable("id") long id, @RequestBody User user) {
        return service.updateUserById(id, user);
    }

}
