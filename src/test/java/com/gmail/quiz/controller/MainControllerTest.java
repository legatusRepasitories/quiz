package com.gmail.quiz.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmail.quiz.model.Role;
import com.gmail.quiz.model.User;
import com.gmail.quiz.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Collections;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MainController.class)
//@WithMockUser(roles={"USER","ADMIN"})
//@WithAnonymousUser
public class MainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UserService userService;


    @Test
    public void Should_ReturnMainPage_ForAnyUser() throws Exception {

        mockMvc.perform(get("/")
                .accept(MediaType.TEXT_HTML)
                .characterEncoding("UTF-8"))
                .andDo(print())


                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Content of the main page")));

    }

    @Test
    public void Should_RedirectToLogin_When_AnonymousUserRequestQuiz() throws Exception {

        mockMvc.perform(get("/quiz")
                .accept(MediaType.TEXT_HTML)
                .characterEncoding("UTF-8"))
                .andDo(print())


                .andExpect(status().isFound())
                .andExpect(redirectedUrlPattern("**/login"));

    }

    @Test
    @WithMockUser
    public void Should_Return200_When_AuthenticatedUserRequestQuiz() throws Exception {

        mockMvc.perform(get("/quiz")
                .accept(MediaType.TEXT_HTML)
                .characterEncoding("UTF-8"))
                .andDo(print())


                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Start Quiz")));

    }

    @Test
    public void Should_ReturnLoginPage_ForAnyUser() throws Exception {

        mockMvc.perform(get("/login")
                .accept(MediaType.TEXT_HTML)
                .characterEncoding("UTF-8"))
                .andDo(print())


                .andExpect(status().isOk());

    }

    @Test
    public void Should_ReturnRegistrationPage_ForAnyUser() throws Exception {

        mockMvc.perform(get("/registration")
                .accept(MediaType.TEXT_HTML)
                .characterEncoding("UTF-8"))
                .andDo(print())


                .andExpect(status().isOk());

    }

    @Test
    public void Should_NotAuthenticateUserAndReturnLoginPageWithErrorMessage_When_WrongCredentialsPassed() throws Exception {

        User user = basicUser();


        ResultActions requestResult = mockMvc
                .perform(formLogin("/perform_login")
                        .user("userName", user.getUserName())
                        .password(user.getPassword()))
                .andDo(print())


                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());

        verify(userService).getUserByName(user.getUserName());
    }

    @Test
    public void Should_AuthenticateUserAndReturnMainPage_When_CorrectCredentialsPassed() throws Exception {

        User user = basicUser();
        String password = user.getPassword();
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        user.setEnabled(true);
        user.setRoles(Collections.singleton(Role.USER));

        when(userService.getUserByName(user.getUserName())).thenReturn(user);


        mockMvc
                .perform(formLogin("/perform_login")
                        .user("userName", user.getUserName())
                        .password(password))
                .andDo(print())


                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated());

        verify(userService).getUserByName(user.getUserName());

    }

    @Test
    public void Should_RegisterNewUserAndRedirectToLoginPage_When_NewUserName() throws Exception {

        User user = basicUser();

        when(userService.createUser(user)).thenReturn(true);


        mockMvc.perform(post("/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_HTML)
                .param("userName", user.getUserName())
                .param("password", user.getPassword())
                .with(csrf()))
                .andDo(print())


                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(unauthenticated());

        verify(userService).createUser(user);

    }

    @Test
    public void Should_ReturnRegistrationPageWithErrorMessage_When_UserNameAlreadyExist() throws Exception {

        User user = basicUser();

        when(userService.createUser(user)).thenReturn(false);


        mockMvc.perform(post("/registration")
                .accept(MediaType.TEXT_HTML)
                .param("userName", user.getUserName())
                .param("password", user.getPassword())
                .with(csrf()))
                .andDo(print())


                .andExpect(status().isOk())
                .andExpect(model().attributeExists("message"))
                .andExpect(unauthenticated())
                .andExpect(content().string(containsString(String.format("User with name - %s exists!", user.getUserName()))));

        verify(userService).createUser(user);

    }

    private User basicUser() {
        return new User("userName", "password");
    }

}
