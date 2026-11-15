package ge.mate.studai_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttemptResponse {
    private Long quizId;
    private String quizTitle;
    private int score;
    private int totalQuestions;
    private double percentage;
    private LocalDateTime completedAt;
    private Map<Long, String> answers;
}
