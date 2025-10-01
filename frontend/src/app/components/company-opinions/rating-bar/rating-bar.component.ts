import {Component, Input} from '@angular/core';

@Component({
  selector: 'app-rating-bar',
  imports: [],
  templateUrl: './rating-bar.component.html',
  styleUrl: './rating-bar.component.css'
})
export class RatingBarComponent {
  @Input() rating: number = 0;
  @Input() count: number = 0;
  @Input() total: number = 0;

  getPercentage(): number {
    return this.total ? (this.count / this.total) * 100 : 0;
  }
}
