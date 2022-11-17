package be.collide.resource;

import be.collide.quizbackoffice.domain.*;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


@QuarkusTest
public class QuizResourceTest {

    @Inject
    DynamoDbEnhancedClient client;

    @BeforeEach
    void setUp() {
        DynamoDbTable<Quiz> quizTable = client.table("Quiz", TableSchema.fromBean(Quiz.class));

        quizTable.createTable(builder -> builder
                .provisionedThroughput(b -> b
                        .readCapacityUnits(10L)
                        .writeCapacityUnits(10L)
                        .build())
        );

    }

    @AfterEach
    void tearDown() {
        DynamoDbTable<Quiz> quizTable = client.table("Quiz", TableSchema.fromBean(Quiz.class));

        quizTable.deleteTable();
    }

    @Test
    void createQuiz() {
        // Check List is Empty
        List<Quiz> allQuizes = given()
                .when().get("/quizzes").then().statusCode(200).extract().jsonPath().getList(".", Quiz.class);

        assertEquals(allQuizes.size(), 0);

        // Add A Quiz
        Question.builder()
                .question("Is this a perfect question?")
                .difficulty(Difficulty.EASY)
                .timePerQuestion(15)
                .answers(List.of(Answer.builder().answer("Yes").correct(true).build()
                        , Answer.builder().answer("No").correct(false).build()))
                .build();
        Question q1 = Question.builder()
                .question("Is this a perfect question?")
                .difficulty(Difficulty.EASY)
                .timePerQuestion(15)
                .answers(List.of(Answer.builder().answer("Yes").correct(true).build()
                        , Answer.builder().answer("No").correct(false).build()))
                .build();

        Quiz quiz = Quiz.builder()
                .title("Test Quiz")
                .theme("theme")
                .type(QuizType.POLL)
                .beginning(LocalDateTime.now())
                .ending(LocalDateTime.now().plusDays(1))
                .questions(List.of(q1))
                .build();

        String url = given()
                .contentType("application/json")
                .body(quiz)
                .when().post("/quizzes")
                .then().log().all()
                .statusCode(201)
                .extract().headers().get("Location").getValue();

        // Check List is no longer Empty
        allQuizes = given()
                .when().get("/quizzes").then().statusCode(200).extract().jsonPath().getList(".", Quiz.class);

        assertEquals(allQuizes.size(), 1);

        // Retrieve and verify added Quiz
        Quiz quizBeforeUpdate = given()
                .when().get(url)
                .then()
                .statusCode(200).extract().as(Quiz.class);

        assertThat(quizBeforeUpdate.getId()).isNotNull();


        quizBeforeUpdate.getQuestions().add(Question.builder()
                .question("Is this a good question?")
                .difficulty(Difficulty.EASY)
                .timePerQuestion(15)
                .answers(List.of(Answer.builder().answer("Yes").correct(true).build(), Answer.builder().answer("No").correct(false).build()))
                .build());


        given().body(quizBeforeUpdate)
                .contentType("application/json")
                .when().put(url)
                .then()
                .statusCode(204);


        Quiz retrievedQuizAfterUpdate = given()
                .when().get(url)
                .then()
                .extract().as(Quiz.class);

        assertThat(retrievedQuizAfterUpdate.getQuestions()).usingRecursiveComparison().ignoringFields("id", "answers.id").isEqualTo(quizBeforeUpdate.getQuestions());
        assertThat(retrievedQuizAfterUpdate.getQuestions()).flatExtracting("answers").extracting("id").doesNotContainNull();

        //Delete Quiz
        given().when().delete(url).then().statusCode(204);

        given()
                .when().get(url)
                .then()
                .statusCode(404);


        // Check list is empty again
        allQuizes = given()
                .when().get("/quizzes").then().statusCode(200).extract().jsonPath().getList(".", Quiz.class);

        assertEquals(allQuizes.size(), 0);

    }
}
