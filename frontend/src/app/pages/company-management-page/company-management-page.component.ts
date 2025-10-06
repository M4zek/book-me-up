import {Component} from '@angular/core';
import {
  CompanyManagementNavigationComponent
} from "./company-management-navigation/company-management-navigation.component";
import {RouterOutlet} from "@angular/router";

@Component({
  selector: 'app-company-management-page',
  imports: [
    CompanyManagementNavigationComponent,
    RouterOutlet
  ],
  templateUrl: './company-management-page.component.html',
  styleUrl: './company-management-page.component.css'
})
export class CompanyManagementPageComponent {

}
