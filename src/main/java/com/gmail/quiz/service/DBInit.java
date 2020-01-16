package com.gmail.quiz.service;

import com.gmail.quiz.model.Role;
import com.gmail.quiz.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DBInit
//        implements CommandLineRunner
{

    private UserService service;

    @Autowired
    public DBInit(UserService service) {
        this.service = service;
    }


    public void run(String... args) throws Exception {
        service.deleteAll();

        User admin = new User("a", "a");
        admin.setEnabled(true);
//        admin.setRoles(Collections.singleton(Role.ADMIN));
        admin.setRoles(Stream.of(Role.ADMIN, Role.USER).collect(Collectors.toSet()));

        User user = new User("u", "u");
        user.setEnabled(true);
        user.setRoles(Collections.singleton(Role.USER));

        service.createUser(admin);
        service.createUser(user);
    }




}
