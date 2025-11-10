import {Component, Input} from '@angular/core';
import {OfferItemComponent} from "./offer-item/offer-item.component";
import {NgForOf} from "@angular/common";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {CompanyOffersResponse} from "../../model/response/company-response.model";

@Component({
  selector: 'app-company-offer-list',
    imports: [
        OfferItemComponent,
        NgForOf,
        FormsModule,
        ReactiveFormsModule
    ],
  templateUrl: './company-offer-list.component.html',
  styleUrl: './company-offer-list.component.css'
})
export class CompanyOfferListComponent {

    @Input() offerItems: CompanyOffersResponse[] = []

}
