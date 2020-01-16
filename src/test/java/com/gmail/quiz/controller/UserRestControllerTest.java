package com.gmail.quiz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmail.quiz.model.User;
import com.gmail.quiz.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserRestController.class)
class UserRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void Should_ReturnUserList_For_AdminUser() throws Exception {

        List<User> users = Collections.singletonList(new User("userName", "password"));
        when(userService.getAllUsers()).thenReturn(users);
        String expectedResponseBody = objectMapper.writeValueAsString(users);


        MvcResult mvcResult = mockMvc.perform(get("/api/user/all"))
                .andDo(print())


                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        assertThat(expectedResponseBody).isEqualTo(actualResponseBody);

        verify(userService).getAllUsers();

    }

    @Test
    @WithMockUser
    public void Should_ForwardToAccessDeniedPage_When_NotAdminUser_RequestApiUsersAll() throws Exception {

        mockMvc.perform(get("/api/user/all"))
                .andDo(print())


                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/errors/accessDenied"));

        verify(userService, never()).getAllUsers();

    }

    @Test
    public void Should_RedirectToLoginPage_When_UnauthorizedUser_RequestApiUsersAll() throws Exception {

        mockMvc.perform(get("/api/user/all"))
                .andDo(print())


                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        verify(userService, never()).getAllUsers();

    }

}