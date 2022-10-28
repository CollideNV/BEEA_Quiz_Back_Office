package be.collide.quizbackoffice.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuizTest {
    @Test
    void calculateDifficulty_oneEasyQuestion_QuizIsEasy() {
        Quiz quiz = new Quiz();
        Question q1 = new Question();
        q1.setDifficulty(Difficulty.EASY);
        quiz.setQuestions(List.of(q1));

        quiz.calculateDifficulty();

        assertEquals(quiz.getDifficulty(), Difficulty.EASY);
    }

    @Test
    void calculateDifficulty_oneMediumQuestion_QuizIsMedium() {
        Quiz quiz = new Quiz();
        Question q1 = new Question();
        q1.setDifficulty(Difficulty.MEDIUM);
        quiz.setQuestions(List.of(q1));

        quiz.calculateDifficulty();

        assertEquals(quiz.getDifficulty(), Difficulty.MEDIUM);
    }

    @Test
    void calculateDifficulty_oneHardQuestion_QuizIsHard() {
        Quiz quiz = new Quiz();
        Question q1 = new Question();
        q1.setDifficulty(Difficulty.HARD);
        quiz.setQuestions(List.of(q1));

        quiz.calculateDifficulty();

        assertEquals(quiz.getDifficulty(), Difficulty.HARD);
    }


    @Test
    void calculateDifficulty_oneMediumQuestion_oneEasyQuestion_QuizIsEASY() {
        Quiz quiz = new Quiz();
        Question q1 = new Question();
        q1.setDifficulty(Difficulty.MEDIUM);
        Question q2 = new Question();
        q2.setDifficulty(Difficulty.EASY);
        quiz.setQuestions(List.of(q1, q2));

        quiz.calculateDifficulty();

        assertEquals(quiz.getDifficulty(), Difficulty.EASY);
    }
}