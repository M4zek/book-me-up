import {Component, OnInit} from '@angular/core';
import {CompanyListComponent} from "../../../components/company-list-item/company-list/company-list.component";
import {CompanySummaryResponse} from "../../../model/response.model";
import {Pagination} from "../../../model/search/search.model";
import {CompanyService} from "../../../service/company.service";
import {DoubleSpinnerComponent} from "../../../components/double-spinner/double-spinner.component";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-welcome',
    imports: [
        CompanyListComponent,
        DoubleSpinnerComponent,
        NgIf
    ],
  templateUrl: './welcome.component.html',
  styleUrl: './welcome.component.css'
})
export class WelcomeComponent implements OnInit {
    recommendedCompanies: CompanySummaryResponse[] = []

    pagination: Pagination = {
        totalItems: 0,
        itemsPerPage: 10,
        currentPage: 0
    }

    constructor(private companyService: CompanyService) {}

    ngOnInit(): void {
        this.getCompanyList();
    }


    getCompanyList() {
        this.companyService.getCompanySummary(this.pagination)
        .subscribe(result => {
            this.recommendedCompanies = result.content;
        })
    }

}
