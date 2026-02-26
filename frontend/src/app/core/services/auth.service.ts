import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private apiUrl = 'http://localhost:8084/api/auth';

  currentUser = signal<any | null>(null);

  constructor(private http: HttpClient, private router: Router) {

    const stored = localStorage.getItem('user');
    if (stored) {
      try {
        this.currentUser.set(JSON.parse(stored));
      } catch {}
    }
  }

  login(credentials: { email: string; password: string }) {

    return this.http.post<any>(`${this.apiUrl}/login`, credentials).pipe(

      tap(response => {

        // ✅ Backend wraps everything inside "data"
        const loginData = response.data;

        localStorage.setItem('token', loginData.token);
        localStorage.setItem('user', JSON.stringify(loginData));

        this.currentUser.set(loginData);
      })
    );
  }

  logout() {
    localStorage.clear();
    this.currentUser.set(null);
    this.router.navigate(['/login']);
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  getRole(): string {
    return this.currentUser()?.role || localStorage.getItem('role') || '';
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }
}