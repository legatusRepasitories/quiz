package com.gmail.quiz.service;

import com.gmail.quiz.dao.user.UserRepo;
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
        implements CommandLineRunner
{

    private UserRepo repo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public DBInit(UserRepo repo) {
        this.repo = repo;
    }


    public void run(String... args) throws Exception {
        if (repo.findByUserName("admin") == null) {

            User admin = new User("admin", "admin");
            admin.setPassword(passwordEncoder.encode(admin.getPassword()));
            admin.setEnabled(true);
            admin.setRoles(Stream.of(Role.ADMIN, Role.USER).collect(Collectors.toSet()));

            repo.save(admin);
        }

    }




}
