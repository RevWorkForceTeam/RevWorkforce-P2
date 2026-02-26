import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { EmployeeService } from '../../../core/services/employee.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-holidays',
  standalone: true,
  imports: [CommonModule, FormsModule, SidebarComponent],
  templateUrl: './holidays.component.html',
  styleUrls: ['./holidays.component.css']
})
export class HolidaysComponent implements OnInit {
  holidays = signal<any[]>([]);
  isLoading = signal(true);
  showModal = signal(false);
  currentHoliday: any = { name: '', holidayDate: '' };

  constructor(
    private employeeService: EmployeeService,
    public auth: AuthService
  ) {}

  ngOnInit() { this.loadHolidays(); }

  loadHolidays() {
    this.employeeService.getHolidays().subscribe({
      next: (data) => { this.holidays.set(data); this.isLoading.set(false); },
      error: () => { this.holidays.set([]); this.isLoading.set(false); }
    });
  }

  openAddModal() {
    this.currentHoliday = { name: '', holidayDate: '' };
    this.showModal.set(true);
  }

  saveHoliday() {
    this.employeeService.addHoliday(this.currentHoliday).subscribe({
      next: () => { this.showModal.set(false); this.loadHolidays(); },
      error: () => alert('Failed to add holiday')
    });
  }

  deleteHoliday(id: number) {
    if (!confirm('Delete this holiday?')) return;
    this.employeeService.deleteHoliday(id).subscribe({
      next: () => this.loadHolidays(),
      error: () => alert('Failed to delete holiday')
    });
  }

  getInitials(name: string | undefined): string {
    if (!name) return '?';
    return name.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2);
  }

  formatDate(date: string): string {
    return new Date(date).toLocaleDateString('en-US', { weekday: 'short', month: 'short', day: 'numeric', year: 'numeric' });
  }

  getMonthYear(date: string): string {
    return new Date(date).toLocaleDateString('en-US', { month: 'long', year: 'numeric' });
  }
}
