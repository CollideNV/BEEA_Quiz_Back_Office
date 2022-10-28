package be.collide.quizbackoffice.domain;

import be.collide.quizbackoffice.util.converter.UUIDToStringConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.UUID;


@Jacksonized
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class Answer {

    private UUID id;
    private String answer;
    private boolean correct;


    @DynamoDbPartitionKey
    @DynamoDbConvertedBy(UUIDToStringConverter.class)
    public UUID getId() {
        return id;
    }
}
