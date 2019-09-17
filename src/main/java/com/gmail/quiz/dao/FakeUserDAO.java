package com.gmail.quiz.dao;

import com.gmail.quiz.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("fakeDAO")
public class FakeUserDAO implements UserDAO {

    private static List<User> fakeDB = new ArrayList<>();

    @Override
    public User insertUser(final long id, final User user) {
        User newUser = new User(id, user.getLogin());
        fakeDB.add(newUser);
        return newUser;
    }

    @Override
    public List<User> selectAllUsers() {
        return fakeDB;
    }

    @Override
    public User deleteUserById(final long id) {
        User userToDelete = getUserById(id).orElse(null);
        if (userToDelete != null) {
            fakeDB.remove(userToDelete);
        }
        return userToDelete;
    }

    @Override
    public Optional<User> getUserById(final long id) {
        return fakeDB.stream()
                .filter(user -> user.getId() == id)
                .findFirst();
    }

    @Override
    public User updateUserById(long id, User user) {
        return getUserById(id)
                .map(u -> {
                    int toUpdatePersonIndex = fakeDB.indexOf(u);
                    if (toUpdatePersonIndex >= 0) {
                        return fakeDB.set(toUpdatePersonIndex, new User(id, user.getLogin()));
                    }
                    return null;
                }).orElse(null);
    }
}
