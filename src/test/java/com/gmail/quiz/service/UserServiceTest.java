package com.gmail.quiz.service;

import com.gmail.quiz.dao.user.UserDAO;
import com.gmail.quiz.model.Role;
import com.gmail.quiz.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserDAO dao;

    @InjectMocks
    private UserService userService;

    private User user;


    @BeforeEach
    void beforeEach() {
        user = new User("userName", "password");
    }

    @Test
    void Should_SaveUser_When_UserIsNew() {

        when(dao.getUserByName(user.getUserName())).thenReturn(null);
        when(dao.createUser(user)).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("EncodedPassword");
        String passwordBeforeEncoding = user.getPassword();


        boolean isUserCreated = userService.createUser(this.user);


        assertThat(isUserCreated).isTrue();
        assertThat(user).isNotNull();
        assertThat(user.getPassword()).isNotEqualTo(passwordBeforeEncoding);
        assertThat(user.getRoles()).containsOnly(Role.USER);
        assertThat(user.getRoles()).hasSize(1);
        assertThat(user.isEnabled()).isTrue();

        verify(dao).getUserByName(any());
        verify(dao).createUser(any());
        verify(passwordEncoder, times(1)).encode(anyString());

    }

    @Test
    void Should_FailToSaveUser_When_UserAlreadyExist() {

        when(dao.getUserByName(user.getUserName())).thenReturn(user);
        String passwordBeforeEncoding = user.getPassword();


        boolean isUserCreated = userService.createUser(user);


        assertThat(isUserCreated).isFalse();
        assertThat(user.isEnabled()).isFalse();
        assertThat(user.getRoles()).isNull();
        assertThat(user.getPassword()).isEqualTo(passwordBeforeEncoding);

        verify(dao).getUserByName(anyString());
        verify(dao, never()).createUser(any());
        verifyNoInteractions(passwordEncoder);

    }

    @Test
    public void Should_ReturnUser_When_IdExist() {

        when(dao.getUserById(anyLong())).thenReturn(user);


        User returnedUser = userService.getUserById(1L);


        assertThat(returnedUser).isNotNull();
        assertThat(user).isEqualTo(returnedUser);

        verify(dao).getUserById(anyLong());

    }

    @Test
    public void Should_ReturnNull_When_NoUserWithSuchId() {

        when(dao.getUserById(anyLong())).thenReturn(null);


        User returnedUser = userService.getUserById(123L);


        assertThat(returnedUser).isNull();

        verify(dao).getUserById(anyLong());

    }

    @Test
    public void Should_ReturnUser_When_UserWithThatNameExist() {

        when(dao.getUserByName(anyString())).thenReturn(user);


        User returnedUser = userService.getUserByName(user.getUserName());


        assertThat(returnedUser).isNotNull();
        assertThat(returnedUser).isEqualTo(user);

        verify(dao).getUserByName(anyString());

    }

    @Test
    public void Should_ReturnNull_When_UserWithThatNameNotExist() {

        when(dao.getUserByName(anyString())).thenReturn(null);


        User returnedUser = userService.getUserByName(user.getUserName());


        assertThat(returnedUser).isNull();

        verify(dao).getUserByName(anyString());

    }

    @Test
    public void Should_ReturnUserList() {

        when(dao.getAllUsers())
                .thenReturn(Stream.of(user,
                        new User("a", "a"),
                        new User("u", "u"))
                        .collect(Collectors.toList()));


        List<User> users = userService.getAllUsers();


        assertThat(users).isNotNull();
        assertThat(users).hasSize(3);
        assertThat(users).contains(user);

        verify(dao).getAllUsers();

    }

    @Test
    public void Should_UpdateUser() {

        when(dao.updateUser(user)).thenReturn(user);


        User updatedUser = userService.updateUser(user);


        assertThat(updatedUser).isNotNull();

        verify(dao).updateUser(user);
    }

    @Test
    public void Should_DeleteUser_With_PassedId() {

        userService.deleteUserById(anyLong());


        verify(dao).deleteUserById(anyLong());
    }

    @Test
    public void Should_DeleteAllUsers() {

        userService.deleteAll();


        verify(dao).deleteAll();
    }
}