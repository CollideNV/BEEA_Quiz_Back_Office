package be.collide.quizbackoffice.domain;

import be.collide.quizbackoffice.util.converter.LocalDateTimeToStringTypeConverter;
import be.collide.quizbackoffice.util.converter.UUIDToStringConverter;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Jacksonized
@Getter
@Setter
@Builder(toBuilder = true)
@DynamoDbBean
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Quiz {
    @Builder.Default
    private UUID id = UUID.randomUUID();
    private String title;
    private String theme;
    private QuizType type;
    private Difficulty difficulty;
    // TODO: Convert to date
    private LocalDateTime beginning;
    // TODO: Convert to date
    private LocalDateTime ending;


    @Builder.Default
    private List<Question> questions = new ArrayList<>();

    @DynamoDbPartitionKey
    @DynamoDbConvertedBy(UUIDToStringConverter.class)
    public UUID getId() {
        return id;
    }
    //private byte[] image;

    @DynamoDbConvertedBy(LocalDateTimeToStringTypeConverter.class)
    public LocalDateTime getBeginning() {
        return beginning;
    }

    @DynamoDbConvertedBy(LocalDateTimeToStringTypeConverter.class)
    public LocalDateTime getEnding() {
        return ending;
    }

    public void calculateDifficulty() {
        long easyWeight = 1;
        long mediumWeight = 2;
        long hardWeight = 3;
        long easyCount = 0;
        long mediumCount = 0;
        long hardCount = 0;

        for (Question question : this.questions) {
            switch (question.getDifficulty()) {
                case EASY -> easyCount++;
                case MEDIUM -> mediumCount++;
                case HARD -> hardCount++;
            }
        }
        if (this.questions.size() > 0) {
            long averageWeightedDifficulty = (easyCount * easyWeight + mediumCount * mediumWeight + hardCount * hardWeight) / this.questions.size();
            if (averageWeightedDifficulty < 1.75) {
                this.setDifficulty(Difficulty.EASY);
            } else if (averageWeightedDifficulty < 2.25) {
                this.setDifficulty(Difficulty.MEDIUM);
            } else {
                this.setDifficulty(Difficulty.HARD);
            }
        }
    }
}
