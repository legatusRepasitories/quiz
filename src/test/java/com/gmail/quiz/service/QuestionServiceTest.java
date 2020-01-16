package com.gmail.quiz.service;

import com.gmail.quiz.dao.question.QuestionDAO;
import com.gmail.quiz.dto.QuestionTO;
import com.gmail.quiz.model.Question;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @Mock
    private QuestionDAO dao;

    @InjectMocks
    private QuestionService questionService;

    private Question question;

    @BeforeEach
    void beforeEach() {
        question = new Question("Text of question for tests",
                Collections.singletonList("Right Answer"),
                Arrays.asList("Wrong Answer #1", "Wrong Answer #2"));
    }

    @Test
    public void Should_SaveNewQuestion() {

        when(dao.createQuestion(question)).thenReturn(question);


        Question returnedQuestion = questionService.createQuestion(this.question);


        assertThat(returnedQuestion).isNotNull();
        assertThat(returnedQuestion).isEqualTo(question);

        verify(dao).createQuestion(any());

    }

    @Test
    public void Should_ReturnQuestion_When_IdExist() {


        when(dao.getQuestionById(anyLong())).thenReturn(question);


        Question returnedQuestion = questionService.getQuestionById(1L);


        assertThat(returnedQuestion).isNotNull();

        verify(dao).getQuestionById(anyLong());

    }

    @Test
    public void Should_ReturnNull_When_IdNotExist() {

        when(dao.getQuestionById(anyLong())).thenReturn(null);


        Question returnedQuestion = questionService.getQuestionById(1L);


        assertThat(returnedQuestion).isNull();

        verify(dao).getQuestionById(anyLong());

    }

    @Test
    public void Should_ReturnQuestionList() {

        when(dao.getAllQuestions()).thenReturn(Set.of(question));


        Set<QuestionTO> questions = questionService.getAllQuestions();


        assertThat(questions).isNotNull();
        assertThat(questions).isNotEmpty();

        verify(dao).getAllQuestions();

    }

    @Test
    public void Should_UpdateQuestion() {

        when(dao.updateQuestion(any())).thenReturn(question);


        Question updatedQuestion = questionService.updateQuestion(question);


        assertThat(updatedQuestion).isNotNull();

        verify(dao).updateQuestion(any());

    }

    @Test
    public void Should_DeleteQuestionById() {

        questionService.deleteQuestionById(1L);


        verify(dao).deleteQuestionById(anyLong());

    }


}
