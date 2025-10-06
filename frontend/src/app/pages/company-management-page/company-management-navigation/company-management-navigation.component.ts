import {Component} from '@angular/core';
import {DropDownListComponent} from "../../../components/drop-down-list/drop-down-list.component";
import {NgClass, NgForOf} from "@angular/common";
import {Router, RouterLink} from "@angular/router";

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
    RouterLink
  ],
  templateUrl: './company-management-navigation.component.html',
  styleUrl: './company-management-navigation.component.css'
})
export class CompanyManagementNavigationComponent {

  NAVIGATION: CompanyManagementNavigation = {
    homePage: {name: 'Home', path: '/app/company-management/home'},
    employeePage: {name: 'Employees', path: '/app/company-management/employee'},
    offerPage: {name: 'Offers', path: '/app/company-management/offers'},
    appointmentsPage: {name: 'Appointments', path: '/app/company-management/appointments'}
  };

  public navigation: string[] = Object.keys(this.NAVIGATION);


  constructor(public router: Router) {
  }


}
