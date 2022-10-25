package be.collide.quizbackoffice.domain;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public class Leaderboard {
    private List<Score> scores;
}
