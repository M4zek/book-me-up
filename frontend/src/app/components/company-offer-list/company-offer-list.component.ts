import {Component, Input} from '@angular/core';
import {OfferItemComponent} from "./offer-item/offer-item.component";
import {NgForOf, NgIf} from "@angular/common";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {CompanyOffersResponse} from "../../model/response/company-response.model";
import {AuthModalComponent} from "../modals/auth-modal/auth-modal.component";
import {ReservationModalComponent} from "../modals/reservation-modal/reservation-modal.component";
import {UserContextService} from "../../service/user-context.service";

@Component({
  selector: 'app-company-offer-list',
    imports: [
        OfferItemComponent,
        NgForOf,
        FormsModule,
        ReactiveFormsModule,
        AuthModalComponent,
        ReservationModalComponent,
        NgIf
    ],
  templateUrl: './company-offer-list.component.html',
  styleUrl: './company-offer-list.component.css'
})
export class CompanyOfferListComponent {

    @Input() offerItems: CompanyOffersResponse[] = [];
    @Input() companyId: number = 0;

    selectedOffer: CompanyOffersResponse | null = null;
    isReservationModalOpen = false;
    isAuthModalOpen = false;

    constructor(private userContextService: UserContextService) {}


    protected offerItemClick(offer: CompanyOffersResponse) {
        this.userContextService.isLoggedIn().subscribe(isLoggedIn => {
            if (isLoggedIn) {
                this.selectedOffer = offer;
                this.isReservationModalOpen = true;
            } else {
                this.selectedOffer = null;
                this.isAuthModalOpen = true;
            }
        })
    }

    protected hideReservationDialog() {
        this.isReservationModalOpen = false;
    }

    protected hideAuthModal() {
        this.isAuthModalOpen = false;
    }
}
