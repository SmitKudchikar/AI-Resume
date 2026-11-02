
import { Routes } from '@angular/router';

import { Dashboard } from './components/dashboard/dashboard';
import { ResumeForm } from './components/resume-form/resume-form';
import { Assessment } from './components/assessment/assessment';
import { History } from './history/history';

export const routes: Routes = [
  { path: '', component: Dashboard },
  { path: 'resume-form', component: ResumeForm },
  { path: 'assessment', component: Assessment },
  { path: 'history', component: History },
  { path: '**', redirectTo: '' }
];
