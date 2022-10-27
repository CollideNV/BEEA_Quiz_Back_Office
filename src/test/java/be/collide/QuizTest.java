package be.collide;

import be.collide.quizbackoffice.domain.Difficulty;
import be.collide.quizbackoffice.domain.Quiz;
import be.collide.quizbackoffice.domain.QuizType;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@QuarkusTest
public class QuizTest {

    @Inject()
    DynamoDbClient dynamoDbClient;

    @Inject
    DynamoDbEnhancedClient client;


    @BeforeEach
    void beforeEach() {
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
    void name() {
        Quiz quiz = new Quiz(null, "Test Quiz", "theme", QuizType.POLL, Difficulty.EASY, LocalDateTime.now(), LocalDateTime.now().plusDays(1), Collections.EMPTY_LIST);
        String url = given()
                .log().uri()
                .contentType("application/json")
                .body(quiz)
                .when().post("/quizzes")
                .then().log().all()
                .statusCode(201)
                .extract().headers().get("Location").getValue();

        ValidatableResponse validatableResponse = given()
                .when().get(url)
                .then()
                .statusCode(200);

        assertNotNull(validatableResponse.extract().body().jsonPath().get("id"));
        assertEquals(validatableResponse.extract().body().jsonPath().get("title"), quiz.getTitle());
        assertEquals(validatableResponse.extract().body().jsonPath().get("theme"), quiz.getTheme());
    }
}
