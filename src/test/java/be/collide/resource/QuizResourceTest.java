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
        Question q1 = new Question(null, "Is this a perfect question?", Difficulty.EASY, 15, List.of(new Answer(null, "Yes", true), new Answer(null, "No", false)));
        Quiz quiz = new Quiz(null, "Test Quiz", "theme", QuizType.POLL, Difficulty.EASY, LocalDateTime.now(), LocalDateTime.now().plusDays(1), List.of(q1));
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


        // Add A Question the the Quiz
        quizBeforeUpdate.getQuestions().add(new Question(null, "Is this a good question?", Difficulty.EASY, 15, List.of(new Answer(null, "Yes", true), new Answer(null, "No", false))));


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
