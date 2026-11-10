package ge.mate.studai_backend.service;

import ge.mate.studai_backend.dto.request.QuizGenerateRequest;
import ge.mate.studai_backend.dto.response.QuizResponse;

public interface QuizService {
    QuizResponse generateAndSaveQuiz(QuizGenerateRequest request);
    QuizResponse getQuizById(Long id);
}
