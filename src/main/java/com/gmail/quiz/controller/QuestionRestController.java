package com.gmail.quiz.controller;

import com.gmail.quiz.dto.QuestionTO;
import com.gmail.quiz.model.Question;
import com.gmail.quiz.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/question")
@Slf4j
public class QuestionRestController {

    private QuestionService questionService;

    @Autowired
    public QuestionRestController(QuestionService service) {
        this.questionService = service;
    }

    @PostMapping
    public QuestionTO insertQuestion(@RequestBody Question question) {
        log.info("insertQuestion, POST");

        val createdQuestion = questionService.createQuestion(question);
        log.debug("created question : {}", createdQuestion);

        return new QuestionTO(createdQuestion);
    }

    @GetMapping(path = "/{id}")
    public Question getQuestionById(@PathVariable("id") long id) {
        log.info("getQuestionById, GET");
        log.debug("id : {}", id);

        val question = questionService.getQuestionById(id);
        log.debug("question returned by service : {}", question);

        return question;
    }

    @GetMapping(path = "/all")
    public Set<QuestionTO> getQuestions() {
        log.info("getQuestions, GET");

        return questionService.getAllQuestions();
    }


    @PutMapping()
    public QuestionTO updateQuestion(@RequestBody  Question changedQuestion) {
        log.info("updateQuestionById, PUT");
        log.debug("changedQuestion : {}", changedQuestion);

        val updatedQuestion = questionService.updateQuestion(changedQuestion);

        return new QuestionTO(updatedQuestion);
    }

    @DeleteMapping(path = "/{id}")
    public void deleteQuestionById(@PathVariable("id") long id) {
        log.info("deleteQuestionById, DELETE");
        log.debug("id : {}", id);

        questionService.deleteQuestionById(id);
    }

    @PostMapping(path = "/validate")
    public boolean validate(@RequestBody Question questionToValidate) {
        log.info("validate");
        log.debug("questionToValidate : {}", questionToValidate);

        Question question = questionService.getQuestionById(questionToValidate.getId());
        log.debug("question : {}", question);

        if (questionToValidate.getRightVariants().size() != question.getRightVariants().size()) {
            log.info("questions rightVariants are unequal");

            return false;
        }
        val isCorrect = questionToValidate.getRightVariants().containsAll(question.getRightVariants());
        log.debug("isCorrect : {}", isCorrect);

        return isCorrect;
    }
}
