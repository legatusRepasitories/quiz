package com.gmail.quiz.controller;

import com.gmail.quiz.model.Role;
import com.gmail.quiz.model.User;
import com.gmail.quiz.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;

import static java.lang.String.format;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;


    @Test
    @WithMockUser(roles = "ADMIN")
    public void Should_ReturnUserPage_For_AdminUser() throws Exception {

        User user = basicEnabledUserWithRole();
        when(userService.getUserById(user.getId())).thenReturn(user);


        mockMvc.perform(get("/user/{id}", user.getId()))
                .andDo(print())


                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", user))
                .andExpect(content().string(containsString(format("<div id=\"userId\">%d</div>", user.getId()))))
                .andExpect(content().string(containsString(format("<div id=\"userName\">%s</div>", user.getUserName()))));

        verify(userService).getUserById(user.getId());

    }

    @Test
    @WithMockUser
    public void Should_ForwardToAccessDeniedPage_For_NotAdminUser_When_UserUrlCalled() throws Exception {

        mockMvc.perform(get("/user/{id}", 0))
                .andDo(print())


                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/errors/accessDenied"));

        verify(userService, never()).getUserById(anyLong());

    }

    @Test
    public void Should_RedirectToLoginPage_For_UnauthorizedUser_When_UserUrlCalled() throws Exception {

        mockMvc.perform(get("/user/{id}", 0))
                .andDo(print())


                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        verify(userService, never()).getUserById(anyLong());

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void Should_RedirectToErrorsWrongArgumentPage_For_AdminUser_When_WrongIdPassed() throws Exception {

        mockMvc.perform(get("/user/{id}", 0))
                .andDo(print())



                .andExpect(view().name("errors/wrongArgument"))
                .andExpect(content().string(containsString("There is no entity with such Id.")));

        verify(userService).getUserById(anyLong());

    }



    @Test
    @WithMockUser(roles = "ADMIN")
    public void Should_ReturnUsersPage_For_AdminUser() throws Exception {

        mockMvc.perform(get("/users")
                .accept(MediaType.TEXT_HTML))
                .andDo(print())


                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void Should_ForwardToAccessDeniedPage_For_NotAdminUser_When_UsersUrlCalled() throws Exception {

        mockMvc.perform(get("/users")
                .accept(MediaType.TEXT_HTML))
                .andDo(print())


                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/errors/accessDenied"));

    }

    @Test
    public void Should_ReturnLoginPage_For_UnauthorizedUser_When_UsersUrlCalled() throws Exception {

        mockMvc.perform(get("/users")
                .accept(MediaType.TEXT_HTML))
                .andDo(print())


                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

    }


    private User basicEnabledUserWithRole() {
        User user = new User("userName", "password");
        user.setEnabled(true);
        user.setRoles(Collections.singleton(Role.USER));
        return user;

    }

}