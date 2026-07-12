import {Component, OnInit} from '@angular/core';
import {
    CompanyManagementOfferItemComponent
} from "../../../components/company-management-offer-item/company-management-offer-item.component";
import {NgForOf, NgIf} from "@angular/common";
import {CompanyOfferSearch, Pagination} from "../../../model/search/search.model";
import {PaginatorComponent} from "../../../components/paginator/paginator.component";
import {SearchAndSortBarComponent, SortBy} from "../../../components/search-bar/search-and-sort-bar.component";
import {
    CompanyAddOfferModalComponent
} from "../../../components/modals/company-add-offer-modal/company-add-offer-modal.component";
import {DropDownListItem} from "../../../components/drop-down-list/drop-down-list.component";
import {CompanyContextService} from "../../../service/company-context.service";
import {CompanyService} from "../../../service/company.service";
import {CompanyOffersResponse} from "../../../model/http/company.model";
import {DoubleSpinnerComponent} from "../../../components/double-spinner/double-spinner.component";
import {ToastService} from "../../../service/toast.service";

@Component({
  selector: 'app-company-management-offer',
    imports: [
        CompanyManagementOfferItemComponent,
        NgForOf,
        PaginatorComponent,
        SearchAndSortBarComponent,
        CompanyAddOfferModalComponent,
        DoubleSpinnerComponent,
        NgIf
    ],
  templateUrl: './company-management-offer.component.html',
  styleUrl: './company-management-offer.component.css'
})
export class CompanyManagementOfferComponent implements OnInit {

    isAddOfferModalOpen = false;

    offers: CompanyOffersResponse[] = []

    paginator: Pagination = {
        totalItems: 0,
        itemsPerPage: 5,
        currentPage: 0,
        itemsPerPageOptions: [5, 10, 25, 50]
    }

    sortByItems: DropDownListItem[] = [
        {content: 'Price', image: 'icons/sort_number_asc_icon.svg', option: 'price,asc'},
        {content: 'Price', image: 'icons/sort_number_desc_icon.svg', option: 'price,desc'},
        {content: 'Duration', image: 'icons/sort_number_desc_icon.svg', option: 'duration,asc'},
        {content: 'Duration', image: 'icons/sort_number_desc_icon.svg', option: 'duration,desc'},
    ]

    isEditable: boolean = false;
    searchValue: CompanyOfferSearch = { company_id: 0 };
    isOfferLoading: boolean = false;

    constructor(
        private ctx: CompanyContextService,
        private toast: ToastService,
        private companyService: CompanyService) {
    }

    ngOnInit() {
        this.ctx.currentCompany$.subscribe(company => {
            if(company){
                this.searchValue = { company_id: company.id };
                this.isEditable = this.ctx.hasAnyRole("COMPANY_OWNER", "COMPANY_MANAGER")
                this.searchOffers();
            }
        })
    }

    onSortChange($event: SortBy) {
        this.searchValue.sort = $event.sorting;
        this.searchOffers();
    }

    onSearchChange($event: string) {
        this.searchValue.name = $event;
        this.searchOffers();
    }

    openAddModal() {
        this.isAddOfferModalOpen = true;
    }

    closeAddModal(){
        this.isAddOfferModalOpen = false;
    }


    protected searchOffers(){
        this.isOfferLoading = true;
        this.companyService.searchCompanyOffers(this.searchValue, this.paginator).subscribe(
            response => {
                if(response.status === 200 && response.body){
                    this.offers = response.body.content;
                    this.paginator.currentPage = response.body.page.number;
                    this.paginator.totalItems = response.body.page.totalElements;
                }
                this.isOfferLoading = false;
            }
        )
    }

    protected onPaginationChanged() {
        this.searchOffers();
    }

    protected onOfferAddToList($event: CompanyOffersResponse | null) {
        if($event) {
            this.offers.push($event)
        }
    }
}
