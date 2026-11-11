package ge.mate.studai_backend.service;

import ge.mate.studai_backend.dto.request.QuizGenerateRequest;
import ge.mate.studai_backend.dto.request.SubmitQuizRequest;
import ge.mate.studai_backend.dto.response.QuizResponse;
import ge.mate.studai_backend.dto.response.QuizResultResponse;

import java.util.List;

public interface QuizService {
    QuizResponse generateAndSaveQuiz(QuizGenerateRequest request);
    QuizResponse getQuizById(Long id);

    List<QuizResponse> getAllQuizzes();

    QuizResultResponse gradeQuiz(Long id, SubmitQuizRequest request);
}
