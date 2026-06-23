package com.smit.resume.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;

import java.util.List;
import java.util.Map;


@Service
public class geminiService {

@Value("${openai.api.key}")
private String apiKey;

@Value("${openai.model}")
private String model;

String url = "https://api.openai.com/v1/chat/completions";
    @PostConstruct
    public void checkKey() {
        System.out.println("API Key = [" + apiKey + "]");
    }        

    private final static RestTemplate restTemplate = new RestTemplate();

    public String analyzeResume(String resumeText) {

        System.out.println("API KEY = " + apiKey);

        String prompt = """
Analyze this resume and provide:

1. Resume Score (0-100)
2. Strengths
3. Missing Skills
4. Improvement Suggestions

Format:

Resume Score: <score>

Strengths:
- point

Missing Skills:
- point

Improvement Suggestions:
- point

Resume:
""" + resumeText;
String url = "https://api.groq.com/openai/v1/chat/completions";

Map<String, Object> requestBody = Map.of(
        "model", "llama-3.3-70b-versatile",
        "messages", List.of(
                Map.of(
                        "role", "user",
                        "content", prompt
                )
        )
);

HttpHeaders headers = new HttpHeaders();
headers.setContentType(MediaType.APPLICATION_JSON);
headers.setBearerAuth(apiKey);

HttpEntity<Map<String, Object>> entity =
        new HttpEntity<>(requestBody, headers);

Map response =
        restTemplate.postForObject(url, entity, Map.class);

        try {
List<Map<String, Object>> choices =
        (List<Map<String, Object>>) response.get("choices");

Map<String, Object> message =
        (Map<String, Object>) choices.get(0).get("message");

return message.get("content").toString();

        } catch (Exception e) {

        System.out.println("Gemini Error: " + e.getMessage());

        return "Gemini API Error: " + e.getMessage();
    }
    }
}