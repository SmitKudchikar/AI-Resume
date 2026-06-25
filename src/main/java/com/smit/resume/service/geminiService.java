package com.smit.resume.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private static final RestTemplate restTemplate = new RestTemplate();

    @PostConstruct
    public void checkKey() {
        System.out.println("Gemini API Key Loaded: " + (apiKey != null && !apiKey.isEmpty()));
    }

    public String analyzeResume(String resumeText) {

        try {

            String prompt = """
                    Analyze this resume and provide:

                    1. Resume Score (0-100)
                    2. Strengths
                    3. Missing Skills
                    4. Improvement Suggestions

                    Format exactly like:

                    Resume Score: <score>

                    Strengths:
                    - point

                    Missing Skills:
                    - point

                    Improvement Suggestions:
                    - point

                    Resume:
                    """ + resumeText;

            String url =
                    "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key="
                            + apiKey;

            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of(
                                    "parts", List.of(
                                            Map.of("text", prompt)
                                    )
                            )
                    )
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(requestBody, headers);

            Map<String, Object> response =
                    restTemplate.postForObject(url, entity, Map.class);

            if (response == null) {
                return "Error: Empty response from Gemini.";
            }

            List<Map<String, Object>> candidates =
                    (List<Map<String, Object>>) response.get("candidates");

            if (candidates == null || candidates.isEmpty()) {
                return "Error: No candidates returned by Gemini.";
            }

            Map<String, Object> content =
                    (Map<String, Object>) candidates.get(0).get("content");

            List<Map<String, Object>> parts =
                    (List<Map<String, Object>>) content.get("parts");

            if (parts == null || parts.isEmpty()) {
                return "Error: No content returned by Gemini.";
            }

            return parts.get(0).get("text").toString();

        } catch (Exception e) {

            e.printStackTrace();

            return "Gemini API Error: " + e.getMessage();
        }
    }
}
