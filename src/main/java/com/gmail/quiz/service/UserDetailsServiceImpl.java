package com.gmail.quiz.service;

import com.gmail.quiz.model.User;
import com.gmail.quiz.service.UserService;
import com.gmail.quiz.util.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service("myUserDetailsService")
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserService service;

    public UserDetailsServiceImpl() {
    }


    public UserDetailsServiceImpl(UserService service) {
        this.service = service;
    }

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        log.info("loadUserByUsername executed");
        log.debug("user name : {}", name);

        User user = service.getUserByName(name);

        if (user == null) {
            log.info("user not found");
            log.debug("no user with name {} found", name);

            throw new UsernameNotFoundException("No user found for " + name + ".");
        }

        log.info("user found");
        return new UserDetailsImpl(user);
    }

    public void setService(UserService service) {
        this.service = service;
    }
}
