package be.collide.resource;

import be.collide.quizbackoffice.domain.*;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.ValidatableResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


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
        Quiz quiz = new Quiz(null, "Test Quiz", "theme", QuizType.POLL, Difficulty.EASY, LocalDateTime.now(), LocalDateTime.now().plusDays(1), Collections.EMPTY_LIST);
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
        ValidatableResponse validatableResponse = given()
                .when().get(url)
                .then()
                .statusCode(200);

        assertNotNull(validatableResponse.extract().body().jsonPath().get("id"));
        assertEquals(validatableResponse.extract().body().jsonPath().get("title"), quiz.getTitle());
        assertEquals(validatableResponse.extract().body().jsonPath().get("theme"), quiz.getTheme());
        assertEquals(validatableResponse.extract().body().jsonPath().get("questions"), quiz.getQuestions());


        // Add A Question the the Quiz
        Question question = new Question(null, "Is this a good question?", Difficulty.EASY, 15, List.of(new Answer(UUID.randomUUID(), "Yes", true), new Answer(UUID.randomUUID(), "No", false)));

        Quiz updatedQuiz = new Quiz(UUID.fromString(validatableResponse.extract().body().jsonPath().get("id")), "Test Quiz", "theme", QuizType.POLL, Difficulty.EASY, LocalDateTime.now(), LocalDateTime.now().plusDays(1), List.of(question));

        given().body(updatedQuiz)
                .contentType("application/json")
                .when().put(url)
                .then()
                .statusCode(204);



        Quiz retrievedQuizAfterUpdate = given()
                .when().get(url)
                .then()
                .extract().as(Quiz.class);

        Assertions.assertThat(retrievedQuizAfterUpdate.getQuestions()).usingRecursiveComparison().ignoringFields("id").isEqualTo(updatedQuiz.getQuestions());

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
