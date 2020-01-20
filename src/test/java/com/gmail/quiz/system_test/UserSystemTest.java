package com.gmail.quiz.system_test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmail.quiz.PostgresqlContainerImpl;
import com.gmail.quiz.dao.user.UserRepo;
import com.gmail.quiz.model.Role;
import com.gmail.quiz.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Transactional
public class UserSystemTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Container
    public static PostgreSQLContainer<PostgresqlContainerImpl> postgreSQLContainer = PostgresqlContainerImpl.getInstance();

    private User adminUser;
    private User simpleUser1;
    private User simpleUser2;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void Should_ReturnUserPage_For_AdminUser() throws Exception {
        initEntities();
        User savedUser = userRepo.save(simpleUser1);


        mockMvc.perform(get("/user/{id}", savedUser.getId()))
                .andDo(print())


                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", savedUser))
                .andExpect(content().string(containsString(format("<div id=\"userId\">%d</div>", savedUser.getId()))))
                .andExpect(content().string(containsString(format("<div id=\"userName\">%s</div>", savedUser.getUserName()))));

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void Should_ReturnUserList_For_AdminUser() throws Exception {
        initEntities();
        String expectedResponseBody = objectMapper
                .writeValueAsString(userRepo.saveAll(List.of(adminUser, simpleUser1, simpleUser2)));


        MvcResult mvcResult = mockMvc.perform(get("/api/user/all"))
                .andDo(print())


                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        assertThat(expectedResponseBody).isEqualTo(actualResponseBody);

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
