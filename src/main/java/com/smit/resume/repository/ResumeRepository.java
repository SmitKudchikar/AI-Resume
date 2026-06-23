package com.smit.resume.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smit.resume.entity.Resume;

public interface ResumeRepository extends JpaRepository<Resume, Long> {

}