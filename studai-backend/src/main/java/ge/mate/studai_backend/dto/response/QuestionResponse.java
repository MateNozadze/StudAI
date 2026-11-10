package ge.mate.studai_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponse {
    private Long id;
    private String text;
    private List<String> options;
    private String correctAnswer;

}
