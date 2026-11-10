package ge.mate.studai_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizResponse {
    private Long id;
    private String title;
    private List<QuestionResponse> questions;
}
