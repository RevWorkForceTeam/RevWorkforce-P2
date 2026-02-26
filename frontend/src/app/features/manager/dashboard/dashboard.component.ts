import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { LeaveService } from '../../../core/services/leave.service';
import { EmployeeService } from '../../../core/services/employee.service';
import { AuthService } from '../../../core/services/auth.service';
import { LeaveApplication } from '../../../core/models/leave.model';
import { User } from '../../../core/models/user.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, SidebarComponent, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  pendingLeaves = signal<LeaveApplication[]>([]);
  teamMembers = signal<User[]>([]);
  isLoading = signal(true);
  today = new Date();

  constructor(
    private leaveService: LeaveService,
    private employeeService: EmployeeService,
    public auth: AuthService
  ) {}

  ngOnInit() { this.loadData(); }

  loadData() {
    this.leaveService.getTeamPendingLeaves().subscribe({
      next: d => { this.pendingLeaves.set(d); this.isLoading.set(false); },
      error: () => { this.pendingLeaves.set([]); this.isLoading.set(false); }
    });
    this.employeeService.getMyTeam().subscribe({
      next: d => this.teamMembers.set(d),
      error: () => this.teamMembers.set([])
    });
  }

  approve(id: number) {
    this.leaveService.approveLeave(id, '').subscribe({
      next: () => this.pendingLeaves.update(l => l.filter(x => x.id !== id)),
      error: () => alert('Failed. Try again.')
    });
  }

  reject(id: number) {
    const comment = prompt('Reason for rejection (required):');
    if (!comment?.trim()) return;
    this.leaveService.rejectLeave(id, comment).subscribe({
      next: () => this.pendingLeaves.update(l => l.filter(x => x.id !== id)),
      error: () => alert('Failed. Try again.')
    });
  }

  getInitials(name: string | undefined): string {
    if (!name) return '?';
    return name.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2);
  }

  getAvatarColor(i: number): string {
    const colors = ['linear-gradient(135deg,#3b82f6,#1d4ed8)', 'linear-gradient(135deg,#10b981,#059669)',
      'linear-gradient(135deg,#8b5cf6,#6d28d9)', 'linear-gradient(135deg,#f59e0b,#d97706)',
      'linear-gradient(135deg,#ec4899,#be185d)', 'linear-gradient(135deg,#14b8a6,#0d9488)'];
    return colors[i % colors.length];
  }

  getStars(rating: number): string { return '★'.repeat(rating) + '☆'.repeat(5 - rating); }
}
