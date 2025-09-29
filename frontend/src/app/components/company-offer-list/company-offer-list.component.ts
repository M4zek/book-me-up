import {Component} from '@angular/core';
import {OfferItemComponent} from "./offer-item/offer-item.component";
import {NgForOf} from "@angular/common";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";

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

    offerItems = [
        {title: "Test 1", description: "Test 1", price: 10, duration: 10},
        {title: "Test 2", description: "Test 2", price: 10, duration: 10},
        {title: "Test 3", description: "Test 3", price: 10, duration: 10},
        {title: "Test 4", description: "Test 4", price: 10, duration: 10},
        {title: "Test 5", description: "Test 5", price: 10, duration: 10},
  ]

}
