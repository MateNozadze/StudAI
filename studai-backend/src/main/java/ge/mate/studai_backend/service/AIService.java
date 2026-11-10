package ge.mate.studai_backend.service;

import ge.mate.studai_backend.dto.response.QuestionResponse;

import java.util.List;

public interface AIService {
    List<QuestionResponse> generateQuizQuestions(String materialContent, int questionCount);
}
