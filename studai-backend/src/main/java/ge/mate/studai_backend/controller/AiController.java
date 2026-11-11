package ge.mate.studai_backend.controller;

import ge.mate.studai_backend.dto.request.QuizGenerateRequest;
import ge.mate.studai_backend.dto.response.QuestionResponse;
import ge.mate.studai_backend.service.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AIService aiService;

    @PostMapping("/generate-quiz")
    public ResponseEntity<List<QuestionResponse>> generateQuizFromAi(@RequestBody QuizGenerateRequest request) {
        List<QuestionResponse> questions = aiService.generateQuizQuestions(
                request.getMaterialContent(),
                request.getSubject(),
                request.getQuestionCount() != null ? request.getQuestionCount() : 5,
                request.getDifficulty()
        );
        return ResponseEntity.ok(questions);
    }
}