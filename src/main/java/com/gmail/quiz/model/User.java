package com.gmail.quiz.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gmail.quiz.util.Util;

public class User {
    private final long id;
    private final String login;


    public User(@JsonProperty("id") final long id,
                @JsonProperty("login") final String login) {
        this.id = id;
        this.login = login;
    }

    public long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }
}
