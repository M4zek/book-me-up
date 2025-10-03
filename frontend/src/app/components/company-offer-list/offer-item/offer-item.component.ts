import {Component, Input} from '@angular/core';
import {DecimalPipe} from "@angular/common";
import {ReservationModalComponent} from "../../reservation-modal/reservation-modal.component";

@Component({
  selector: 'app-offer-item',
  imports: [
    DecimalPipe,
    ReservationModalComponent
  ],
  templateUrl: './offer-item.component.html',
  styleUrl: './offer-item.component.css'
})
export class OfferItemComponent {
  @Input() title: string = 'Offer title';
  @Input() description: string = 'Description';
  @Input() price: number = 30.00;
  @Input() duration: number = 10;

  isReservationVisible: boolean = false;

  openReservationDialog() {
    this.isReservationVisible = true;
  }

  hideReservationDialog() {
    this.isReservationVisible = false;
  }

}
