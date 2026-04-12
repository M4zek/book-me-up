import {Component, OnInit} from '@angular/core';
import {CompanyOpinionsComponent} from "../../../components/company-opinions/company-opinions.component";
import {
    CompanyPortfolioListComponent
} from "../../../components/company-portfolio-list/company-portfolio-list.component";
import {MapComponent} from "../../../components/map/map.component";
import {
    CompanyBusinessHoursComponent
} from "../../../components/company-business-hours/company-business-hours.component";
import {
    CompanyDescriptionEditModalComponent
} from "../../../components/modals/company-description-edit-modal/company-description-edit-modal.component";
import {
    CompanyAddressEditModalComponent
} from "../../../components/modals/company-address-edit-modal/company-address-edit-modal.component";
import {CompanyHomeManagementModel, PortfolioModel} from "../../../model/gui/gui.model";
import {
    CompanyNameLogoEditModalComponent
} from "../../../components/modals/company-name-logo-edit-modal/company-name-logo-edit-modal.component";
import {
    CompanyBusinessHourEditModalComponent
} from "../../../components/modals/company-bussines-hour-edit-modal/company-business-hour-edit-modal.component";
import {
    CompanyPortfolioAddModalComponent
} from "../../../components/modals/company-portfolio-add-modal/company-portfolio-add-modal.component";
import {CompanyContextService} from "../../../service/company-context.service";
import {CompanyDetailsResponse} from "../../../model/http/company.model";
import {CompanyService} from "../../../service/company.service";
import {concatMap} from "rxjs";
import {NgIf} from "@angular/common";
import {DoubleSpinnerComponent} from "../../../components/double-spinner/double-spinner.component";

@Component({
  selector: 'app-company-management-home',
    imports: [
        CompanyOpinionsComponent,
        CompanyPortfolioListComponent,
        MapComponent,
        CompanyBusinessHoursComponent,
        CompanyDescriptionEditModalComponent,
        CompanyAddressEditModalComponent,
        CompanyNameLogoEditModalComponent,
        CompanyBusinessHourEditModalComponent,
        CompanyPortfolioAddModalComponent,
        NgIf,
        DoubleSpinnerComponent
    ],
  templateUrl: './company-management-home.component.html',
  styleUrl: './company-management-home.component.css'
})
export class CompanyManagementHomeComponent implements OnInit {

  openEditDescriptionModal: boolean = false;
  openEditAddressModal: boolean = false;
  openEditHoursModal: boolean = false;
  openEditLogoNameModal: boolean = false;
  openAddPortfolioModal: boolean = false;

  isCompanyDataLoading: boolean = false;
  isDataEditable: boolean = false;

  companyPortfolioData: PortfolioModel[] = []
  companyGUIData: CompanyHomeManagementModel | null = null;

  constructor(private companyContextService: CompanyContextService,
              private companyService: CompanyService) {}

  ngOnInit() {
      this.companyContextService.currentCompany$.subscribe(company => {
          if(company) {
              this.isCompanyDataLoading = true;
              this.companyService.getCompanyPortfolioByCompanyId(company.id)
                  .pipe(
                      concatMap(result => {
                          let tmpList: PortfolioModel[] = [];
                          if(result.body?.content){
                              result.body.content.forEach(element => {
                                  tmpList.push({
                                      id: element.id,
                                      name: element.filename,
                                      photo: element.image
                                  })
                              })
                              this.companyPortfolioData = tmpList;
                          }

                          return this.companyService.getCompanyDetailById(company.id);
                      })
                  ).subscribe(result => {
                      if(result.status == 200 && result.body) {
                          this.companyGUIData = this.createGUIModel(result.body);
                      }

                      this.isCompanyDataLoading = false;
                      this.isDataEditable = this.companyContextService.hasAnyRole("COMPANY_OWNER")
              })
          } else {
              this.companyGUIData = null;
          }
      })
  }

  ngOnDestroy() {
      this.companyGUIData = null;
      this.companyPortfolioData = []
  }


  openEditModal(modalName: string) {
      if (!this.companyContextService.hasRole("COMPANY_EMPLOYEE")){
          switch (modalName) {
              case 'editDescriptionModal':
                  this.openEditDescriptionModal = true;
                  break;
              case 'editAddressModal':
                  this.openEditAddressModal = true;
                  break;
              case 'editHoursModal':
                  this.openEditHoursModal = true;
                  break;
              case 'editLogoNameModal':
                  this.openEditLogoNameModal = true;
                  break;
              case 'addPortfolioModal':
                  this.openAddPortfolioModal = true;
                  break;
          }
      } else {
          console.error("You dont have permission to edit company data!")
      }
  }


  closeEditModal(modalName: string) {
    switch (modalName) {
      case 'editDescriptionModal':
        this.openEditDescriptionModal = false;
        break;
      case 'editAddressModal':
        this.openEditAddressModal = false;
        break;
      case 'editHoursModal':
        this.openEditHoursModal = false;
        break;
      case 'editLogoNameModal':
        this.openEditLogoNameModal = false;
        break;
      case 'addPortfolioModal':
        this.openAddPortfolioModal = false;
        break;
    }
  }

  private createGUIModel(response: CompanyDetailsResponse): CompanyHomeManagementModel {
      return {
          id: response.id,
          description: response.description,
          portfolio: this.companyPortfolioData,
          name_logo: {
              logo: response.logo,
              logoName: 'none',
              companyName: response.name
          },
          address: {
              city: response.address.city,
              postalCode: response.address.postalCode,
              street: response.address.street,
              buildingNumber: response.address.buildingNumber
          },
          hours: response.companyHours,
          opinions: response.reviewStatistics
      }
  }


  protected onDescChange($event: string) {
    if(this.companyGUIData)
      this.companyGUIData.description = $event;
  }
}

