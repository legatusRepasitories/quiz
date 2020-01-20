package com.gmail.quiz.system_test;


import com.gmail.quiz.PostgresqlContainerImpl;
import com.gmail.quiz.dao.user.UserRepo;
import com.gmail.quiz.model.Role;
import com.gmail.quiz.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
//@ContextConfiguration(initializers = {QuizApplicationIntegrationTest.Initializer.class})
@Testcontainers
@Transactional
public class LoginRegistrationSystemTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User adminUser;
    private User simpleUser1;
    private User simpleUser2;

    @Container
    public static PostgreSQLContainer<PostgresqlContainerImpl> postgreSQLContainer = PostgresqlContainerImpl.getInstance();
    //or
    //    @Container
//    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
//            .withDatabaseName("testing-database")
//            .withUsername("sa")
//            .withPassword("sa");;
//
//    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
//
//        @Override
//        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
//
//            TestPropertyValues
//                    .of("spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
//                            "spring.datasource.username=" + postgreSQLContainer.getUsername(),
//                            "spring.datasource.password=" + postgreSQLContainer.getPassword())
//                    .applyTo(configurableApplicationContext.getEnvironment());
//
//        }
//
//    }


    // login
    @Test
    void Should_RegisterNewUser_When_UserNameNotOccupied_And_ReturnLoginPage() throws Exception {

        mockMvc.perform(post("/registration").with(csrf())
                .param("userName", "testUserName")
                .param("password", "testPassword"))
                .andDo(print())


                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        User user = userRepo.findByUserName("testUserName");
        assertThat(user).isNotNull();
    }

    @Test
    void Should_NotRegisterNewUser_When_UserNameIsOccupied_And_ReturnRegistrationPageWithErrorMessage() throws Exception {

        initEntities();
        userRepo.saveAll(List.of(adminUser, simpleUser1, simpleUser2));


        mockMvc.perform(post("/registration").with(csrf())
                .param("userName", simpleUser1.getUserName())
                .param("password", simpleUser1.getPassword()))
                .andDo(print())


                .andExpect(model().attributeExists("message"))
                .andExpect(content().string(containsString(String.format("User with name - %s exists!",
                        simpleUser1.getUserName()))));

        assertThat(userRepo.findAll().spliterator().estimateSize()).isEqualTo(3L);
    }

    // login
    @Test
    public void Should_NotAuthenticateUser_And_ReturnLoginPageWithErrorMessage_When_WrongCredentialsPassed() throws Exception {
        initEntities();
        userRepo.save(adminUser);


        mockMvc
                .perform(formLogin("/perform_login")
                        .user("userName", adminUser.getUserName())
                        .password("wrongPassword"))
                .andDo(print())


                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());

    }

    @Test
    public void Should_AuthenticateUserAndReturnMainPage_When_CorrectCredentialsPassed() throws Exception {
        initEntities();
        userRepo.save(adminUser);


        mockMvc
                .perform(formLogin("/perform_login")
                        .user("userName", adminUser.getUserName())
                        .password("adminPassword"))
                .andDo(print())


                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated());

    }


    private void initEntities() {
        adminUser = new User("admin", passwordEncoder.encode("adminPassword"));
        adminUser.setEnabled(true);
        adminUser.setRoles(Set.of(Role.ADMIN, Role.USER));

        simpleUser1 = new User("simpleUser1", passwordEncoder.encode("userPassword"));
        simpleUser1.setEnabled(true);
        simpleUser1.setRoles(Set.of(Role.USER));

        simpleUser2 = new User("simpleUser2", passwordEncoder.encode("userPassword"));
        simpleUser2.setEnabled(true);
        simpleUser2.setRoles(Set.of(Role.USER));
    }
}
