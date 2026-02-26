import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { EmployeeService } from '../../../core/services/employee.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, SidebarComponent],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  profile = signal<any>(null);
  isLoading = signal(true);
  isEditing = signal(false);
  showPasswordSection = signal(false);
  isChangingPassword = signal(false);
  editData: any = {};
  passwordData = { currentPassword: '', newPassword: '', confirmPassword: '' };
  successMsg = signal('');
  errorMsg = signal('');
  passwordError = signal('');

  constructor(
    private employeeService: EmployeeService,
    public auth: AuthService
  ) {}

  ngOnInit() {
    // Use the new /me endpoint
    this.employeeService.getMyProfile().subscribe({
      next: (data) => {
        this.profile.set(data);
        this.editData = {...data};
        this.isLoading.set(false);
      },
      error: () => {
        // Fallback to stored user data
        const stored = localStorage.getItem('user');
        const user = stored ? JSON.parse(stored) : this.auth.currentUser();
        this.profile.set(user);
        this.editData = {...user};
        this.isLoading.set(false);
      }
    });
  }

  toggleEdit() {
    this.isEditing.update(v => !v);
    if (!this.isEditing()) {
      this.editData = {...this.profile()};
    }
  }

  saveProfile() {
    this.successMsg.set('');
    this.errorMsg.set('');

    this.employeeService.updateMyProfile(this.editData).subscribe({
      next: (data) => {
        this.profile.set(data);
        this.isEditing.set(false);
        this.successMsg.set('Profile updated successfully!');
        setTimeout(() => this.successMsg.set(''), 3000);
      },
      error: (err) => {
        this.errorMsg.set(err.error?.message || 'Failed to update profile');
        setTimeout(() => this.errorMsg.set(''), 3000);
      }
    });
  }

  changePassword() {
    this.passwordError.set('');
    this.successMsg.set('');

    if (!this.passwordData.currentPassword || !this.passwordData.newPassword || !this.passwordData.confirmPassword) {
      this.passwordError.set('All password fields are required');
      return;
    }

    if (this.passwordData.newPassword !== this.passwordData.confirmPassword) {
      this.passwordError.set('New passwords do not match');
      return;
    }

    if (this.passwordData.newPassword.length < 6) {
      this.passwordError.set('Password must be at least 6 characters');
      return;
    }

    this.isChangingPassword.set(true);

    this.employeeService.changeMyPassword(this.passwordData.currentPassword, this.passwordData.newPassword).subscribe({
      next: () => {
        this.isChangingPassword.set(false);
        this.successMsg.set('Password changed successfully!');
        this.showPasswordSection.set(false);
        this.passwordData = { currentPassword: '', newPassword: '', confirmPassword: '' };
        setTimeout(() => this.successMsg.set(''), 3000);
      },
      error: (err) => {
        this.isChangingPassword.set(false);
        this.passwordError.set(err.error?.message || 'Failed to change password');
      }
    });
  }

  getInitials(name: string | undefined): string {
    if (!name) return '?';
    return name.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2);
  }

  getRole(): 'EMPLOYEE' | 'MANAGER' | 'ADMIN' {
    const role = this.auth.getRole();
    return (role === 'EMPLOYEE' || role === 'MANAGER' || role === 'ADMIN') ? role : 'EMPLOYEE';
  }
}
