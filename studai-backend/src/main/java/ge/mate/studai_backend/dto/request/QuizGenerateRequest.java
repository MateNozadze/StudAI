package ge.mate.studai_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizGenerateRequest {
    private long materialId;
    private Integer numberOfQuestions;
}
