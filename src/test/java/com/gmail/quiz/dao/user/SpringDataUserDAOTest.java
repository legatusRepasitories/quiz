package com.gmail.quiz.dao.user;

import com.gmail.quiz.model.Role;
import com.gmail.quiz.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(SpringDataUserDAO.class)
class SpringDataUserDAOTest {

    //    @Autowired private DataSource dataSource;
//    @Autowired private JdbcTemplate jdbcTemplate;
//    @Autowired private EntityManager entityManager;
//    @Autowired private UserRepo userRepository;
    @Autowired
    private SpringDataUserDAO repo;

    private User user1;
    private User user2;
    private User user3;


    @BeforeEach
    public void beforeEach() {

        user1 = new User("testDBUser1", "password");
        user1.setRoles(new HashSet<>(Collections.singletonList(Role.USER)));
        user1.setEnabled(true);

        user2 = new User("testDBUser2", "password");
        user2.setRoles(Collections.singleton(Role.USER));
        user2.setEnabled(true);

        user3 = new User("testDBUser3", "password");
        user3.setRoles(Collections.singleton(Role.USER));
        user3.setEnabled(true);


    }

    @Test
    void injectedComponentsAreNotNull() {

//        assertThat(dataSource).isNotNull();
//        assertThat(jdbcTemplate).isNotNull();
//        assertThat(entityManager).isNotNull();
//        assertThat(userRepository).isNotNull();
        assertThat(repo).isNotNull();
    }

    @Test
    void Should_FindUserByName_When_Saved() {
        repo.createUser(user1);


        User userFromBD = repo.getUserByName(user1.getUserName());


        assertThat(userFromBD).isNotNull();
        assertThat(userFromBD.getUserName()).isEqualTo(user1.getUserName());

    }

    @Test
    void Should_NotFindUserByName_When_NotSaved() {
        User userFromBD = repo.getUserByName(user1.getUserName());


        assertThat(userFromBD).isNull();
    }

    @Test
    void Should_FindUserById_When_Saved() {
        User savedUser = repo.createUser(user1);


        User userFromBD = repo.getUserById(savedUser.getId());


        assertThat(userFromBD).isNotNull();
        assertThat(userFromBD.getUserName()).isEqualTo(user1.getUserName());
    }

    @Test
    void Should_NotFindUserById_When_NotSaved() {

        User userFromBD = repo.getUserById(user1.getId());


        assertThat(userFromBD).isNull();
    }

    @Test
    void Should_ContainAllSavedUsers() {
        repo.createUser(user1);
        repo.createUser(user2);
        repo.createUser(user3);


        Iterable<User> allUsers = repo.getAllUsers();


        Set<User> users = StreamSupport.stream(allUsers.spliterator(), false).collect(Collectors.toSet());
        assertThat(users).hasSize(3);
        assertThat(users).contains(user1);
        assertThat(users).contains(user2);
        assertThat(users).contains(user3);
    }

    @Test
    void Should_DeleteUser() {
        User savedUser = repo.createUser(user1);
        repo.createUser(user2);
        repo.createUser(user3);


        repo.deleteUserById(savedUser.getId());
        Iterable<User> allUsers = repo.getAllUsers();


        Set<User> users = StreamSupport.stream(allUsers.spliterator(), false).collect(Collectors.toSet());
        assertThat(users).hasSize(2);
        assertThat(users).doesNotContain(user1);
        assertThat(users).contains(user2);
        assertThat(users).contains(user3);
    }

    @Test
    void Should_UpdateUser() {
        User savedUser = repo.createUser(user1);
        savedUser.setPassword("newPassword");

        User userAfterUpdating = repo.updateUser(savedUser);


        assertThat(userAfterUpdating).isNotNull();
        assertThat(userAfterUpdating.getPassword()).isEqualTo("newPassword");
    }

    @Test
    void Should_DeleteAllUser() {
        repo.createUser(user1);
        repo.createUser(user2);
        repo.createUser(user3);


        repo.deleteAll();
        Iterable<User> allUsers = repo.getAllUsers();


        Set<User> users = StreamSupport.stream(allUsers.spliterator(), false).collect(Collectors.toSet());
        assertThat(users).isEmpty();
    }

}