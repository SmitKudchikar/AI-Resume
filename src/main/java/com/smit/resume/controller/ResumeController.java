package com.smit.resume.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.smit.resume.DTO.ResumeResponse;
import com.smit.resume.entity.Resume;
import com.smit.resume.repository.ResumeRepository;
import com.smit.resume.service.geminiService;
import com.smit.resume.service.pdfservice;

@CrossOrigin(origins = "https://ai-resume-frontend-z1ov.onrender.com")
@RestController
@RequestMapping("/resume")
public class ResumeController {

    private final ResumeRepository repository;
    private final geminiService geminiService;
    private final pdfservice pdfservice;

    public ResumeController(
            ResumeRepository repository,
            geminiService geminiService,
            pdfservice pdfservice) {

        this.repository = repository;
        this.geminiService = geminiService;
        this.pdfservice = pdfservice;
    }

    @GetMapping("/all")
    public List<Resume> getAllResumes() {
        return repository.findAll();
    }

    @PostMapping("/upload")
    public ResumeResponse analyzeResume(
            @RequestParam("file") MultipartFile file)
            throws Exception {

        // Extract text from PDF
        String resumeText = pdfservice.extractText(file);

        // Get AI analysis
        String feedback = geminiService.analyzeResume(resumeText);

        // Extract score
        int score = extractScore(feedback);

        // Determine status
        String status = score >= 50
                ? "Qualified"
                : "Improvement Required";

        // Save data in database
        Resume resume = new Resume();
        resume.setResumeText(resumeText);
        resume.setFeedback(feedback);
        resume.setScore(score);
        resume.setStatus(status);
        resume.setUploadedAt(LocalDateTime.now());

        repository.save(resume);

        // Return only required data to frontend
        ResumeResponse response = new ResumeResponse();
        response.setScore(score);
        response.setStatus(status);

        return response;
    }

    private int extractScore(String feedback) {

        Pattern pattern = Pattern.compile(
                "Resume Score:\\s*(\\d+)",
                Pattern.CASE_INSENSITIVE
        );

        Matcher matcher = pattern.matcher(feedback);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }

        return 0;
    }
}