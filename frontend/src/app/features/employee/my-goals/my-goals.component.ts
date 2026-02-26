import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { PerformanceService } from '../../../core/services/performance.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-my-goals',
  standalone: true,
  imports: [CommonModule, FormsModule, SidebarComponent],
  templateUrl: './my-goals.component.html',
  styleUrls: ['./my-goals.component.css']
})
export class MyGoalsComponent implements OnInit {
  goals = signal<any[]>([]);
  isLoading = signal(true);
  showAddForm = signal(false);
  newGoal = { title: '', description: '', deadline: '', priority: 'MEDIUM' };

  constructor(
    private performanceService: PerformanceService,
    public auth: AuthService
  ) {}

  ngOnInit() {
    this.loadGoals();
  }

  loadGoals() {
    this.performanceService.getMyGoals().subscribe({
      next: (data) => {
        this.goals.set(data);
        this.isLoading.set(false);
      },
      error: () => {
        this.goals.set([]);
        this.isLoading.set(false);
      }
    });
  }

  addGoal() {
    if (!this.newGoal.title || !this.newGoal.deadline) return;
    
    this.performanceService.createGoal(this.newGoal).subscribe({
      next: () => {
        this.showAddForm.set(false);
        this.newGoal = { title: '', description: '', deadline: '', priority: 'MEDIUM' };
        this.loadGoals();
      },
      error: () => alert('Failed to create goal')
    });
  }

  updateProgress(id: number, progress: number) {
    this.performanceService.updateGoalProgress(id, progress).subscribe({
      next: () => this.goals.update(list => list.map(g => g.id === id ? {...g, progress} : g)),
      error: () => alert('Failed to update progress')
    });
  }

  deleteGoal(id: number) {
    if (!confirm('Delete this goal?')) return;
    
    this.performanceService.deleteGoal(id).subscribe({
      next: () => this.goals.update(list => list.filter(g => g.id !== id)),
      error: () => alert('Failed to delete goal')
    });
  }

  getInitials(name: string | undefined): string {
    if (!name) return '?';
    return name.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2);
  }

  getProgressColor(progress: number): string {
    if (progress >= 75) return '#10b981';
    if (progress >= 50) return '#3b82f6';
    if (progress >= 25) return '#f59e0b';
    return '#ef4444';
  }

  getRole(): 'EMPLOYEE' | 'MANAGER' | 'ADMIN' {
    const role = this.auth.getRole();
    return (role === 'EMPLOYEE' || role === 'MANAGER' || role === 'ADMIN') ? role : 'EMPLOYEE';
  }
}
