import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { EmployeeService } from '../../../core/services/employee.service';
import { LeaveService } from '../../../core/services/leave.service';
import { AuthService } from '../../../core/services/auth.service';
import { User } from '../../../core/models/user.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, SidebarComponent, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  employees = signal<User[]>([]);
  allEmployees = signal<User[]>([]);
  isLoading = signal(true);
  
  today = new Date();

  stats = signal({ total: 0, active: 0, inactive: 0, onLeave: 0, departments: 0, openReviews: 0 });

  departments = signal<any[]>([]);

  configs = [
    { icon: 'bi-building', color: 'rgba(59,130,246,0.1)', iconColor: 'var(--info)', name: 'Departments', desc: 'Manage company departments', btn: 'Manage', route: '/admin/departments' },
    { icon: 'bi-briefcase', color: 'rgba(16,185,129,0.1)', iconColor: 'var(--success)', name: 'Designations', desc: 'Manage job titles & roles', btn: 'Manage', route: '/admin/designations' },
    { icon: 'bi-megaphone', color: 'rgba(245,158,11,0.1)', iconColor: 'var(--warning)', name: 'Announcements', desc: 'Create & publish updates', btn: 'Manage', route: '/admin/announcements' },
    { icon: 'bi-calendar3', color: 'rgba(139,92,246,0.1)', iconColor: 'var(--purple)', name: 'Holiday Calendar', desc: 'Add/edit company holidays', btn: 'Manage', route: '/admin/holidays' }
  ];

  constructor(
    private employeeService: EmployeeService,
    private leaveService: LeaveService,
    public auth: AuthService
  ) {}

  ngOnInit() { this.loadData(); }

  loadData() {
    this.employeeService.getAllEmployees().subscribe({
      next: d => {
        this.allEmployees.set(d);
        this.employees.set(d.slice(0, 5));
        this.calculateStats(d);
        this.isLoading.set(false);
      },
      error: () => { this.employees.set([]); this.isLoading.set(false); }
    });
  }

  calculateStats(employees: any[]) {
    const total = employees.length;
    const active = employees.filter(e => e.active).length;
    const inactive = total - active;
    const depts = new Set(employees.map(e => e.departmentName).filter(Boolean));
    
    this.stats.set({
      total,
      active,
      inactive,
      onLeave: 0,
      departments: depts.size,
      openReviews: 0
    });

    // Calculate department counts
    const deptCounts: any = {};
    employees.forEach(e => {
      if (e.departmentName) {
        deptCounts[e.departmentName] = (deptCounts[e.departmentName] || 0) + 1;
      }
    });

    const deptIcons: any = {
      'Engineering': 'bi-code-slash',
      'Human Resources': 'bi-people',
      'Finance': 'bi-currency-dollar',
      'Marketing': 'bi-graph-up',
      'Operations': 'bi-gear'
    };

    const deptColors: any = {
      'Engineering': { bg: 'rgba(59,130,246,0.1)', text: 'var(--info)' },
      'Human Resources': { bg: 'rgba(236,72,153,0.1)', text: 'var(--pink)' },
      'Finance': { bg: 'rgba(245,158,11,0.1)', text: 'var(--warning)' },
      'Marketing': { bg: 'rgba(20,184,166,0.1)', text: 'var(--teal)' },
      'Operations': { bg: 'rgba(139,92,246,0.1)', text: 'var(--purple)' }
    };

    this.departments.set(
      Object.entries(deptCounts).map(([name, count]) => ({
        name,
        count,
        icon: deptIcons[name] || 'bi-building',
        color: deptColors[name]?.bg || 'rgba(100,100,100,0.1)',
        textColor: deptColors[name]?.text || 'var(--text-main)'
      }))
    );
  }

  deactivate(id: number) {
    if (!confirm('Deactivate this employee?')) return;
    this.employeeService.deactivateEmployee(id).subscribe({
      next: () => this.employees.update(list => list.map(e => e.id === id ? { ...e, status: 'INACTIVE' as const } : e)),
      error: () => alert('Failed. Try again.')
    });
  }

  reactivate(id: number) {
    this.employeeService.reactivateEmployee(id).subscribe({
      next: () => this.employees.update(list => list.map(e => e.id === id ? { ...e, status: 'ACTIVE' as const } : e)),
      error: () => alert('Failed. Try again.')
    });
  }

  getInitials(name: string | undefined): string {
    if (!name) return '?';
    return name.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2);
  }

  getAvatarColor(i: number): string {
    const colors = ['linear-gradient(135deg,#3b82f6,#1d4ed8)', 'linear-gradient(135deg,#10b981,#059669)',
      'linear-gradient(135deg,#ec4899,#be185d)', 'linear-gradient(135deg,#6b7280,#374151)',
      'linear-gradient(135deg,#f59e0b,#d97706)'];
    return colors[i % colors.length];
  }
}
