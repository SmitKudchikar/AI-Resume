package com.smit.resume.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
public Resume uploadResume(@RequestParam("file") MultipartFile file) throws Exception {

    System.out.println("File received: " + file.getOriginalFilename());

    String resumeText = pdfservice.extractText(file);
    System.out.println("PDF text extracted successfully");

    String feedback = geminiService.analyzeResume(resumeText);
    System.out.println("Gemini response: " + feedback);

    Resume resume = new Resume();
    resume.setResumeText(resumeText);
    resume.setFeedback(feedback);

    Resume saved = repository.save(resume);
    System.out.println("Saved successfully");

    return saved;
}
@PostMapping("/analyze")
public Resume analyzeResume(
        @RequestParam("file") MultipartFile file)
        throws Exception {

    String resumeText = pdfservice.extractText(file);
    String feedback = geminiService.analyzeResume(resumeText);

    Resume resume = new Resume();
    resume.setResumeText(resumeText);
    resume.setFeedback(feedback);

    return repository.save(resume);
}
}

    
