package com.gmail.quiz.dao.question;

import com.gmail.quiz.model.Question;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;

public interface QuestionRepo extends CrudRepository<Question, Long> {

}
