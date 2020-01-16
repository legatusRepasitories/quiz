package com.gmail.quiz.dao.user;

import com.gmail.quiz.model.User;

import java.util.List;

public interface UserDAO {

    User createUser(User user);

    User getUserById(long id);

    User getUserByName(String name);

    List<User> getAllUsers();

    User updateUser(User user);

    void deleteUserById(long id);

    void deleteAll();

}
