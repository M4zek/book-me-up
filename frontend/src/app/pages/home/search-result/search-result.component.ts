import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {CompanyListComponent} from "../../../components/company-list-item/company-list/company-list.component";
import {CompanyListItemComponent} from "../../../components/company-list-item/company-list-item.component";
import {Pagination, SearchCompanyOptions} from "../../../model/search/search.model";
import {Subject, takeUntil} from "rxjs";
import {PaginatorComponent} from "../../../components/paginator/paginator.component";
import {CompanyService} from "../../../service/company.service";
import {CompanySummaryResponse} from "../../../model/response/company-response.model";
import {NgForOf, NgIf} from "@angular/common";
import {DoubleSpinnerComponent} from "../../../components/double-spinner/double-spinner.component";
import {UserContextService} from "../../../service/user-context.service";

@Component({
  selector: 'app-search-result',
    imports: [
        CompanyListComponent,
        CompanyListItemComponent,
        PaginatorComponent,
        NgForOf,
        DoubleSpinnerComponent,
        NgIf
    ],
  templateUrl: './search-result.component.html',
  styleUrl: './search-result.component.css'
})
export class SearchResultComponent implements OnInit {

  private destroy$ = new Subject<void>();

  searchValueOptions: SearchCompanyOptions = {}
  pagination: Pagination = {
      totalItems: 0,
      itemsPerPage: 5,
      currentPage: 0,
      itemsPerPageOptions: [5, 10, 15, 20, 30]
  }

  companyList: CompanySummaryResponse[] = []
  companyRecommendedList: CompanySummaryResponse[] = []

  loadingRecommended: boolean = true;
  searchingCompanies: boolean = true;
  textRecommended: string = "Recommended";
  textSearch: string = "Search results";

  constructor(private route: ActivatedRoute,
              private router: Router,
              private userContextService: UserContextService,
              private companyService: CompanyService) {}


  ngOnInit() {
    this.route.queryParamMap
        .pipe(takeUntil(this.destroy$))
        .subscribe(params => {

            this.searchValueOptions = {
                category: params.get('category'),
                city: params.get('city'),
                companyName: params.get('companyName'),
            }
            this.searchCompany();
            this.searchRecommended();
            this.createTitleTexts();
        });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private createTitleTexts(){
      this.textRecommended = "Recommended";
      this.textSearch = "Search results";

      if (this.searchValueOptions) {
          Object.entries(this.searchValueOptions).forEach(([key, value]) => {
              if (value != null && value !== '') {
                  this.textSearch += " - " + value;
                  this.textRecommended += " - " + value;
              }
          });
      }
  }

  protected searchCompany() {
      this.companyList = []
      this.searchingCompanies = true;
      this.companyService.searchCompanyByCompanyNameOrCityOrCategory(this.pagination, this.searchValueOptions).subscribe({
          next: (response) => {
              if (response.status === 200 && response.body) {
                  this.companyList = response.body.content
                  this.pagination.currentPage = response.body.page.number;
                  this.pagination.totalItems = response.body.page.totalElements;
              }
              this.searchingCompanies = false;
          }
      })
  }

  protected searchRecommended(){
      this.companyRecommendedList = []
      this.loadingRecommended = true
      this.companyService.getCompanyRecommended(this.pagination, this.searchValueOptions).subscribe({
          next: (response) => {
              if (response.status === 200 && response.body) {
                  this.companyRecommendedList = response.body.content
              }
              this.loadingRecommended = false;
          }
      })
  }

    protected onPaginatorChanged() {
        this.searchCompany();
    }

    onCompanyClick(company: CompanySummaryResponse) {
        this.userContextService.isLoggedIn().subscribe(isLogged => {
            if (isLogged) {
                this.router.navigate(['app/company', company.id])
                    .then(r => console.log("Redirect to APP/company/: ",r));
            } else {
                this.router.navigate(['guest/company', company.id])
                    .then(r => console.log("Redirect to GUEST/company/: ",r));
            }
        })
    }
}
