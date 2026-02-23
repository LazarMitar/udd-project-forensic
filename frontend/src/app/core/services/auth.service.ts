import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { CurrentUser } from '../models/current-user.model';
import { Observable, tap, catchError, of, switchMap, map } from 'rxjs';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  role?: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly api = `${environment.apiUrl}/api/auth`;
  private currentUser = signal<CurrentUser | null>(null);
  private checked = signal(false);

  readonly user = this.currentUser.asReadonly();
  readonly isAuthenticated = computed(() => !!this.currentUser());
  readonly isAuthChecked = this.checked.asReadonly();

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  
  login(credentials: LoginRequest): Observable<void> {
  return this.http.post<void>(`${this.api}/login`, credentials, { withCredentials: true }).pipe(
    switchMap(() => this.fetchCurrentUser()),
    map(() => void 0)
  );
}

  register(data: RegisterRequest): Observable<void> {
    return this.http.post<void>(`${this.api}/register`, data, { withCredentials: true });
  }

  logout(): void {
    this.http.post(`${this.api}/logout`, {}, { withCredentials: true }).subscribe({
      next: () => {},
      error: () => {}
    });
    this.currentUser.set(null);
    this.router.navigate(['/login']);
  }

  fetchCurrentUser(): Observable<CurrentUser | null> {
    return this.http.get<CurrentUser>(`${this.api}/me`, { withCredentials: true }).pipe(
      tap(user => {
        this.currentUser.set(user);
        this.checked.set(true);
      }),
      catchError(() => {
        this.currentUser.set(null);
        this.checked.set(true);
        return of(null);
      })
    );
  }

  ensureAuthChecked(): Observable<CurrentUser | null> {
    if (this.checked()) {
      return of(this.currentUser());
    }
    return this.fetchCurrentUser();
  }

  setUser(user: CurrentUser | null): void {
    this.currentUser.set(user);
  }
}
