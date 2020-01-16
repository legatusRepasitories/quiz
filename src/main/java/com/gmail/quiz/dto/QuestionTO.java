package com.gmail.quiz.dto;

import com.gmail.quiz.model.Question;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class QuestionTO {

    private long id;
    private String text;
    private Set<String> variants = new HashSet<>();

    public QuestionTO(Question question) {
        this.id = question.getId();
        this.text = question.getText();
        variants.addAll(question.getWrongVariants());
        variants.addAll(question.getRightVariants());
    }
}
