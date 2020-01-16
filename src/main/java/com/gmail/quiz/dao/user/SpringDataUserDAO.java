package com.gmail.quiz.dao.user;

import com.gmail.quiz.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Repository("userSpringData")
@Slf4j
public class SpringDataUserDAO implements UserDAO {

    private final UserRepo userRepo;

    @Autowired
    public SpringDataUserDAO(UserRepo repo) {
        this.userRepo = repo;
    }

    @Override
    public User createUser(User user) {
        log.info("createUser executed");
        log.debug("user : {}", user);

        return userRepo.save(user);
    }


    public User getUserByName(String name) {
        log.info("getUserByName executed");
        log.debug("name : {}", name);

        return userRepo.findByUserName(name);
    }

    @Override
    public User getUserById(long id) {
        log.info("getUserById executed");
        log.debug("id : {}", id);

        return userRepo.findById(id).orElse(null);
    }

    @Override
    public List<User> getAllUsers() {
        log.info("getAllUsers executed");

        return StreamSupport
                .stream(userRepo.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public User updateUser(User user) {
        log.info("updateUser executed");
        log.debug("user : {}", user);

        return userRepo.save(user);
    }

    @Override
    public void deleteUserById(long id) {
        log.info("deleteUserById executed");
        log.debug("id : {}", id);

        userRepo.deleteById(id);
    }

    @Override
    public void deleteAll() {
        log.info("deleteAll executed");

        userRepo.deleteAll();
    }
}
