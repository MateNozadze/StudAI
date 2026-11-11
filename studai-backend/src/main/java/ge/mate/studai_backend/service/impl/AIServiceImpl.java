package ge.mate.studai_backend.service.impl;

import ge.mate.studai_backend.dto.response.QuestionResponse;
import ge.mate.studai_backend.service.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class AIServiceImpl implements AIService {

    private final RestTemplate restTemplate;

    @Value("${ai.service.url:http://localhost:8000}")
    private String aiServiceUrl;

    @Override
    public List<QuestionResponse> generateQuizQuestions(String materialContent,String subject, int questionCount, String difficulty) {
        String url = aiServiceUrl + "/api/ai/generate-quiz";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("material", materialContent); // პითონი ელოდება "material"-ს
        requestBody.put("subject", subject);           // პითონი ელოდება "subject"-ს
        requestBody.put("questionCount", questionCount);
        requestBody.put("difficulty", difficulty != null ? difficulty : "medium");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            List<QuestionResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<List<QuestionResponse>>() {}
            ).getBody();

            return response != null ? response : List.of();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to generate quiz from AI service: " + e.getMessage());
        }
    }
}