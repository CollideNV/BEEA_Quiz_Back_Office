package be.collide.quizbackoffice.util.converter;

import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeToStringTypeConverter implements AttributeConverter<LocalDateTime> {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;

    @Override
    public AttributeValue transformFrom(LocalDateTime input) {
        return AttributeValue.fromS(input.format(dateTimeFormatter));
    }

    @Override
    public LocalDateTime transformTo(AttributeValue input) {
        return LocalDateTime.parse(input.s(), dateTimeFormatter);
    }

    @Override
    public EnhancedType<LocalDateTime> type() {
        return EnhancedType.of(LocalDateTime.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.S;
    }
}
