package ge.mate.studai_backend.dto.request;

import java.util.Map;

public class SubmitQuizRequest {
    private Map<Long, String> answers;

    public Map<Long, String> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<Long, String> answers) {
        this.answers = answers;
    }
}
