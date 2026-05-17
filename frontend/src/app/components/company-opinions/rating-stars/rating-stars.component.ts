import {Component, EventEmitter, Input, Output} from '@angular/core';
import {NgForOf} from "@angular/common";

@Component({
  selector: 'app-rating-stars',
  imports: [
    NgForOf
  ],
  templateUrl: './rating-stars.component.html',
  styleUrl: './rating-stars.component.css'
})
export class RatingStarsComponent {
  uniqueId = crypto.randomUUID();
  @Input() rating: number = 0;
  @Input() isEditing = false;
  @Output() ratingChanged = new EventEmitter<number>();

  stars: number[] = [1, 2, 3, 4, 5];

  getStarFill(star: number): number {
    if (this.rating >= star) return 100;
    if (this.rating + 1 > star) return (this.rating - (star - 1)) * 100;
    return 0;
  }

  setRating(rating: number) {
    if(this.isEditing){
      this.rating = rating;
      this.ratingChanged.emit(this.rating);
    }
  }
}
