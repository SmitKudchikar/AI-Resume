
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import {
  ResumeService,
  ResumeResponse
} from '../services/resume';

@Component({
  selector: 'app-history',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './history.html',
  styleUrl: './history.css',
})
export class History implements OnInit {

  resumes: ResumeResponse[] = [];

  searchTerm = '';
  selectedStatus = 'All';

  isLoading = false;
  errorMessage = '';

  constructor(private resumeService: ResumeService) {}

  ngOnInit(): void {
    this.loadResumes();
  }

  // Fetch resume records from Spring Boot
  loadResumes(): void {

    this.isLoading = true;
    this.errorMessage = '';

    this.resumeService.getAllResumes().subscribe({

      next: (response) => {
        this.resumes = response;
        this.isLoading = false;
      },

      error: (error) => {
        console.error('Error fetching resumes:', error);

        this.errorMessage =
          'Unable to load resume history. Please check your backend connection.';

        this.isLoading = false;
      }

    });
  }

  // Total candidates
  get totalCandidates(): number {
    return this.resumes.length;
  }

  // Qualified candidates (score >= 75)
  get qualifiedCandidates(): number {
    return this.resumes.filter(
      resume => resume.score !== null && resume.score >= 70
    ).length;
  }

  // Not qualified candidates (score < 75)
  get notQualifiedCandidates(): number {
    return this.resumes.filter(
      resume => resume.score !== null && resume.score < 75
    ).length;
  }

  // Determine candidate status
  getStatus(score: number | null): string {

    if (score === null) {
      return 'Pending';
    }

    return score >= 75 ? 'Qualified' : 'Not Qualified';
  }

  // Search and filter candidates
  get filteredResumes(): ResumeResponse[] {

    return this.resumes.filter(resume => {

      const name = resume.candidateName || 'Unknown Candidate';

      const matchesSearch = name
        .toLowerCase()
        .includes(this.searchTerm.toLowerCase());

      const status = this.getStatus(resume.score);

      const matchesStatus =
        this.selectedStatus === 'All' ||
        status === this.selectedStatus;

      return matchesSearch && matchesStatus;

    });

  }

}
