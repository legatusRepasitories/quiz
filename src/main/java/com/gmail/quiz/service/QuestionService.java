package com.gmail.quiz.service;

import com.gmail.quiz.dao.question.QuestionDAO;
import com.gmail.quiz.dto.QuestionTO;
import com.gmail.quiz.model.Question;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@CacheConfig(cacheNames = "question")
@Slf4j
public class QuestionService {
    
    private QuestionDAO questionDAO;

    @Autowired
    public QuestionService(@Qualifier("questionSpringData") QuestionDAO dao) {
        this.questionDAO = dao;
    }

    @CachePut(key = "#question.id")
    @CacheEvict(key = "'all'")
    public Question createQuestion(Question question) {
        log.info("createQuestion executed");
        log.debug("question : {}", question);

        return questionDAO.createQuestion(question);
    }

    @Cacheable(condition="#result != null")
    public Question getQuestionById(long id) {
        log.info("getQuestionById executed");
        log.debug("id : {}", id);

        return questionDAO.getQuestionById(id);
    }


    @Cacheable(key = "'all'")
    public Set<QuestionTO> getAllQuestions() {
        log.info("getAllQuestions executed");

        return questionDAO.getAllQuestions().stream()
                .map(QuestionTO::new)
                .collect(Collectors.toSet());
    }


    @CachePut(key = "#updatedQuestion.id")
    @CacheEvict(key = "'all'")
    public Question updateQuestion(Question updatedQuestion) {
        log.info("updateQuestion executed");
        log.debug("updated question : {}", updatedQuestion);

        return questionDAO.updateQuestion(updatedQuestion);
    }


    @Caching(evict = { @CacheEvict(key = "'all'"),
                       @CacheEvict (key = "#id") })
    public void deleteQuestionById(long id) {
        log.info("deleteQuestionById executed");
        log.debug("id : {}", id);

        questionDAO.deleteQuestionById(id);
    }

}
