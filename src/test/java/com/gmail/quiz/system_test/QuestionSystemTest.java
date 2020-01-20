package com.gmail.quiz.system_test;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmail.quiz.PostgresqlContainerImpl;
import com.gmail.quiz.dao.question.QuestionRepo;
import com.gmail.quiz.dto.QuestionTO;
import com.gmail.quiz.model.Question;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Transactional
public class QuestionSystemTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private QuestionRepo questionRepo;

    @Container
    public static PostgreSQLContainer<PostgresqlContainerImpl> postgreSQLContainer = PostgresqlContainerImpl.getInstance();

    private Question testQuestion1 = new Question("test question text #1",
            new ArrayList<>(Collections.singletonList("right variant")),
            new ArrayList<>(Arrays.asList("wrong variant #1", "wrong variant #2")));

    private Question testQuestion2 = new Question("test question text 2",
            Arrays.asList("right variant #1", "right variant #2"),
            Arrays.asList("wrong variant #1", "wrong variant #2"));


    @Test
    @WithMockUser(roles = "ADMIN")
    public void Should_ReturnQuestionPage_For_Admin() throws Exception {

        questionRepo.save(testQuestion1);
        Question savedQuestion = questionRepo.save(testQuestion2);


        mockMvc.perform(get("/question/{id}", savedQuestion.getId()))
                .andDo(print())


                .andExpect(status().isOk())
                .andExpect(model().attribute("question", savedQuestion));

    }


    @Test
    @WithMockUser(roles = "ADMIN")
    public void Given_NewQuestionSaved_Should_FindIt() throws Exception {

        String jsonQuestion = objectMapper.writeValueAsString(testQuestion1);


        ResultActions resultActions = mockMvc.perform(post("/api/question").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonQuestion))
                .andDo(print());


        resultActions.andExpect(status().isOk());

        String response = resultActions.andReturn().getResponse().getContentAsString();
        long addedUserId = objectMapper.readValue(response, Question.class).getId();
        Question questionFromDB = questionRepo.findById(addedUserId).orElse(null);

        assertThat(questionFromDB).isNotNull();
        assertThat(questionFromDB.getText()).isEqualTo(testQuestion1.getText());
        assertThat(questionFromDB.getRightVariants()).containsOnlyElementsOf(testQuestion1.getRightVariants());
        assertThat(questionFromDB.getWrongVariants()).containsOnlyElementsOf(testQuestion1.getWrongVariants());
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    public void Given_DBContainsQuestion_Should_FindIt() throws Exception {

        Question savedQuestion = questionRepo.save(testQuestion2);


        MvcResult mvcResult = mockMvc.perform(get("/api/question/{id}", savedQuestion.getId()))
                .andDo(print())


                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertThat(response).isEqualTo(objectMapper.writeValueAsString(savedQuestion));

    }


    @Test
    @WithMockUser
    public void Should_ReturnQuestionList_For_AuthenticatedUser() throws Exception {

        questionRepo.save(testQuestion1);
        questionRepo.save(testQuestion2);


        mockMvc.perform(get("/api/question/all"))
                .andDo(print())


                .andExpect(status().isOk())
                .andExpect(content().string(containsString(testQuestion1.getText())))
                .andExpect(content().string(containsString(testQuestion2.getText())));

    }


    @Test
    @WithMockUser(roles = "ADMIN")
    public void Should_UpdateQuestion() throws Exception {

        Question savedQuestion = questionRepo.save(testQuestion1);
        String oldQuestionText = savedQuestion.getText();
        String newQuestionText = "updated question";
        Question question = new Question(
                newQuestionText, testQuestion1.getRightVariants(), testQuestion1.getWrongVariants()
        );
        question.setId(savedQuestion.getId());
        String jsonQuestion = objectMapper.writeValueAsString(question);


        mockMvc.perform(put("/api/question")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonQuestion))
                .andDo(print())


                .andExpect(status().isOk())
                .andExpect(content().string(containsString("updated question")));

        Question updatedQuestion = questionRepo.findById(savedQuestion.getId()).orElse(null);

        assertThat(updatedQuestion).isNotNull();
        assertThat(updatedQuestion.getText()).isNotEqualTo(oldQuestionText);
        assertThat(updatedQuestion.getText()).isEqualTo(newQuestionText);

    }


    @Test
    @WithMockUser(roles = "ADMIN")
    public void Should_DeleteQuestion_For_AdminUser() throws Exception {

        questionRepo.save(testQuestion1);
        Question savedQuestion = questionRepo.save(testQuestion2);


        mockMvc.perform(delete("/api/question/{id}", savedQuestion.getId())
                .with(csrf()))
                .andDo(print())


                .andExpect(status().isOk());


        Question deletedQuestion = questionRepo.findById(savedQuestion.getId()).orElse(null);
        assertThat(deletedQuestion).isNull();
    }


    @Test
    @WithMockUser
    public void Should_ReturnTrue_When_CorrectAnswersPassed_For_AuthenticatedUser() throws Exception {

        Question savedQuestion = questionRepo.save(testQuestion1);
        String jsonQuestion = objectMapper.writeValueAsString(savedQuestion);


        MvcResult mvcResult = mockMvc.perform(post("/api/question/validate")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonQuestion))
                .andDo(print())


                .andExpect(status().isOk())
                .andReturn();
        MatcherAssert.assertThat(mvcResult.getResponse().getContentAsString(), equalTo("true"));

    }


    @Test
    @WithMockUser
    public void Should_ReturnFalse_When_WrongAnswersPassed_For_AuthenticatedUser() throws Exception {

        Question savedQuestion = questionRepo.save(testQuestion1);
        Question wrongAnswers = new Question(
                savedQuestion.getText(), Collections.singletonList("wrong"), savedQuestion.getWrongVariants()
        );
        wrongAnswers.setId(savedQuestion.getId());

        String jsonQuestion = objectMapper.writeValueAsString(wrongAnswers);


        MvcResult mvcResult = mockMvc.perform(post("/api/question/validate")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonQuestion))
                .andDo(print())


                .andExpect(status().isOk())
                .andReturn();

        MatcherAssert.assertThat(mvcResult.getResponse().getContentAsString(), equalTo("false"));

    }

}
