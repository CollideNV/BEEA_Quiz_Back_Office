package be.collide.quizbackoffice.domain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public class Score {

    private Long userId;
    private int score;


}
