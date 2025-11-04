import {Component} from '@angular/core';
import {
    CompanyManagementOfferItemComponent
} from "../../../components/company-management-offer-item/company-management-offer-item.component";
import {NgForOf} from "@angular/common";
import {Pagination} from "../../../model/search/search.model";
import {PaginatorComponent} from "../../../components/paginator/paginator.component";
import {OfferManagementItem} from "../../../model/gui/gui.model";
import {SearchAndSortBarComponent, SortBy} from "../../../components/search-bar/search-and-sort-bar.component";
import {
    CompanyAddOfferModalComponent
} from "../../../components/modals/company-add-offer-modal/company-add-offer-modal.component";

@Component({
  selector: 'app-company-management-offer',
    imports: [
        CompanyManagementOfferItemComponent,
        NgForOf,
        PaginatorComponent,
        SearchAndSortBarComponent,
        CompanyAddOfferModalComponent
    ],
  templateUrl: './company-management-offer.component.html',
  styleUrl: './company-management-offer.component.css'
})
export class CompanyManagementOfferComponent {

    isAddOfferModalOpen = false;

    offers: OfferManagementItem[] = [
        {
            id: 0,
            name: 'Men\'s haircut',
            price: 30.00,
            duration: 30,
            description: 'Some description'
        },
        {
            id: 1,
            name: 'Men\'s haircut',
            price: 25.00,
            duration: 15,
            description: 'Some descriptions'
        }
    ]

    paginator: Pagination = {
        totalItems: this.offers.length,
        itemsPerPage: 10,
        currentPage: 1,
        itemsPerPageOptions: [5, 10, 25, 50]
    }


    onSortChange($event: SortBy) {
        console.log($event);
    }

    onSearchChange($event: string) {
        console.log($event);
    }

    openAddModal() {
        this.isAddOfferModalOpen = true;
    }

    closeAddModal(){
        this.isAddOfferModalOpen = false;
    }

}
