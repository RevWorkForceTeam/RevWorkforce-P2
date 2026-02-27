import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { PerformanceService } from '../../../core/services/performance.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-my-reviews',
  standalone: true,
  imports: [CommonModule, FormsModule, SidebarComponent],
  templateUrl: './my-reviews.component.html',
  styleUrls: ['./my-reviews.component.css']
})
export class MyReviewsComponent implements OnInit {
  reviews = signal<any[]>([]);
  isLoading = signal(true);
  showCreateModal = false;
  isSubmitting = false;

  newReview = {
    employeeId: null as number | null,
    year: new Date().getFullYear(),
    deliverables: '',
    accomplishments: '',
    improvements: '',
    selfRating: null as number | null
  };

  constructor(
    private performanceService: PerformanceService,
    public auth: AuthService
  ) {}

  ngOnInit() {
    this.newReview.employeeId = this.auth.currentUser()?.id;
    this.loadReviews();
  }

  loadReviews() {
    this.performanceService.getMyReviews().subscribe({
      next: (data) => {
        this.reviews.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Failed to load reviews:', err);
        this.isLoading.set(false);
      }
    });
  }

  createReview() {
    if (!this.newReview.year || !this.newReview.deliverables || !this.newReview.accomplishments || 
        !this.newReview.improvements || !this.newReview.selfRating) {
      alert('Please fill all fields');
      return;
    }

    this.isSubmitting = true;
    this.performanceService.createReview(this.newReview).subscribe({
      next: () => {
        this.isSubmitting = false;
        this.showCreateModal = false;
        this.resetForm();
        this.loadReviews();
        alert('Review created successfully!');
      },
      error: (err) => {
        this.isSubmitting = false;
        const errorMsg = err.error?.message || 'Failed to create review';
        alert(errorMsg);
      }
    });
  }

  resetForm() {
    this.newReview = {
      employeeId: this.auth.currentUser()?.id,
      year: new Date().getFullYear(),
      deliverables: '',
      accomplishments: '',
      improvements: '',
      selfRating: null
    };
  }

  getInitials(name: string | undefined): string {
    if (!name) return '?';
    return name.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2);
  }

  getStars(rating: number): string {
    return '★'.repeat(rating) + '☆'.repeat(5 - rating);
  }

  submitReview(reviewId: number) {
    if (!confirm('Submit this review for manager feedback?')) return;
    
    this.performanceService.submitReview(reviewId).subscribe({
      next: () => {
        this.loadReviews();
        alert('Review submitted successfully!');
      },
      error: (err) => {
        alert(err.error?.message || 'Failed to submit review');
      }
    });
  }

  getRole(): 'EMPLOYEE' | 'MANAGER' | 'ADMIN' {
    const role = this.auth.getRole();
    return (role === 'EMPLOYEE' || role === 'MANAGER' || role === 'ADMIN') ? role : 'EMPLOYEE';
  }
}
