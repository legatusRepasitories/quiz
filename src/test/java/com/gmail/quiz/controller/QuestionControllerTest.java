package com.gmail.quiz.controller;

import com.gmail.quiz.model.Question;
import com.gmail.quiz.service.QuestionService;
import com.gmail.quiz.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = QuestionController.class)
class QuestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuestionService questionService;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void Should_ReturnNewQuestionPage_For_Admin() throws Exception {

        Question question = new Question();


        mockMvc.perform(get("/newQuestion"))
                .andDo(print())


                .andExpect(status().isOk())
                .andExpect(model().attribute("question", question));

    }

    @Test
    @WithMockUser
    public void Should_ForwardToAccessDeniedPage_For_NotAdmin_When_NewQuestionUrlCalled() throws Exception {

        mockMvc.perform(get("/newQuestion"))
                .andDo(print())


                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/errors/accessDenied"));

    }

    @Test
    public void Should_RedirectToLoginPage_For_UnauthorizedUser_When_NewQuestionUrlCalled() throws Exception {

        mockMvc.perform(get("/newQuestion"))
                .andDo(print())


                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

    }

    //
    @Test
    @WithMockUser(roles = "ADMIN")
    public void Should_ReturnQuestionsPage_For_Admin() throws Exception {
        mockMvc.perform(get("/questions"))
                .andDo(print())


                .andExpect(status().isOk());

    }

    @Test
    @WithMockUser
    public void Should_ForwardToAccessDeniedPage_For_NotAdmin_When_QuestionsUrlCalled() throws Exception {

        mockMvc.perform(get("/questions"))
                .andDo(print())


                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/errors/accessDenied"));

    }

    @Test
    public void Should_RedirectToLoginPage_For_UnauthorizedUser_When_QuestionsUrlCalled() throws Exception {

        mockMvc.perform(get("/questions"))
                .andDo(print())


                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

    }
//

    @Test
    @WithMockUser(roles = "ADMIN")
    public void Should_ReturnQuestionPage_For_Admin() throws Exception {

        Question question = sampleQuestion();
        when(questionService.getQuestionById(1L)).thenReturn(question);


        mockMvc.perform(get("/question/{id}", 1L))
                .andDo(print())


                .andExpect(status().isOk())
                .andExpect(model().attribute("question", question));

        verify(questionService).getQuestionById(1L);

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void Should_ReturnWrongArgumentPage_For_Admin_When_WrongIdPassed() throws Exception {

        mockMvc.perform(get("/question/{id}", 1L))
                .andDo(print())


                .andExpect(view().name("errors/wrongArgument"))
                .andExpect(content().string(containsString("There is no entity with such Id.")));

        verify(questionService).getQuestionById(1L);

    }

    @Test
    @WithMockUser
    public void Should_ForwardToAccessDeniedPage_For_NotAdmin_When_QuestionUrlCalled() throws Exception {

        mockMvc.perform(get("/question/{id}", 1L))
                .andDo(print())


                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/errors/accessDenied"));

        verify(questionService, never()).getQuestionById(1L);
    }

    @Test
    public void Should_RedirectToLoginPage_For_UnauthorizedUser_When_QuestionUrlCalled() throws Exception {

        mockMvc.perform(get("/question/{id}", 0))
                .andDo(print())


                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        verify(questionService, never()).getQuestionById(1L);

    }


    private Question sampleQuestion() {
        return new Question("question text",
                Collections.singletonList("right variant"),
                Arrays.asList("wrong variant #1", "wrong variant #2"));
    }

}