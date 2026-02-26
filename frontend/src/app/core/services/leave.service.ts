import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs/operators';
import { LeaveApplication, LeaveBalance } from '../models/leave.model';

@Injectable({ providedIn: 'root' })
export class LeaveService {
  private api = 'http://localhost:8084/api';
  constructor(private http: HttpClient) {}

  // Employee
  getMyBalances()       { return this.http.get<any>(`${this.api}/leaves/balance/me`).pipe(map((res: any) => res.data || res)); }
  getMyLeaves()         { return this.http.get<any>(`${this.api}/leaves/me`).pipe(map((res: any) => res.data || res)); }
  applyLeave(data: any) { return this.http.post<any>(`${this.api}/leaves`, data).pipe(map((res: any) => res.data || res)); }
  cancelLeave(id: number) { return this.http.put<any>(`${this.api}/leaves/${id}/cancel`, {}).pipe(map((res: any) => res.data || res)); }

  // Manager
  getTeamLeaves()        { return this.http.get<any>(`${this.api}/leaves/manager/pending`).pipe(map((res: any) => res.data || res)); }
  getTeamPendingLeaves() { return this.http.get<any>(`${this.api}/leaves/manager/pending`).pipe(map((res: any) => res.data || res)); }
  getTeamCalendar()      { return this.http.get<any>(`${this.api}/leaves/team-calendar`).pipe(map((res: any) => res.data || res)); }
  getEmployeeLeaveBalance(employeeId: number) { return this.http.get<any>(`${this.api}/leaves/balance/employee/${employeeId}`).pipe(map((res: any) => res.data || res)); }
  approveLeave(id: number, comments: string) { return this.http.put<any>(`${this.api}/leaves/${id}/approve`, { comments }).pipe(map((res: any) => res.data || res)); }
  rejectLeave(id: number, comments: string)  { return this.http.put<any>(`${this.api}/leaves/${id}/reject?comment=${encodeURIComponent(comments)}`, {}).pipe(map((res: any) => res.data || res)); }

  // Holidays
  getAllHolidays()       { return this.http.get<any>(`${this.api}/leaves/holidays`).pipe(map((res: any) => res.data || res)); }
  
  // Leave Types
  getLeaveTypes()        { return this.http.get<any>(`${this.api}/leaves/types`).pipe(map((res: any) => res.data || res)); }
  
  // Admin - Assign Leave Balance
  assignLeaveBalance(employeeId: number, leaveTypeId: number, totalQuota: number) {
    return this.http.post<any>(`${this.api}/leave-balances/assign?employeeId=${employeeId}&leaveTypeId=${leaveTypeId}&totalQuota=${totalQuota}`, {}).pipe(map((res: any) => res.data || res));
  }

  // Admin - Adjust Leave Balance
  adjustLeaveBalance(employeeId: number, leaveTypeId: number, adjustment: number, reason: string) {
    return this.http.put<any>(`${this.api}/leave-balances/adjust?employeeId=${employeeId}&leaveTypeId=${leaveTypeId}&adjustment=${adjustment}&reason=${encodeURIComponent(reason)}`, {}).pipe(map((res: any) => res.data || res));
  }

  // Admin - Get All Leave Balances
  getAllLeaveBalances() {
    return this.http.get<any>(`${this.api}/leave-balances/all`).pipe(map((res: any) => res.data || res));
  }

  // Admin - Get All Leaves
  getAllLeaves() { return this.http.get<any>(`${this.api}/leaves/all`).pipe(map((res: any) => res.data || res)); }

  // Admin - Get All Employees
  getAllEmployees() { return this.http.get<any>(`${this.api}/users`).pipe(map((res: any) => res.data || res)); }

  // Admin - Reports
  getDepartmentWiseReport() { return this.http.get<any>(`${this.api}/leaves/reports/department`).pipe(map((res: any) => res.data || res)); }
  getEmployeeWiseReport() { return this.http.get<any>(`${this.api}/leaves/reports/employee`).pipe(map((res: any) => res.data || res)); }
}
