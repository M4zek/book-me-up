import {Component, Input} from '@angular/core';
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
  @Input() rating: number = 0; // np. 4.2
  stars: number[] = [1, 2, 3, 4, 5];

  getStarFill(star: number): number {
    if (this.rating >= star) return 100;
    if (this.rating + 1 > star) return (this.rating - (star - 1)) * 100;
    return 0;
  }
}
