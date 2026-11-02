import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ResumeService, ResumeResponse } from '../../services/resume';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit {

  totalResumes = 0;
  qualifiedCandidates = 0;
  pendingAssessments = 0;
  averageScore = 0;

  latestResumeScore: number | null = null;
  latestEligibility = 'NO DATA';
  assessmentAccess = 'LOCKED';

  constructor(private resumeService: ResumeService) {}

  ngOnInit(): void {
    this.loadDashboardData();
  }

  loadDashboardData(): void {

    this.resumeService.getAllResumes().subscribe({

      next: (resumes: ResumeResponse[]) => {

        console.log('All Resume Records:', resumes);

        this.totalResumes = resumes.length;

        this.qualifiedCandidates = resumes.filter(
          resume => resume.status === 'QUALIFIED'
        ).length;

        const scoredResumes = resumes.filter(
          resume => resume.score !== null
        );

        this.averageScore = scoredResumes.length
          ? Math.round(
              scoredResumes.reduce(
                (total, resume) => total + (resume.score ?? 0), 0
              ) / scoredResumes.length
            )
          : 0;

        // Find latest uploaded resume
        const latestResume = [...resumes].sort((a, b) =>
          new Date(b.uploadedAt ?? 0).getTime() -
          new Date(a.uploadedAt ?? 0).getTime()
        )[0];

        console.log('Latest Resume:', latestResume);
        console.log('Latest Score:', latestResume?.score);
        console.log('Latest Status:', latestResume?.status);

        if (latestResume) {

          this.latestResumeScore = latestResume.score;
          this.latestEligibility = latestResume.status ?? 'PENDING';

          this.assessmentAccess =
            latestResume.status === 'QUALIFIED'
              ? 'AVAILABLE'
              : 'LOCKED';

        } else {

          this.latestResumeScore = null;
          this.latestEligibility = 'NO DATA';
          this.assessmentAccess = 'LOCKED';

        }
      },

      error: (error) => {
        console.error('Unable to load dashboard data:', error);
      }

    });
  }
}
