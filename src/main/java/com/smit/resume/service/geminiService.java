package com.smit.resume.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class geminiService {

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.url}")
    private String groqApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostConstruct
    public void checkKey() {
        System.out.println("Groq API Key Loaded: " +
                (apiKey != null && !apiKey.isBlank()));
    }

    public String analyzeResume(String resumeText) {

        try {

String prompt = """
        Analyze this resume.

        Return ONLY in this format:

        Resume Score: <score>

        Missing Skills:
        - skill1
        - skill2
        - skill3
        - skill4
        - skill5

        Rules:
        - Score must be between 0 and 100.
        - Include only important missing skills.
        - Do not add strengths.
        - Do not add explanations.
        - Follow the format exactly.

        Resume:
        """ + resumeText;

            String url = groqApiUrl;

            Map<String, Object> requestBody = Map.of(
                    "model", "llama-3.3-70b-versatile",
                    "messages", List.of(
                            Map.of(
                                    "role", "user",
                                    "content", prompt
                            )
                    ),
                    "temperature", 0.3,
                    "max_tokens", 1000
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> responseEntity =
                    restTemplate.exchange(
                            url,
                            HttpMethod.POST,
                            entity,
                            Map.class
                    );

            Map<String, Object> response = responseEntity.getBody();

            if (response == null) {
                return "Error: Empty response from Groq.";
            }

            List<Map<String, Object>> choices =
                    (List<Map<String, Object>>) response.get("choices");

            if (choices == null || choices.isEmpty()) {
                return "Error: No choices returned by Groq.";
            }

            Map<String, Object> message =
                    (Map<String, Object>) choices.get(0).get("message");

            if (message == null || !message.containsKey("content")) {
                return "Error: No content returned by Groq.";
            }

            return message.get("content").toString();

            

        } catch (Exception e) {

            e.printStackTrace();

            return "Groq API Error: " + e.getMessage();
        }
    }
}
