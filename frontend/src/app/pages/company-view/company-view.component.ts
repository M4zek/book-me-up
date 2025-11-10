import {Component, OnInit} from '@angular/core';
import {ImageListComponent} from "../../components/image-list/image-list.component";
import {CompanyOfferListComponent} from "../../components/company-offer-list/company-offer-list.component";
import {MapComponent} from "../../components/map/map.component";
import {CompanyEmployeeListComponent} from "../../components/company-employee-list/company-employee-list.component";
import {CompanyBusinessHoursComponent} from "../../components/company-business-hours/company-business-hours.component";
import {CompanyOpinionsComponent} from "../../components/company-opinions/company-opinions.component";
import {CompanyService} from "../../service/company.service";
import {ActivatedRoute} from "@angular/router";
import {NgIf} from "@angular/common";
import {DoubleSpinnerComponent} from "../../components/double-spinner/double-spinner.component";
import {Address} from "../../model/gui/gui.model";
import {
    CompanyDetailsResponse,
    CompanyOffersResponse,
    CompanyPortfolioResponse
} from "../../model/response/company-response.model";
import {Pagination} from "../../model/search/search.model";
import {PaginatorComponent} from "../../components/paginator/paginator.component";

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
        PaginatorComponent
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
          ratingCounts: [{ 1:0 }]
      },
      companyHours: [],
      owner: {
          id: 0, firstName: '', lastName: '', avatar: ''
      },
      employees: [],
      logo: ''
  };

  requestCompanySuccess = false; // Flag to show data company when request successfully pass
  requestCompanyOfferSuccess = false; // Flag to show company offer when request successfully pass
  company_id: number | null = null;

  constructor(private companyService: CompanyService,
              private routerActive: ActivatedRoute) {}

  ngOnInit(): void {
      this.company_id = Number(this.routerActive.snapshot.paramMap.get("id"));
      if (this.company_id){
          this.readCompanyFromApi(this.company_id);
          this.readCompanyOfferFromApi(this.company_id);
          this.readPortfolioCompanyFromApi(this.company_id);
      }

  }

  likeCompany(){
    console.log("Liked company")
  }

  shareCompany(){
    console.log("Shared company")
  }


  private readCompanyFromApi(id: number){
      this.companyService.getCompanyDetailById(id).subscribe({
          next: (response) => {
              if(response.status === 200 && response.body) {
                  this.company = response.body;
                  this.company.employees = [this.company.owner, ...this.company.employees];
                  this.companyAddress = this.getAddressToMap()
                  this.requestCompanyOfferSuccess = true;
              }
          },
          error: (error) => {
              this.requestCompanyOfferSuccess = false;
              const message = error.error;
              console.log(message);
          }
      })
  }


  private readPortfolioCompanyFromApi(id: number){
    this.companyService.getCompanyPortfolioByCompanyId(id).subscribe({
        next: (response) => {
            if(response.status === 200 && response.body) {
                this.portfolioItems = response.body.content
            }
        }
    })
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
