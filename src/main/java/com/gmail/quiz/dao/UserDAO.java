package com.gmail.quiz.dao;

import com.gmail.quiz.model.User;
import com.gmail.quiz.util.Util;

import java.util.List;
import java.util.Optional;

public interface UserDAO {

    User insertUser(final long id, final User user);

    default User insertUser(final User user){
        long id = Util.generateId();
        return insertUser(id, user);
    }

    List<User> selectAllUsers();

    User deleteUserById(final long id);

    User updateUserById(final long id, final User user);

    Optional<User> getUserById(final long id);

}
