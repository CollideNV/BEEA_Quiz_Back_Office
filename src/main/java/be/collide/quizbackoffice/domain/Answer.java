package be.collide.quizbackoffice.domain;

import be.collide.quizbackoffice.util.converter.UUIDToStringConverter;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.UUID;


@Jacksonized
@Getter
@Setter
@Builder
@DynamoDbBean
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Answer {

    @Builder.Default
    private UUID id = UUID.randomUUID();
    private String answer;
    private boolean correct;


    @DynamoDbPartitionKey
    @DynamoDbConvertedBy(UUIDToStringConverter.class)
    public UUID getId() {
        return id;
    }
}
