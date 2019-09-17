package com.gmail.quiz.service;

import com.gmail.quiz.dao.UserDAO;
import com.gmail.quiz.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Qualifier("fakeDAO")
    private final UserDAO dao;

    @Autowired
    public UserService(UserDAO dao) {
        this.dao = dao;
    }

    public User insertUser(final User user) {
        return dao.insertUser(user);
    }

    public List<User> selectAllUsers() {
        return dao.selectAllUsers();
    }

    public User deleteUserByID(final long id) {
        return dao.deleteUserById(id);
    }

    public Optional<User> getUserById(final long id) {
        return dao.getUserById(id);
    }

    public User updateUserById(long id, User user) {
        return dao.updateUserById(id, user);
    }
}
