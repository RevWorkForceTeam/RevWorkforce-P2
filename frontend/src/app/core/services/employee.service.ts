import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs/operators';
import { User } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class EmployeeService {
  private api = 'http://localhost:8084/api';
  constructor(private http: HttpClient) {}

  // Employee self-service
  getMyProfile()                { return this.http.get<any>(`${this.api}/users/me`).pipe(map((res: any) => res.data || res)); }
  updateMyProfile(data: any)    { return this.http.put<any>(`${this.api}/users/me`, data).pipe(map((res: any) => res.data || res)); }
  changeMyPassword(currentPassword: string, newPassword: string) { 
    return this.http.put<any>(`${this.api}/users/me/change-password`, { currentPassword, newPassword }).pipe(map((res: any) => res.data || res)); 
  }

  // Admin operations
  getAllEmployees()              { return this.http.get<any>(`${this.api}/users`).pipe(map((res: any) => res.data || res)); }
  getEmployeeById(id: number)   { return this.http.get<any>(`${this.api}/users/${id}`).pipe(map((res: any) => res.data || res)); }
  getUserById(id: number)       { return this.http.get<any>(`${this.api}/users/${id}`).pipe(map((res: any) => res.data || res)); }
  addEmployee(data: Partial<User>) { return this.http.post<any>(`${this.api}/users`, data).pipe(map((res: any) => res.data || res)); }
  updateEmployee(id: number, data: Partial<User>) { return this.http.put<any>(`${this.api}/users/${id}`, data).pipe(map((res: any) => res.data || res)); }
  assignManager(userId: number, managerId: number) { return this.http.put<any>(`${this.api}/users/assign-manager`, { userId, managerId }).pipe(map((res: any) => res.data || res)); }
  deactivateEmployee(id: number) { return this.http.put<any>(`${this.api}/users/${id}/deactivate`, {}).pipe(map((res: any) => res.data || res)); }
  reactivateEmployee(id: number) { return this.http.put<any>(`${this.api}/users/${id}/reactivate`, {}).pipe(map((res: any) => res.data || res)); }
  getMyTeam()                   { return this.http.get<any>(`${this.api}/users/manager/${this.getCurrentUserId()}`).pipe(map((res: any) => res.data || res)); }
  
  // Departments
  getDepartments()              { return this.http.get<any>(`${this.api}/departments`).pipe(map((res: any) => res.data || res)); }
  addDepartment(data: any)      { return this.http.post<any>(`${this.api}/departments`, data).pipe(map((res: any) => res.data || res)); }
  updateDepartment(id: number, data: any) { return this.http.put<any>(`${this.api}/departments/${id}`, data).pipe(map((res: any) => res.data || res)); }
  deleteDepartment(id: number)  { return this.http.delete<any>(`${this.api}/departments/${id}`).pipe(map((res: any) => res.data || res)); }
  
  // Designations
  getDesignations()             { return this.http.get<any>(`${this.api}/designations`).pipe(map((res: any) => res.data || res)); }
  addDesignation(data: any)     { return this.http.post<any>(`${this.api}/designations`, data).pipe(map((res: any) => res.data || res)); }
  updateDesignation(id: number, data: any) { return this.http.put<any>(`${this.api}/designations/${id}`, data).pipe(map((res: any) => res.data || res)); }
  deleteDesignation(id: number) { return this.http.delete<any>(`${this.api}/designations/${id}`).pipe(map((res: any) => res.data || res)); }
  
  // Holidays
  getHolidays()                 { return this.http.get<any>(`${this.api}/leaves/holidays`).pipe(map((res: any) => res.data || res)); }
  addHoliday(data: any)         { return this.http.post<any>(`${this.api}/leaves/holidays`, data).pipe(map((res: any) => res.data || res)); }
  deleteHoliday(id: number)     { return this.http.delete<any>(`${this.api}/leaves/holidays/${id}`).pipe(map((res: any) => res.data || res)); }
  
  private getCurrentUserId(): number {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user).id : 0;
  }
}
