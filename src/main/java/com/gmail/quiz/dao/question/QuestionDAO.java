package com.gmail.quiz.dao.question;

import com.gmail.quiz.model.Question;

import java.util.Set;

public interface QuestionDAO {

    Question createQuestion(Question question);

    Question getQuestionById(long id);

    Set<Question> getAllQuestions();

    Question updateQuestion(Question updatedQuestion);

    void deleteQuestionById(long id);

}
