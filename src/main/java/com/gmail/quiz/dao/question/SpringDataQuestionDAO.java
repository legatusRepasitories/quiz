package com.gmail.quiz.dao.question;

import com.gmail.quiz.dto.QuestionTO;
import com.gmail.quiz.model.Question;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Repository("questionSpringData")
@Slf4j
public class SpringDataQuestionDAO implements QuestionDAO {

    private QuestionRepo questionRepo;

    @Autowired
    public SpringDataQuestionDAO(QuestionRepo repo) {
        this.questionRepo = repo;
    }

    @Override
    public Question createQuestion(Question question) {
        log.info("createQuestion executed");
        log.debug("question : {}", question);

        return questionRepo.save(question);
    }

    @Override
    public Question getQuestionById(long id) {
        log.info("getQuestionById executed");
        log.debug("id : {}", id);

        return questionRepo.findById(id).orElse(null);
    }

    @Override
    public Set<Question> getAllQuestions() {
        log.info("getAllQuestions executed");

        return StreamSupport.stream(questionRepo.findAll().spliterator(), false)
                .collect(Collectors.toSet());
    }

    @Override
    public Question updateQuestion(Question updatedQuestion) {
        log.info("updateQuestion executed");
        log.debug("updated question : {}", updatedQuestion);

        return questionRepo.save(updatedQuestion);
    }

    @Override
    public void deleteQuestionById(long id) {
        log.info("deleteQuestionById executed");
        log.debug("id : {}", id);

        questionRepo.deleteById(id);
    }
}
