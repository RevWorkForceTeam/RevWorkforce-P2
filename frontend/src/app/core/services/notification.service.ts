import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private api = 'http://localhost:8084/api';
  constructor(private http: HttpClient) {}

  getMyNotifications() { return this.http.get<any>(`${this.api}/notifications/me`).pipe(map((res: any) => res.data || res)); }
  markAsRead(id: number) { return this.http.put<any>(`${this.api}/notifications/${id}/read`, {}).pipe(map((res: any) => res.data || res)); }
  markAllAsRead() { return this.http.put<any>(`${this.api}/notifications/read-all`, {}).pipe(map((res: any) => res.data || res)); }
}
