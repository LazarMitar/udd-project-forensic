import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { guestGuard } from './core/guards/guest.guard';

export const routes: Routes = [
  {
    path: 'login',
    canActivate: [guestGuard],
    loadComponent: () =>
      import('./features/auth/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'register',
    canActivate: [guestGuard],
    loadComponent: () =>
      import('./features/auth/register/register.component').then(m => m.RegisterComponent)
  },
  {
    path: 'dashboard',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent),
    children: [
      {
        path: '',
        loadComponent: () =>
          import('./features/dashboard/welcome/welcome.component').then(m => m.WelcomeComponent)
      },
      {
        path: 'upload',
        loadComponent: () =>
          import('./features/dashboard/report-upload/report-upload.component').then(m => m.ReportUploadComponent)
      },
      {
        path: 'search',
        loadComponent: () =>
          import('./features/dashboard/search/search.component').then(m => m.SearchComponent)
      },
      {
        path: 'geo',
        loadComponent: () =>
          import('./features/dashboard/geo-search/geo-search.component').then(m => m.GeoSearchComponent)
      }
    ]
  },
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: '**', redirectTo: 'dashboard' }
];
