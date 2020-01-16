package com.gmail.quiz.controller;

import com.gmail.quiz.model.Question;
import com.gmail.quiz.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Controller
@Slf4j
public class QuestionController {

    private QuestionService questionService;

    @Autowired
    public QuestionController(QuestionService service) {
        this.questionService = service;
    }

    @GetMapping(path = "/newQuestion")
    public String questionForm(Model model) {
        log.info("questionForm, GET");

        Question question = new Question();
        log.debug("question returned by service {}", question);

        model.addAttribute("question", question);
        return "question/question";
    }

    @GetMapping(path = "/questions")
    public String getAllQuestions() {
        log.info("getAllQuestions ,GET");

        return "question/questions";
    }

    @GetMapping(path = "/question/{id}")
    public String getQuestionById(@PathVariable("id") long id, Model model) {
        log.info("getQuestionById, GET");
        log.debug("id: {}", id);

        val question = questionService.getQuestionById(id);
        log.debug("question returned by service: {}", question);

        if (question == null) {
            log.info("question == null");

            return "errors/wrongArgument";
        }

        model.addAttribute("question", question);
        return "question/question";
    }

}
