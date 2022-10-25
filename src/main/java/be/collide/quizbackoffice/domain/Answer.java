package be.collide.quizbackoffice.domain;

import lombok.*;

import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public class Answer {
    private UUID id;
    private UUID questionId;
    private String answer;
}
