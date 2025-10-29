import {Component, EventEmitter, Input, Output} from '@angular/core';
import {NgIf} from "@angular/common";
import {RatingStarsComponent} from "../../company-opinions/rating-stars/rating-stars.component";

export interface OpinionModel{
  offerId: number;
  userId: number;
  rating: number;
  content: string;
}

@Component({
  selector: 'app-add-opinion-modal',
  imports: [
    NgIf,
    RatingStarsComponent
  ],
  templateUrl: './add-opinion-modal.component.html',
  styleUrl: './add-opinion-modal.component.css'
})
export class AddOpinionModalComponent {

  @Input() isVisible: boolean = false;
  @Output() closeModal = new EventEmitter<void>();

  close() {
    this.closeModal.emit();
  }

  onRatingChange($event: number) {
    console.log($event);
  }

  confirm() {

  }
}
