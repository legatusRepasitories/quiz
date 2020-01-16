package com.gmail.quiz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmail.quiz.dto.QuestionTO;
import com.gmail.quiz.model.Question;
import com.gmail.quiz.service.QuestionService;
import com.gmail.quiz.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = QuestionRestController.class)
class QuestionRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private QuestionService questionService;

    @MockBean
    private UserService userService;

    private Question question;

    @BeforeEach
    private void initQuestion() {
        question = sampleQuestion();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void Should_CreateNewQuestion_And_ReturnQuestionTO_For_Admin() throws Exception {

        String jsonQuestion = objectMapper.writeValueAsString(question);
        when(questionService.createQuestion(question)).thenReturn(question);


        ResultActions resultActions = mockMvc.perform(post("/api/question").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonQuestion))
                .andDo(print());


        resultActions.andExpect(status().isOk());

        String response = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(response).contains(String.valueOf(question.getId()));
        assertThat(response).contains(question.getText());
        assertThat(response).contains(question.getRightVariants().get(0));
        assertThat(response).contains(question.getWrongVariants().get(0));
        assertThat(response).contains(question.getWrongVariants().get(1));

        verify(questionService).createQuestion(question);

    }

    @Test
    @WithMockUser
    public void Should_NotTryToCreateNewUser_When_NotAdmin() throws Exception {

        String jsonQuestion = objectMapper.writeValueAsString(question);

        mockMvc.perform(post("/api/question").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonQuestion))
                .andDo(print())

                .andExpect(status().isForbidden());

        verify(questionService, never()).createQuestion(question);

    }

    @Test
    public void Should_NotTryToCreateNewUser_When_UnauthorizedUser() throws Exception {

        String jsonQuestion = objectMapper.writeValueAsString(question);

        mockMvc.perform(post("/api/question").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonQuestion))
                .andDo(print())

                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        verify(questionService, never()).createQuestion(question);

    }

    //

    @Test
    @WithMockUser(roles = "ADMIN")
    public void Should_ReturnQuestion_For_AdminUser() throws Exception {

        when(questionService.getQuestionById(0)).thenReturn(question);


        MvcResult mvcResult = mockMvc.perform(get("/api/question/{id}", 0))
                .andDo(print())


                .andExpect(status().isOk())
                .andReturn();
        assertThat(mvcResult.getResponse().getContentAsString(), is(equalTo(objectMapper.writeValueAsString(question))));


        verify(questionService).getQuestionById(0);
    }

    @Test
    @WithMockUser
    public void Should_NotReturnQuestion_For_NotAdminUser() throws Exception {

        mockMvc.perform(get("/api/question/{id}", 0))
                .andDo(print())


                .andExpect(status().isForbidden());

        verify(questionService, never()).getQuestionById(0);
    }

    @Test
    public void Should_NotReturnQuestion_For_UnauthorizedUser() throws Exception {

        mockMvc.perform(get("/api/question/{id}", 0))
                .andDo(print())


                .andExpect(status().is3xxRedirection());

        verify(questionService, never()).getQuestionById(0);
    }

//

    @Test
    @WithMockUser
    public void Should_ReturnQuestionList_For_AuthenticatedUser() throws Exception {

        when(questionService.getAllQuestions()).thenReturn(Collections.singleton(new QuestionTO(question)));


        mockMvc.perform(get("/api/question/all"))
                .andDo(print())


                .andExpect(status().isOk())
                .andExpect(content().string(containsString(question.getText())));

        verify(questionService).getAllQuestions();

    }

    @Test
    public void Should_NotReturnQuestionList_For_UnauthorizedUser() throws Exception {

        mockMvc.perform(get("/api/question/all"))
                .andDo(print())


                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        verify(questionService, never()).getAllQuestions();

    }

    //

    @Test
    @WithMockUser(roles = "ADMIN")
    public void Should_UpdateQuestion_For_AdminUser() throws Exception {

        String jsonQuestion = objectMapper.writeValueAsString(question);
        when(questionService.updateQuestion(question)).thenReturn(question);


        mockMvc.perform(put("/api/question")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonQuestion))
                .andDo(print())


                .andExpect(status().isOk())
                .andExpect(content().string(containsString(question.getText())));

        verify(questionService).updateQuestion(question);

    }

    @Test
    @WithMockUser
    public void Should_NotUpdateQuestion_For_NotAdminUser() throws Exception {

        String jsonQuestion = objectMapper.writeValueAsString(question);

        mockMvc.perform(put("/api/question")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonQuestion))
                .andDo(print())


                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/errors/accessDenied"));

        verify(questionService, never()).updateQuestion(question);

    }

    @Test
    public void Should_NotUpdateQuestion_For_UnauthorizedUser() throws Exception {

        String jsonQuestion = objectMapper.writeValueAsString(question);


        mockMvc.perform(put("/api/question")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonQuestion))
                .andDo(print())


                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        verify(questionService, never()).updateQuestion(question);

    }

    //

    @Test
    @WithMockUser(roles = "ADMIN")
    public void Should_DeleteQuestion_For_AdminUser() throws Exception {

        mockMvc.perform(delete("/api/question/{id}", 1)
                .with(csrf()))
                .andDo(print())


                .andExpect(status().isOk());

        verify(questionService).deleteQuestionById(1);

    }


    @Test
    @WithMockUser
    public void Should_NotDeleteQuestion_For_NotAdminUser() throws Exception {

        mockMvc.perform(delete("/api/question/{id}", 1)
                .with(csrf()))
                .andDo(print())


                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/errors/accessDenied"));

        verify(questionService, never()).deleteQuestionById(1);

    }

    @Test
    public void Should_NotDeleteQuestion_For_UnauthorizedUser() throws Exception {

        mockMvc.perform(delete("/api/question/{id}", 1)
                .with(csrf()))
                .andDo(print())


                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        verify(questionService, never()).deleteQuestionById(1);

    }

    // Validate

    @Test
    @WithMockUser
    public void Should_ReturnTrue_When_CorrectAnswersPassed_For_AuthenticatedUser() throws Exception {

        String jsonQuestion = objectMapper.writeValueAsString(question);
        when(questionService.getQuestionById(question.getId())).thenReturn(question);


        MvcResult mvcResult = mockMvc.perform(post("/api/question/validate")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonQuestion))
                .andDo(print())


                .andExpect(status().isOk())
                .andReturn();
        assertThat(mvcResult.getResponse().getContentAsString(), equalTo("true"));

        verify(questionService).getQuestionById(question.getId());

    }

    @Test
    @WithMockUser
    public void Should_ReturnFalse_When_WrongAnswersPassed_For_AuthenticatedUser() throws Exception {

        Question wrongAnswers = sampleQuestion();
        wrongAnswers.setRightVariants(Collections.singletonList("wrong"));

        String jsonQuestion = objectMapper.writeValueAsString(wrongAnswers);
        when(questionService.getQuestionById(question.getId())).thenReturn(question);


        MvcResult mvcResult = mockMvc.perform(post("/api/question/validate")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonQuestion))
                .andDo(print())


                .andExpect(status().isOk())
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString(), equalTo("false"));

        verify(questionService).getQuestionById(question.getId());

    }

    @Test
    public void Should_NotValidateQuestion_For_UnauthorizedUser() throws Exception {

        String jsonQuestion = objectMapper.writeValueAsString(question);


        mockMvc.perform(post("/api/question/validate")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonQuestion))
                .andDo(print())


                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        verify(questionService, never()).getQuestionById(question.getId());

    }


    private Question sampleQuestion() {
        return new Question("question text",
                Collections.singletonList("right variant"),
                Arrays.asList("wrong variant #1", "wrong variant #2"));

    }
}