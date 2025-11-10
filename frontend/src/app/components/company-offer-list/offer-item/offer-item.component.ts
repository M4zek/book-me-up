import {Component, Input} from '@angular/core';
import {DecimalPipe} from "@angular/common";
import {ReservationModalComponent} from "../../modals/reservation-modal/reservation-modal.component";
import {CompanyOffersResponse} from "../../../model/response/company-response.model";

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

    @Input() offerItem: CompanyOffersResponse = {
        id: 0,
        name: 'template',
        description: 'template',
        duration: 0,
        price: 0,
    };

  isReservationVisible: boolean = false;

  openReservationDialog() {
    this.isReservationVisible = true;
  }

  hideReservationDialog() {
    this.isReservationVisible = false;
  }

}
