package ge.mate.studai_backend.service.impl;

import ge.mate.studai_backend.domain.Material;
import ge.mate.studai_backend.domain.Question;
import ge.mate.studai_backend.domain.Quiz;
import ge.mate.studai_backend.dto.request.QuizGenerateRequest;
import ge.mate.studai_backend.dto.request.SubmitQuizRequest;
import ge.mate.studai_backend.dto.response.QuestionResponse;
import ge.mate.studai_backend.dto.response.AttemptResponse;
import ge.mate.studai_backend.dto.response.QuizResponse;
import ge.mate.studai_backend.dto.response.QuizResultResponse;
import ge.mate.studai_backend.repository.MaterialRepository;
import ge.mate.studai_backend.domain.QuizAttempt;
import ge.mate.studai_backend.domain.User;
import ge.mate.studai_backend.repository.QuizAttemptRepository;
import ge.mate.studai_backend.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import ge.mate.studai_backend.repository.QuizRepository;
import ge.mate.studai_backend.service.AIService;
import ge.mate.studai_backend.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final MaterialRepository materialRepository;
    private final AIService aiService;
    private final UserRepository userRepository;
    private final QuizAttemptRepository attemptRepository;

    private User currentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }
    @Override
    public QuizResponse generateAndSaveQuiz(QuizGenerateRequest request) {
        String materialContent;
        Material materialEntity = null;

        if (request.getMaterialId() != null && request.getMaterialId() > 0) {
            materialEntity = materialRepository.findById(request.getMaterialId())
                    .orElseThrow(() -> new RuntimeException("Material not found with id: " + request.getMaterialId()));
            materialContent = materialEntity.getContent();
        } else if (request.getMaterialContent() != null && !request.getMaterialContent().isBlank()) {
            materialContent = request.getMaterialContent(); // აქ ვიყენებთ materialContent-ს
        } else {
            throw new RuntimeException("Either materialId or materialContent must be provided");
        }

        List<QuestionResponse> generatedQuestions = aiService.generateQuizQuestions(
                materialContent,
                request.getSubject(),
                request.getQuestionCount() != null ? request.getQuestionCount() : 5,
                request.getDifficulty() != null ? request.getDifficulty() : "medium"
        );

        Quiz quiz = new Quiz();
        quiz.setUser(currentUser());
        quiz.setDifficulty(request.getDifficulty() != null ? request.getDifficulty() : "medium");
        if (materialEntity != null) {
            quiz.setTitle("Quiz for: " + materialEntity.getTitle());
            quiz.setMaterial(materialEntity);
        } else {
            quiz.setTitle(request.getSubject() != null && !request.getSubject().isBlank()
                    ? request.getSubject() : "Generated Custom Quiz");
        }

        List<Question> questions = generatedQuestions.stream().map(dto -> {
            Question q = new Question();
            q.setQuestion(dto.getQuestion());
            q.setOptions(dto.getOptions());
            q.setCorrectAnswer(dto.getCorrectAnswer());
            q.setExplanation(dto.getExplanation());
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

    private AttemptResponse toAttemptDto(QuizAttempt a) {
        int total = a.getQuiz().getQuestions().size();
        double pct = total > 0 ? (double) a.getScore() / total * 100 : 0.0;
        Map<Long, String> answers = a.getAnswers() == null ? Map.of() : new HashMap<>(a.getAnswers());
        return new AttemptResponse(a.getQuiz().getId(), a.getQuiz().getTitle(), a.getScore(), total, pct, a.getCompletedAt(), answers);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttemptResponse> getMyAttempts() {
        return attemptRepository.findByUserIdOrderByCompletedAtDesc(currentUser().getId())
                .stream().map(this::toAttemptDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AttemptResponse getLastAttempt(Long quizId) {
        return attemptRepository.findFirstByUserIdAndQuizIdOrderByCompletedAtDesc(currentUser().getId(), quizId)
                .map(this::toAttemptDto).orElse(null);
    }

    @Override
    public void deleteQuiz(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + id));
        if (quiz.getUser() == null || !quiz.getUser().getId().equals(currentUser().getId())) {
            throw new RuntimeException("Access Denied: not your quiz");
        }
        attemptRepository.deleteByQuizId(id);
        quizRepository.delete(quiz);
    }

    @Override
    @Transactional(readOnly = true)
    public QuizResponse getQuizById(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + id));

        QuizResponse response = new QuizResponse();
        response.setId(quiz.getId());
        response.setTitle(quiz.getTitle());

        List<QuestionResponse> questionResponses = quiz.getQuestions().stream().map(question -> {
            QuestionResponse questionResponse = new QuestionResponse();
            questionResponse.setId(question.getId());
            questionResponse.setQuestion(question.getQuestion());
            questionResponse.setExplanation(question.getExplanation());
            questionResponse.setOptions(question.getOptions());
            questionResponse.setCorrectAnswer(question.getCorrectAnswer());
            return questionResponse;
        }).collect(Collectors.toList());

        response.setQuestions(questionResponses);

        return response;
    }

    @Override
    public List<QuizResponse> getAllQuizzes() {
        List<Quiz> quizzes = quizRepository.findByUserId(currentUser().getId());
        return quizzes.stream().map(quiz -> {
            QuizResponse response = new QuizResponse();
            response.setId(quiz.getId());
            response.setTitle(quiz.getTitle());

            List<QuestionResponse> questionResponses = quiz.getQuestions().stream().map(q->{
                QuestionResponse questionResponse = new QuestionResponse();
                questionResponse.setId(q.getId());
                questionResponse.setQuestion(q.getQuestion());
                questionResponse.setExplanation(q.getExplanation());
                questionResponse.setOptions(q.getOptions());
                questionResponse.setCorrectAnswer(q.getCorrectAnswer());
                return questionResponse;
            }).collect(Collectors.toList());

            response.setQuestions(questionResponses);

            return response;
        }).collect(Collectors.toList());
    }

    @Override
    public QuizResultResponse gradeQuiz(Long id, SubmitQuizRequest request) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + id));

        int totalQuestions = quiz.getQuestions().size();
        int correctCount = 0;

        Map<Long, Boolean> questionResultMap = new HashMap<>();

        for (Question question : quiz.getQuestions()) {
            String studentAnswer = request.getAnswers().get(question.getId());
            boolean isCorrect = question.getCorrectAnswer().equals(studentAnswer);
            if (isCorrect) {
                correctCount++;
            }
            questionResultMap.put(question.getId(), isCorrect);
        }

        double percentage = totalQuestions > 0 ? ((double) correctCount / totalQuestions) * 100 : 0.0;

        QuizAttempt attempt = new QuizAttempt();
        attempt.setUser(currentUser());
        attempt.setQuiz(quiz);
        attempt.setScore(correctCount);
        attempt.setAnswers(request.getAnswers());
        attemptRepository.save(attempt);

        QuizResultResponse response = new QuizResultResponse();
        response.setTotalQuestions(totalQuestions);
        response.setCorrectAnswers(correctCount);
        response.setScorePercentage(percentage);
        response.setDetails(questionResultMap);
        return response;
    }
}