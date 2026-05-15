import {Component, OnInit} from '@angular/core';
import {ImageListComponent} from "../../components/image-list/image-list.component";
import {CompanyOfferListComponent} from "../../components/company-offer-list/company-offer-list.component";
import {MapComponent} from "../../components/map/map.component";
import {CompanyEmployeeListComponent} from "../../components/company-employee-list/company-employee-list.component";
import {CompanyBusinessHoursComponent} from "../../components/company-business-hours/company-business-hours.component";
import {CompanyOpinionsComponent} from "../../components/company-opinions/company-opinions.component";
import {CompanyService} from "../../service/company.service";
import {ActivatedRoute, RouterLink} from "@angular/router";
import {NgIf} from "@angular/common";
import {DoubleSpinnerComponent} from "../../components/double-spinner/double-spinner.component";
import {Address} from "../../model/gui/gui.model";
import {CompanyDetailsResponse, CompanyOffersResponse, CompanyPortfolioResponse} from "../../model/http/company.model";
import {Pagination} from "../../model/search/search.model";
import {PaginatorComponent} from "../../components/paginator/paginator.component";
import {forkJoin} from "rxjs";

@Component({
  selector: 'app-company-view',
    imports: [
        ImageListComponent,
        CompanyOfferListComponent,
        MapComponent,
        CompanyEmployeeListComponent,
        CompanyBusinessHoursComponent,
        CompanyOpinionsComponent,
        NgIf,
        DoubleSpinnerComponent,
        PaginatorComponent,
        RouterLink
    ],
  templateUrl: './company-view.component.html',
  styleUrl: './company-view.component.css'
})
export class CompanyViewComponent implements OnInit {

  companyAddress: Address = {
      city:'',
      street:'',
      postalCode:'',
      buildingNumber:''
  };

  pagination: Pagination = {
      totalItems: 0,
      currentPage: 0,
      itemsPerPage: 5,
      itemsPerPageOptions: [5,10,15,25,50]
  }

  offerItems: CompanyOffersResponse[] = [];
  portfolioItems: CompanyPortfolioResponse[] = [];
  // Template
  company: CompanyDetailsResponse = {
      id: 0, name: '', description: '',
      address: {
           id:0, city: '', postalCode: '', street: '', buildingNumber: ''
      },
      reviewStatistics: {
          rating: 0, totalReviews: 0,
          ratingCounts: { 1:0 }
      },
      companyHours: [],
      owner: {
          id: 0, firstName: '', lastName: '', avatar: ''
      },
      employees: [],
      logo: ''
  };

  isDataLoading = false;
  hasDataLoadingError = false;

  requestCompanyOfferSuccess = false; // Flag to show company offer when request successfully pass
  company_id: number | null = null;

  constructor(private companyService: CompanyService,
              private routerActive: ActivatedRoute) {}

  ngOnInit(): void {

      this.company_id = Number(this.routerActive.snapshot.paramMap.get("id"));

      if(this.company_id){

          this.isDataLoading = true;
          this.hasDataLoadingError = false;

          forkJoin([
              this.companyService.getCompanyDetailById(this.company_id),
              this.companyService.getCompanyOffersByCompanyId(this.company_id, this.pagination),
              this.companyService.getCompanyPortfolioByCompanyId(this.company_id),
          ]).subscribe({
              next: ([details, offers, portfolio]) => {

                  if(details.body && details.status === 200){
                      this.company = details.body;
                      this.company.employees = [this.company.owner, ...this.company.employees];
                      this.companyAddress = this.getAddressToMap();
                  }

                  if(portfolio.body && portfolio.status === 200){
                      this.portfolioItems = portfolio.body.content;
                  }

                  if(offers.body && portfolio.body){
                      this.offerItems = offers.body.content
                      this.pagination.currentPage = offers.body.page.number;
                      this.pagination.totalItems = offers.body.page.totalElements;
                      this.requestCompanyOfferSuccess = true;
                  } else {
                      this.offerItems = [];
                      this.requestCompanyOfferSuccess = false;
                  }

                  this.isDataLoading = false;
              },
              error: (err) => {
                  console.log(err);
                  this.hasDataLoadingError = true;
                  this.isDataLoading = false;

              },
              complete: () => {
                  this.isDataLoading = false;
              }
          })
      }

  }

  likeCompany(){
    console.log("Liked company")
  }

  shareCompany(){
    console.log("Shared company")
  }



  private readCompanyOfferFromApi(id: number){
      this.offerItems = []
      this.requestCompanyOfferSuccess = false;

      this.companyService.getCompanyOffersByCompanyId(id, this.pagination).subscribe({
          next: (response) => {
            if (response.status === 200 && response.body) {
                this.offerItems = response.body.content
                this.pagination.currentPage = response.body.page.number;
                this.pagination.totalItems = response.body.page.totalElements;
                this.requestCompanyOfferSuccess = true;
            } else {
                this.offerItems = [];
                this.requestCompanyOfferSuccess = false;
            }
          },
          error: (error) => {
              this.requestCompanyOfferSuccess = false;
              const message = error.error;
              console.log(message);
          }
      })
  }


  protected  getStringAddress(): string{
      return `${this.company.address.postalCode} ${this.company.address.city}, ${this.company.address.street} ${this.company.address.buildingNumber}`
  }

  protected getAddressToMap(): Address {
    return {
        city: this.company.address.city,
        street: this.company.address.street,
        postalCode: this.company.address.postalCode,
        buildingNumber: this.company.address.buildingNumber
    }
  }

    onPaginatorOfferChanged() {
      if(this.company_id)
        this.readCompanyOfferFromApi(this.company_id);
    }
}
