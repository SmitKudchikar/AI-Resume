
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { finalize } from 'rxjs/operators';

import {
  ResumeService,
  ResumeResponse
} from '../../services/resume';

@Component({
  selector: 'app-resume-form',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './resume-form.html',
  styleUrl: './resume-form.css',
})
export class ResumeForm {

  selectedFile: File | null = null;
  errorMessage = '';
  isLoading = false;

  analysisResult: ResumeResponse | null = null;

  constructor(private resumeService: ResumeService) {}

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];

    this.errorMessage = '';
    this.analysisResult = null;
    this.selectedFile = null;

    if (!file) return;

    if (file.type !== 'application/pdf') {
      this.errorMessage = 'Please select a PDF file.';
      input.value = '';
      return;
    }

    if (file.size > 5 * 1024 * 1024) {
      this.errorMessage = 'File size must be less than 5 MB.';
      input.value = '';
      return;
    }

    this.selectedFile = file;
  }

  analyzeResume(): void {

    if (!this.selectedFile || this.isLoading) return;

    this.isLoading = true;
    this.errorMessage = '';
    this.analysisResult = null;

    this.resumeService.analyzeResume(this.selectedFile)
      .pipe(
        finalize(() => {
          this.isLoading = false;
        })
      )
      .subscribe({

        next: (response: ResumeResponse) => {

          console.log('Backend response:', response);

          if (response.score === null || response.status === null) {
            this.errorMessage = 'Score or eligibility status is missing from the response.';
            return;
          }

          this.analysisResult = response;

          console.log('Frontend result:', this.analysisResult);
        },

        error: (error) => {

          console.error('Resume analysis failed:', error);

          this.errorMessage = error.error?.message ||
            'Unable to analyze resume. Please check your backend connection.';
        }

      });
  }
}
