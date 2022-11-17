package be.collide.quizbackoffice.service;

import be.collide.quizbackoffice.domain.Quiz;
import be.collide.quizbackoffice.exception.ResourceNotFound;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class QuizServiceImpl implements QuizService {

    private DynamoDbTable<Quiz> quizTable;


    @Inject
    DynamoDbEnhancedClient client;

    @PostConstruct
    void postConstruct() {
        quizTable = client.table("Quiz", TableSchema.fromBean(Quiz.class));
    }

    @Override
    public List<Quiz> findAll() {

        return quizTable.scan().items().stream().collect(Collectors.toList());
    }

    @Override
    public void create(Quiz quiz) {


        if (quiz.getId() == null) quiz.setId(UUID.randomUUID());
        quiz.getQuestions().forEach(question -> {
            if (question.getId() == null) question.setId(UUID.randomUUID());
            question.getAnswers().forEach(answer -> {
                if (answer.getId() == null) answer.setId(UUID.randomUUID());
            });
        });

        quiz.calculateDifficulty();
        quizTable.putItem(quiz);
    }

    @Override
    public Quiz get(UUID id) {
        return Optional.ofNullable(quizTable.getItem(Key.builder().partitionValue(id.toString()).build()))
                .orElseThrow(() -> new ResourceNotFound("Quiz with id %s not found!".formatted(id)));
    }

    @Override
    public void update(UUID id, Quiz quiz) {
        Quiz quizToEdit = this.get(id);
        quizToEdit.setTitle(quiz.getTitle());
        quizToEdit.setTheme(quiz.getTheme());
        quizToEdit.setType(quiz.getType());
        quizToEdit.setBeginning(quiz.getBeginning());
        quizToEdit.setEnding(quiz.getEnding());
        quiz.getQuestions().forEach(question -> {
            if (question.getId() == null) question.setId(UUID.randomUUID());
            question.getAnswers().forEach(answer -> {
                if (answer.getId() == null) answer.setId(UUID.randomUUID());
            });
        });
        quizToEdit.setQuestions(quiz.getQuestions());
        quizToEdit.calculateDifficulty();
        quizTable.updateItem(quizToEdit);
    }

    @Override
    public void delete(UUID id) {
        Quiz quiz = this.get(id);
        quizTable.deleteItem(quiz);
    }
}
