package com.gmail.quiz.dao.question;

import com.gmail.quiz.PostgresqlContainerImpl;
import com.gmail.quiz.model.Question;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(SpringDataQuestionDAO.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class SpringDataQuestionDAOTest {

    @Autowired
    private SpringDataQuestionDAO repo;

    @Container
    public static PostgreSQLContainer<PostgresqlContainerImpl> postgreSQLContainer = PostgresqlContainerImpl.getInstance();

    private Question question1;
    private Question question3;
    private Question question2;

    @BeforeEach
    public void beforeEach() {
        question1 = new Question("question #1 text",
                new ArrayList<>(Arrays.asList("rv #1", "rv #2")),
                new ArrayList<>(Arrays.asList("wv #1", "wv #2")));
        question2 = new Question("question #2 text",
                new ArrayList<>(Arrays.asList("rv #1", "rv #2")),
                Collections.singletonList("wv #1"));
        question3 = new Question("question #3 text",
                Collections.singletonList("rv #1"),
                Collections.singletonList("wv #1"));
    }


    @Test
    public void Should_FindQuestion_When_Saved() {

        Question savedQuestion = repo.createQuestion(question1);


        Question question = repo.getQuestionById(savedQuestion.getId());


        assertThat(question).isNotNull();
        assertThat(question).isEqualTo(question1);

    }

    @Test
    public void Should_NotFindQuestion_When_NotSaved() {

        Question question = repo.getQuestionById(question1.getId());


        assertThat(question).isNull();

    }

    @Test
    public void Should_FindAllQuestion() {

        repo.createQuestion(question1);
        repo.createQuestion(question2);
        repo.createQuestion(question3);


        Set<Question> questions = new HashSet<>(repo.getAllQuestions());


        assertThat(questions).isNotNull();
        assertThat(questions).hasSize(3);
        assertThat(questions).containsOnly(question1, question2, question3);

    }

    @Test
    public void Should_UpdateQuestion() {

        Question savedQuestion = repo.createQuestion(question1);
        savedQuestion.setRightVariants(new ArrayList<>(Collections.singleton("updated rw")));


        Question updatedQuestion = repo.updateQuestion(savedQuestion);


        assertThat(updatedQuestion).isNotNull();
        assertThat(updatedQuestion.getRightVariants()).hasSize(1);
        assertThat(updatedQuestion.getRightVariants()).containsExactly("updated rw");

    }

    @Test
    public void Should_DeleteQuestion() {

        Question savedQuestion = repo.createQuestion(question1);
        repo.createQuestion(question2);
        repo.createQuestion(question3);


        repo.deleteQuestionById(savedQuestion.getId());
        Question deletedQuestion = repo.getQuestionById(savedQuestion.getId());
        Set<Question> questions = new HashSet<>(repo.getAllQuestions());


        assertThat(deletedQuestion).isNull();
        assertThat(questions).isNotNull();
        assertThat(questions).hasSize(2);
        assertThat(questions).containsOnly(question2, question3);

    }

}