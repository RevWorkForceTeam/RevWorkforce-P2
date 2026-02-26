import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  selectedRole = signal<'EMPLOYEE' | 'MANAGER' | 'ADMIN'>('EMPLOYEE');
  showPassword = signal(false);
  isLoading = signal(false);
  errorMsg = signal('');

  credentials = { email: '', password: '' };

  constructor(private auth: AuthService, private router: Router) {
    // Clear any existing session when accessing login page
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    localStorage.removeItem('name');
    localStorage.removeItem('user');
  }

  setRole(role: 'EMPLOYEE' | 'MANAGER' | 'ADMIN') {
    this.selectedRole.set(role);
    this.errorMsg.set('');
  }

  togglePassword() {
    this.showPassword.update(v => !v);
  }

  onSubmit() {

    if (!this.credentials.email || !this.credentials.password) {
      this.errorMsg.set('Please fill in all fields.');
      return;
    }

    this.isLoading.set(true);
    this.errorMsg.set('');

    this.auth.login({ ...this.credentials }).subscribe({

      next: (res: any) => {
        this.isLoading.set(false);
        const loginData = res.data || res;
        const userRole = loginData.role;
        const selectedTab = this.selectedRole();

        // Validate role-based tab access
        if (selectedTab === 'MANAGER' && userRole !== 'MANAGER') {
          this.errorMsg.set('Only Managers can login through Manager tab');
          return;
        }
        if (selectedTab === 'ADMIN' && userRole !== 'ADMIN') {
          this.errorMsg.set('Only Admins can login through Admin tab');
          return;
        }
        // Employee tab allows all roles

        const email = loginData.email || this.credentials.email;
        
        const userData = {
          id: loginData.id || null,
          token: loginData.token,
          role: loginData.role,
          name: loginData.name,
          email: email
        };
        
        localStorage.setItem('token', loginData.token);
        localStorage.setItem('role', loginData.role);
        localStorage.setItem('name', loginData.name);
        localStorage.setItem('user', JSON.stringify(userData));
        
        const map: Record<string, string> = {
          EMPLOYEE: '/employee',
          MANAGER: '/manager',
          ADMIN: '/admin'
        };
        
        this.router.navigate([map[userData.role] || '/login']);
      },

      error: (err) => {
        this.isLoading.set(false);
        this.errorMsg.set('Invalid Login Credentials');
      }

    });
  }
}