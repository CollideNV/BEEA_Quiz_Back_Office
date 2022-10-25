package be.collide.quizbackoffice.service;

import be.collide.quizbackoffice.domain.Quiz;

import java.util.List;
import java.util.UUID;

public interface QuizService {

    List<Quiz> findAll();

    Quiz get(UUID id);

    void create(Quiz quiz);

    void update(UUID id, Quiz quiz);

    void delete(UUID id);
}
