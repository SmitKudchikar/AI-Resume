package com.smit.resume.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String candidateName;

@Column(columnDefinition = "TEXT")
private String resumeText;

@Column(columnDefinition = "TEXT")
private String feedback;

    private LocalDateTime uploadedAt;

    private Integer score;

    private String status;    

    public Long getId() {
        return id;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getResumeText() {
        return resumeText;
    }

    public void setResumeText(String resumeText) {
        this.resumeText = resumeText;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

public Integer getScore() {
    return score;
}

public void setScore(Integer score) {
    this.score = score;
}

public String getStatus() {
    return status;
}

public void setStatus(String status) {
    this.status = status;
}
}