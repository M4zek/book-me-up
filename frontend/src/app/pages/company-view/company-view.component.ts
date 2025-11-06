import {Component, OnInit} from '@angular/core';
import {ImageListComponent} from "../../components/image-list/image-list.component";
import {CompanyOfferListComponent} from "../../components/company-offer-list/company-offer-list.component";
import {MapComponent} from "../../components/map/map.component";
import {CompanyEmployeeListComponent} from "../../components/company-employee-list/company-employee-list.component";
import {CompanyBusinessHoursComponent} from "../../components/company-business-hours/company-business-hours.component";
import {CompanyOpinionsComponent} from "../../components/company-opinions/company-opinions.component";
import {CompanyService} from "../../service/company.service";
import {ActivatedRoute} from "@angular/router";
import {CompanyDetailsResponse} from "../../model/response.model";
import {NgIf} from "@angular/common";
import {DoubleSpinnerComponent} from "../../components/double-spinner/double-spinner.component";
import {Address} from "../../model/gui/gui.model";

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
        DoubleSpinnerComponent
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

  constructor(private companyService: CompanyService,
              private routerActive: ActivatedRoute) {}

  ngOnInit(): void {
      const company_id: number | null = Number(this.routerActive.snapshot.paramMap.get("id"));
      this.readCompanyFromApi(company_id);
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
                  this.companyAddress = this.getAddressToMap()
                  this.requestCompanySuccess = true;
              }
          },
          error: (error) => {
              this.requestCompanySuccess = false;
              const message = error.error;
              console.log(message);
          }
      })
  }


  private readPortfolioCompanyFromApi(id: number){
      // TODO Send request
  }

  private readCompanyOfferFromApi(id: number){
      // TODO Send request
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
}
