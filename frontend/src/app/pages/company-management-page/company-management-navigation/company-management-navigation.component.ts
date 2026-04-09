import {Component, OnInit} from '@angular/core';
import {DropDownListComponent, DropDownListItem} from "../../../components/drop-down-list/drop-down-list.component";
import {NgClass, NgForOf} from "@angular/common";
import {Router, RouterLink} from "@angular/router";
import {
    CompanyManagementAddCompanyModalComponent
} from "../../../components/modals/company-management-add-company-modal/company-management-add-company-modal.component";
import {CompanyDetailsResponse, UserCompanyResponse} from "../../../model/http/company.model";
import {CompanyService} from "../../../service/company.service";
import {CompanyContextService} from "../../../service/company-context.service";

export interface NavCompanyManagementType {
  name: string;
  path: string;
}

export interface CompanyManagementNavigation {
  [id: string]: NavCompanyManagementType
}

@Component({
  selector: 'app-company-management-navigation',
    imports: [
        DropDownListComponent,
        NgForOf,
        NgClass,
        RouterLink,
        CompanyManagementAddCompanyModalComponent
    ],
  templateUrl: './company-management-navigation.component.html',
  styleUrl: './company-management-navigation.component.css'
})
export class CompanyManagementNavigationComponent implements OnInit{

  isOpenAddDialog: boolean = false;

  NAVIGATION: CompanyManagementNavigation = {};

  user_companies: UserCompanyResponse[] = []
  selected_company: DropDownListItem | null = null;
  dropDownCompanyItemList: DropDownListItem[] = [];

  public navigation: string[] = Object.keys(this.NAVIGATION);

  constructor(public router: Router,
              private companyService: CompanyService,
              private companyContextService: CompanyContextService) {
  }


    ngOnInit() {
      this.companyService.getUserCompanies().subscribe(response => {
          if (response.status == 200 && response.body) {
              this.user_companies = response.body;

              let tmpList: DropDownListItem[] = [];

                this.user_companies.forEach(company => {
                    tmpList.push({
                        id: company.id,
                        content: company.name,
                        image: company.logo,
                    })
                })

              if (tmpList.length > 0) {
                  this.selected_company = tmpList[0];
                  this.dropDownCompanyItemList = tmpList;
                  this.changeCompanyToContext()
              }
          }
          this.createNavigation();
      })
    }



  openAddCompanyDialog() {
    this.isOpenAddDialog = true;
  }

  closeAddCompanyDialog() {
    this.isOpenAddDialog = false;
  }

  protected onCompanySelectedChange($event: DropDownListItem) {
    this.selected_company = $event;

    if(!this.selected_company.content.toLowerCase().includes('none')){
        this.changeCompanyToContext();
    } else {
        this.companyContextService.setCompany(null);
    }
    this.createNavigation();
  }

  private createNavigation(){
      if(this.companyContextService.getCompany() != null){
          this.NAVIGATION = {
              homePage: {name: 'Home', path: '/app/company-management/home'},
              employeePage: {name: 'Employees', path: '/app/company-management/employee'},
              offerPage: {name: 'Offers', path: '/app/company-management/offers'},
              appointmentsPage: {name: 'Appointments', path: '/app/company-management/appointments'},
              calendarPage: {name: 'Calendar', path: '/app/company-management/calendar'}
          };
      } else {
          this.NAVIGATION = {}
      }
      this.navigation = Object.keys(this.NAVIGATION);
  }

  private changeCompanyToContext(){
      const result: UserCompanyResponse | null =
          this.user_companies.find(c => c.id === this.selected_company?.id) ?? null;
      this.companyContextService.setCompany(result);
  }

    protected onCompanyCreatedFromModal($event: CompanyDetailsResponse) {
        if($event) {

            // Add new company to drop down list component
            const drop_item = {
                id: $event.id,
                content: $event.name,
                image: $event.logo,
            }
            this.dropDownCompanyItemList.push(drop_item);

            // Add new company to all user companies
            this.user_companies.push({
                id: $event.id,
                name: $event.name,
                logo: $event.logo,
                role: ['COMPANY_OWNER']
            })
        }
    }

    protected onShowNewCompany($event: Number) {
        const drop_item = this.dropDownCompanyItemList.find(item => item.id === $event)
        if(drop_item){
            this.selected_company = drop_item;
        }
    }
}
