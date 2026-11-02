
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ResumeResponse {
  id: number;
  candidateName: string | null;
  resumeText: string;
  feedback: string;
  score: number | null;
  status: string | null;
  uploadedAt: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class ResumeService {

  private apiUrl = 'http://localhost:8082/resume';

  constructor(private http: HttpClient) {}

  analyzeResume(file: File): Observable<ResumeResponse> {

    const formData = new FormData();

    formData.append('file', file);

    return this.http.post<ResumeResponse>(
      `${this.apiUrl}/analyze`,
      formData
    );
  }

  getAllResumes(): Observable<ResumeResponse[]> {
    return this.http.get<ResumeResponse[]>(
      `${this.apiUrl}/all`
    );
  }
}
