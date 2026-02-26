import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { EmployeeService } from '../../../core/services/employee.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-employees',
  standalone: true,
  imports: [CommonModule, FormsModule, SidebarComponent],
  templateUrl: './employees.component.html',
  styleUrls: ['./employees.component.css']
})
export class EmployeesComponent implements OnInit {
  employees = signal<any[]>([]);
  filteredEmployees = signal<any[]>([]);
  departments = signal<any[]>([]);
  designations = signal<any[]>([]);
  managers = signal<any[]>([]);
  isLoading = signal(true);
  showAddModal = signal(false);
  showEditModal = signal(false);
  showFilters = signal(false);
  showManagerModal = signal(false);
  showPassword = signal(false);
  selectedEmployee: any = null;
  newManagerId: number | undefined = undefined;
  searchTerm = '';
  
  filters = {
    departmentId: undefined as number | undefined,
    designationId: undefined as number | undefined,
    role: '' as string,
    active: '' as string
  };
  
  newEmployee = {
    firstName: '', lastName: '', email: '', password: '', employeeId: '', 
    departmentId: undefined, designationId: undefined, managerId: undefined,
    phone: '', address: '', joiningDate: '', salary: undefined, role: 'EMPLOYEE' as 'EMPLOYEE' | 'MANAGER' | 'ADMIN'
  };

  editEmployee: any = null;

  constructor(
    private employeeService: EmployeeService,
    public auth: AuthService
  ) {}

  ngOnInit() {
    this.loadEmployees();
    this.loadDepartments();
    this.loadDesignations();
    this.loadManagers();
  }

  loadEmployees() {
    this.employeeService.getAllEmployees().subscribe({
      next: (data) => {
        this.employees.set(data);
        this.filteredEmployees.set(data);
        this.isLoading.set(false);
      },
      error: () => {
        this.employees.set([]);
        this.isLoading.set(false);
      }
    });
  }

  loadDepartments() {
    this.employeeService.getDepartments().subscribe({
      next: (data) => this.departments.set(data),
      error: () => this.departments.set([])
    });
  }

  loadDesignations() {
    this.employeeService.getDesignations().subscribe({
      next: (data) => this.designations.set(data),
      error: () => this.designations.set([])
    });
  }

  loadManagers() {
    this.employeeService.getAllEmployees().subscribe({
      next: (data) => {
        const managerList = data.filter((e: any) => e.role === 'MANAGER' && e.active);
        this.managers.set(managerList);
      },
      error: () => this.managers.set([])
    });
  }

  onSearch() {
    this.applyFilters();
  }

  applyFilters() {
    let filtered = this.employees();

    // Search term filter
    const term = this.searchTerm.toLowerCase();
    if (term) {
      filtered = filtered.filter(e =>
        e.name?.toLowerCase().includes(term) ||
        e.email?.toLowerCase().includes(term) ||
        e.employeeId?.toLowerCase().includes(term) ||
        e.departmentName?.toLowerCase().includes(term)
      );
    }

    // Department filter
    if (this.filters.departmentId) {
      filtered = filtered.filter(e => e.departmentId === this.filters.departmentId);
    }

    // Designation filter
    if (this.filters.designationId) {
      filtered = filtered.filter(e => e.designationId === this.filters.designationId);
    }

    // Role filter
    if (this.filters.role) {
      filtered = filtered.filter(e => e.role === this.filters.role);
    }

    // Status filter
    if (this.filters.active !== '') {
      const isActive = this.filters.active === 'true';
      filtered = filtered.filter(e => e.active === isActive);
    }

    this.filteredEmployees.set(filtered);
  }

  clearFilters() {
    this.searchTerm = '';
    this.filters = {
      departmentId: undefined,
      designationId: undefined,
      role: '',
      active: ''
    };
    this.applyFilters();
  }

  addEmployee() {
    this.employeeService.addEmployee(this.newEmployee).subscribe({
      next: () => {
        this.showAddModal.set(false);
        this.loadEmployees();
        this.resetForm();
      },
      error: () => alert('Failed to add employee')
    });
  }

  deactivate(id: number) {
    if (!confirm('Deactivate this employee?')) return;
    this.employeeService.deactivateEmployee(id).subscribe({
      next: () => this.loadEmployees(),
      error: () => alert('Failed to deactivate')
    });
  }

  reactivate(id: number) {
    this.employeeService.reactivateEmployee(id).subscribe({
      next: () => this.loadEmployees(),
      error: () => alert('Failed to reactivate')
    });
  }

  openEditModal(emp: any) {
    this.editEmployee = {
      id: emp.id,
      firstName: emp.name?.split(' ')[0] || '',
      lastName: emp.name?.split(' ').slice(1).join(' ') || '',
      email: emp.email,
      employeeId: emp.employeeId,
      departmentId: emp.departmentId,
      designationId: emp.designationId,
      managerId: emp.managerId,
      phone: emp.phone || '',
      address: emp.address || '',
      joiningDate: emp.joiningDate || '',
      salary: emp.salary,
      role: emp.role
    };
    this.showEditModal.set(true);
  }

  updateEmployee() {
    this.employeeService.updateEmployee(this.editEmployee.id, this.editEmployee).subscribe({
      next: () => {
        this.showEditModal.set(false);
        this.loadEmployees();
        this.editEmployee = null;
      },
      error: () => alert('Failed to update employee')
    });
  }

  openManagerModal(emp: any) {
    this.selectedEmployee = emp;
    this.newManagerId = emp.managerId;
    this.showManagerModal.set(true);
  }

  assignManager() {
    if (!this.newManagerId) {
      alert('Please select a manager');
      return;
    }
    this.employeeService.assignManager(this.selectedEmployee.id, this.newManagerId).subscribe({
      next: () => {
        this.showManagerModal.set(false);
        this.loadEmployees();
        this.selectedEmployee = null;
      },
      error: () => alert('Failed to assign manager')
    });
  }

  resetForm() {
    this.newEmployee = {
      firstName: '', lastName: '', email: '', password: '', employeeId: '',
      departmentId: undefined, designationId: undefined, managerId: undefined,
      phone: '', address: '', joiningDate: '', salary: undefined, role: 'EMPLOYEE' as 'EMPLOYEE' | 'MANAGER' | 'ADMIN'
    };
  }

  getInitials(name: string | undefined): string {
    if (!name) return '?';
    return name.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2);
  }

  togglePassword() {
    this.showPassword.update(v => !v);
  }
}
