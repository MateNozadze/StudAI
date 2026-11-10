package ge.mate.studai_backend.service.impl;

import ge.mate.studai_backend.domain.Material;
import ge.mate.studai_backend.domain.Question;
import ge.mate.studai_backend.domain.Quiz;
import ge.mate.studai_backend.dto.request.QuizGenerateRequest;
import ge.mate.studai_backend.dto.response.QuestionResponse;
import ge.mate.studai_backend.dto.response.QuizResponse;
import ge.mate.studai_backend.repository.MaterialRepository;
import ge.mate.studai_backend.repository.QuizRepository;
import ge.mate.studai_backend.service.AIService;
import ge.mate.studai_backend.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final MaterialRepository materialRepository;
    private final AIService aiService;

    @Override
    public QuizResponse generateAndSaveQuiz(QuizGenerateRequest request) {
        Material material = materialRepository.findById(request.getMaterialId())
                .orElseThrow(()->new RuntimeException("Material not found"));

        List<QuestionResponse> generatedQuestions = aiService.generateQuizQuestions(
                material.getContent(),
                request.getNumberOfQuestions()
        );

        Quiz quiz = new Quiz();
        quiz.setTitle("Quiz for: "+material.getTitle());
        quiz.setMaterial(material);

        List<Question> questions = generatedQuestions.stream().map(dto -> {
            Question q = new Question();
            q.setQuestion(dto.getText());
            q.setOptions(dto.getOptions());
            q.setCorrectAnswer(dto.getCorrectAnswer());
            q.setQuiz(quiz);
            return q;
        }).collect(Collectors.toList());

        quiz.setQuestions(questions);

        Quiz savedQuiz = quizRepository.save(quiz);

        QuizResponse response = new QuizResponse();
        response.setId(savedQuiz.getId());
        response.setTitle(savedQuiz.getTitle());
        response.setQuestions(generatedQuestions);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public QuizResponse getQuizById(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id:" + id));

        QuizResponse response = new QuizResponse();
        response.setId(quiz.getId());
        response.setTitle(quiz.getTitle());

        List<QuestionResponse>questionResponses = quiz.getQuestions().stream().map(question ->{
            QuestionResponse questionResponse = new QuestionResponse();
            questionResponse.setId(question.getId());
            questionResponse.setText(question.getQuestion());
            questionResponse.setOptions(question.getOptions());
            questionResponse.setCorrectAnswer(question.getCorrectAnswer());
            return questionResponse;
        }).collect(Collectors.toList());

        response.setQuestions(questionResponses);

        return response;

    }
}
