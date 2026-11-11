package ge.mate.studai_backend.controller;


import ge.mate.studai_backend.domain.Quiz;
import ge.mate.studai_backend.dto.request.QuizGenerateRequest;
import ge.mate.studai_backend.dto.request.SubmitQuizRequest;
import ge.mate.studai_backend.dto.response.QuizResponse;
import ge.mate.studai_backend.dto.response.QuizResultResponse;
import ge.mate.studai_backend.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @PostMapping("/generate")
    public ResponseEntity<QuizResponse> generateQuiz(@RequestBody QuizGenerateRequest request) {
        QuizResponse response = quizService.generateAndSaveQuiz(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuizResponse> getQuizById(@PathVariable Long id) {
        QuizResponse quizResponse = quizService.getQuizById(id);
        return ResponseEntity.ok(quizResponse);
    }

    @GetMapping("/all")
    public ResponseEntity<List<QuizResponse>> getAllQuizzes() {
        List<QuizResponse> quizResponses = quizService.getAllQuizzes();
        return ResponseEntity.ok(quizResponses);
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<QuizResultResponse> submitQuiz(
            @PathVariable Long id,
            @RequestBody SubmitQuizRequest request) {

        QuizResultResponse result = quizService.gradeQuiz(id, request);
        return ResponseEntity.ok(result);
    }

}
