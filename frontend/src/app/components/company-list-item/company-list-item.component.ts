import {Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {DecimalPipe} from "@angular/common";
import {CompanySummaryResponse} from "../../model/http/company.model";

@Component({
    selector: 'app-company-list-item',
    imports: [
        DecimalPipe
    ],
    templateUrl: './company-list-item.component.html',
    styleUrl: './company-list-item.component.css'
})
export class CompanyListItemComponent implements OnChanges {

    @Input() company: CompanySummaryResponse = {
        id: 0, name: '',
        address: {id: 0, city: '', postalCode: '', street: '', buildingNumber: '',},
        rating: 0, numberOfReviews: 0, logo: '',
    }
    company_logo_url: string = '/images/default_logo_company.png'
    star_icon: string = '/icons/star_icon.svg'


    ngOnChanges(changes: SimpleChanges): void {
        if(changes['company']) {
            if(!this.company.logo) {
                this.company.logo = this.company_logo_url
            }
        }
    }
}
