import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { LeaveService } from '../../../core/services/leave.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-apply-leave',
  standalone: true,
  imports: [CommonModule, FormsModule, SidebarComponent],
  templateUrl: './apply-leave.component.html',
  styleUrl: './apply-leave.component.css'
})
export class ApplyLeaveComponent implements OnInit {
  leaveTypes = signal<any[]>([]);
  isLoading = signal(false);
  successMsg = signal('');
  errorMsg = signal('');

  leaveData = {
    leaveTypeId: null as number | null,
    fromDate: '',
    toDate: '',
    reason: ''
  };

  constructor(
    private leaveService: LeaveService,
    public auth: AuthService,
    public router: Router
  ) {}

  ngOnInit() {
    this.leaveService.getLeaveTypes().subscribe({
      next: (types: any[]) => this.leaveTypes.set(types),
      error: () => this.errorMsg.set('Failed to load leave types')
    });
  }

  onSubmit() {
    if (!this.leaveData.leaveTypeId || !this.leaveData.fromDate || !this.leaveData.toDate || !this.leaveData.reason) {
      this.errorMsg.set('Please fill all fields');
      return;
    }

    this.isLoading.set(true);
    this.errorMsg.set('');
    this.successMsg.set('');

    this.leaveService.applyLeave(this.leaveData).subscribe({
      next: () => {
        this.isLoading.set(false);
        this.successMsg.set('Leave applied successfully!');
        setTimeout(() => this.router.navigate(['/' + this.getRole().toLowerCase() + '/my-leaves']), 1500);
      },
      error: (err) => {
        this.isLoading.set(false);
        this.errorMsg.set(err.error?.message || 'Failed to apply leave');
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
