package be.collide.quizbackoffice.service;

import be.collide.quizbackoffice.domain.Quiz;
import be.collide.quizbackoffice.exception.ResourceNotFoundException;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
@Slf4j
public class QuizServiceImpl implements QuizService {

    private DynamoDbTable<Quiz> quizTable;


    @Inject
    DynamoDbEnhancedClient client;

    @PostConstruct
    void postConstruct() {
        quizTable = client.table("Quiz", TableSchema.fromBean(Quiz.class));

        try {
            quizTable.createTable(builder -> builder
                    .provisionedThroughput(b -> b
                            .readCapacityUnits(10L)
                            .writeCapacityUnits(10L)
                            .build())
            );
        } catch (ResourceInUseException e) {
            log.debug("Dit not recreate Quiz table as it already exists");
        }
    }

    @Override
    public List<Quiz> findAll() {

        return quizTable.scan().items().stream().collect(Collectors.toList());
    }

    @Override
    public void create(Quiz quiz) {
        quiz.calculateDifficulty();
        quizTable.putItem(quiz);
    }

    @Override
    public Quiz get(UUID id) {
        return Optional.ofNullable(quizTable.getItem(Key.builder().partitionValue(id.toString()).build()))
                .orElseThrow(() -> new ResourceNotFoundException("Quiz with id %s not found!".formatted(id)));
    }

    @Override
    public void update(UUID id, Quiz quiz) {
        quiz.calculateDifficulty();
        quizTable.updateItem(quiz);
    }

    @Override
    public void delete(UUID id) {
        Quiz quiz = this.get(id);
        quizTable.deleteItem(quiz);
    }
}
