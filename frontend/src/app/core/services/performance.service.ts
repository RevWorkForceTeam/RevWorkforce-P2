import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class PerformanceService {
  private api = 'http://localhost:8084/api';
  constructor(private http: HttpClient) {}

  // Goals
  getMyGoals() { return this.http.get<any>(`${this.api}/performance/goals/me`).pipe(map((res: any) => res.data || res)); }
  createGoal(data: any) { return this.http.post<any>(`${this.api}/performance/goals`, data).pipe(map((res: any) => res.data || res)); }
  updateGoalProgress(id: number, progress: number) { return this.http.put<any>(`${this.api}/performance/goals/progress`, { goalId: id, progress }).pipe(map((res: any) => res.data || res)); }
  deleteGoal(id: number) { return this.http.delete<any>(`${this.api}/performance/goals/${id}`).pipe(map((res: any) => res.data || res)); }

  // Reviews
  getMyReviews() { return this.http.get<any>(`${this.api}/performance/reviews/me`).pipe(map((res: any) => res.data || res)); }
  createReview(data: any) { return this.http.post<any>(`${this.api}/performance/reviews/self`, data).pipe(map((res: any) => res.data || res)); }
  submitReview(reviewId: number) { return this.http.put<any>(`${this.api}/performance/reviews/submit`, { reviewId }).pipe(map((res: any) => res.data || res)); }
  
  // Manager
  getTeamGoals() { return this.http.get<any>(`${this.api}/performance/goals/team`).pipe(map((res: any) => res.data || res)); }
  addGoalComment(goalId: number, comment: string) { return this.http.put<any>(`${this.api}/performance/goals/comment`, { goalId, comment }).pipe(map((res: any) => res.data || res)); }
  getTeamReviews() { return this.http.get<any>(`${this.api}/performance/reviews/team`).pipe(map((res: any) => res.data || res)); }
  provideFeedback(id: number, data: any) { return this.http.put<any>(`${this.api}/performance/reviews/${id}/feedback`, data).pipe(map((res: any) => res.data || res)); }
}
