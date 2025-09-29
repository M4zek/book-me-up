import {Component, Input} from '@angular/core';
import {DecimalPipe} from "@angular/common";

@Component({
  selector: 'app-offer-item',
  imports: [
    DecimalPipe
  ],
  templateUrl: './offer-item.component.html',
  styleUrl: './offer-item.component.css'
})
export class OfferItemComponent {
  @Input() title: string = 'Offer title';
  @Input() description: string = 'Description';
  @Input() price: number = 30.00;
  @Input() duration: number = 10;

  openReservationDialog() {
    console.log('OpenReservationDialog: ' + this.title);
  }
}
