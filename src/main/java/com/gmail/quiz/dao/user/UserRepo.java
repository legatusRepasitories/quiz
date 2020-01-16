package com.gmail.quiz.dao.user;

import com.gmail.quiz.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepo extends CrudRepository<User, Long> {

    User findByUserName(String userName);

}
