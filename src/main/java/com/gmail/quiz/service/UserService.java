package com.gmail.quiz.service;

import com.gmail.quiz.dao.user.UserDAO;
import com.gmail.quiz.model.Role;
import com.gmail.quiz.model.User;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@NoArgsConstructor
public class UserService {

    private  UserDAO userDAO;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(@Qualifier("userSpringData") UserDAO dao, PasswordEncoder passwordEncoder) {
        this.userDAO = dao;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean createUser(User user) {
        log.info("createUser executed");
        log.debug("user : {}", user);

        if (isNew(user)) {
            log.info("user is new");

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setEnabled(true);
            //delete later
            if (!user.getUserName().equals("a")) {
                user.setRoles(Collections.singleton(Role.USER));
            }
            //^
            log.debug("user after installation {}", user);

            userDAO.createUser(user);
            return true;
        }
        log.info("user is not new");
        return false;
    }

    public User getUserById(long id) {
        log.info("getUserById is executed");
        log.debug("id : {}", id);

        return userDAO.getUserById(id);
    }

    public User getUserByName(String name) {
        log.info("getUserByName executed");
        log.debug("name : {}", name);

        return userDAO.getUserByName(name);
    }

    public List<User> getAllUsers() {
        log.info("getAllUsers executed");

        return userDAO.getAllUsers();
    }

    public User updateUser(User user) {
        log.info("updateUser executed");
        log.debug("user : {}", user);

        return userDAO.updateUser(user);
    }

    public void deleteUserById(long id) {
        log.info("deleteUserById executed");
        log.debug("id : {}", id);

        userDAO.deleteUserById(id);
    }

    public void deleteAll() {
        log.info("deleteAll executed");

        userDAO.deleteAll();
    }

    private boolean isNew(User user) {
        log.info("isNew executed");
        log.debug("user : {}", user);

        return userDAO.getUserByName(user.getUserName()) == null;
    }

}
