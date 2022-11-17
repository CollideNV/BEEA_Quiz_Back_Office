package be.collide.quizbackoffice.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuizTest {
    @Test
    void calculateDifficulty_oneEasyQuestion_QuizIsEasy() {
        Question q1 = Question.builder().difficulty(Difficulty.EASY).build();
        Quiz quiz = Quiz.builder().questions(List.of(q1)).build();

        quiz.calculateDifficulty();

        assertEquals(quiz.getDifficulty(), Difficulty.EASY);
    }

    @Test
    void calculateDifficulty_oneMediumQuestion_QuizIsMedium() {
        Question q1 = Question.builder().difficulty(Difficulty.MEDIUM).build();
        Quiz quiz = Quiz.builder().questions(List.of(q1)).build();

        quiz.calculateDifficulty();

        assertEquals(quiz.getDifficulty(), Difficulty.MEDIUM);
    }

    @Test
    void calculateDifficulty_oneHardQuestion_QuizIsHard() {
        Question q1 = Question.builder().difficulty(Difficulty.HARD).build();
        Quiz quiz = Quiz.builder().questions(List.of(q1)).build();

        quiz.calculateDifficulty();

        assertEquals(quiz.getDifficulty(), Difficulty.HARD);
    }


    @Test
    void calculateDifficulty_oneMediumQuestion_oneEasyQuestion_QuizIsEASY() {

        Question q1 = Question.builder().difficulty(Difficulty.MEDIUM).build();
        Question q2 = Question.builder().difficulty(Difficulty.EASY).build();
        Quiz quiz = Quiz.builder().questions(List.of(q1, q2)).build();

        quiz.calculateDifficulty();

        assertEquals(quiz.getDifficulty(), Difficulty.EASY);
    }
}