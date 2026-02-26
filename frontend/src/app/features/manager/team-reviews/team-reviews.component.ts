import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { PerformanceService } from '../../../core/services/performance.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-team-reviews',
  standalone: true,
  imports: [CommonModule, FormsModule, SidebarComponent],
  templateUrl: './team-reviews.component.html',
  styleUrls: ['./team-reviews.component.css']
})
export class TeamReviewsComponent implements OnInit {
  reviews = signal<any[]>([]);
  isLoading = signal(true);
  showFeedbackModal = signal(false);
  selectedReview = signal<any>(null);
  feedbackData = { feedback: '', rating: 3 };

  constructor(
    private performanceService: PerformanceService,
    public auth: AuthService
  ) {}

  ngOnInit() {
    this.loadTeamReviews();
  }

  loadTeamReviews() {
    this.performanceService.getTeamReviews().subscribe({
      next: (data) => {
        this.reviews.set(data);
        this.isLoading.set(false);
      },
      error: () => {
        this.reviews.set([]);
        this.isLoading.set(false);
      }
    });
  }

  openFeedbackModal(review: any) {
    this.selectedReview.set(review);
    this.feedbackData = { feedback: review.managerFeedback || '', rating: review.managerRating || 3 };
    this.showFeedbackModal.set(true);
  }

  submitFeedback() {
    const reviewId = this.selectedReview()?.id;
    if (!reviewId) return;

    this.performanceService.provideFeedback(reviewId, this.feedbackData).subscribe({
      next: () => {
        this.showFeedbackModal.set(false);
        this.loadTeamReviews();
        alert('Feedback submitted successfully!');
      },
      error: () => alert('Failed to submit feedback')
    });
  }

  closeModal() {
    this.showFeedbackModal.set(false);
    this.selectedReview.set(null);
  }

  getInitials(name: string | undefined): string {
    if (!name) return '?';
    return name.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2);
  }

  getStars(rating: number): string {
    return '★'.repeat(rating) + '☆'.repeat(5 - rating);
  }
}
