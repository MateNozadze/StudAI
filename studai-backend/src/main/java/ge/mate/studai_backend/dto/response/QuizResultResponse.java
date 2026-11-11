package ge.mate.studai_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizResultResponse {

    private int totalQuestions;
    private int correctAnswers;
    private double scorePercentage;
    private Map<Long, Boolean> details;
}
