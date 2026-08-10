import {Component} from '@angular/core';
import {StatCardComponent} from "../../../components/stat-card/stat-card.component";
import {PaginatorComponent} from "../../../components/paginator/paginator.component";
import {ActivityCardComponent} from "../../../components/user-activity-card/activity-card.component";
import {
  AdminCompaniesToolbarComponent
} from "../../../components/admin-companies-toolbar/admin-companies-toolbar.component";
import {
  CompanyManagementListComponent
} from "../../../components/company-management-list/company-management-list.component";

@Component({
  selector: 'app-admin-companies',
  imports: [
    StatCardComponent,
    PaginatorComponent,
    ActivityCardComponent,
    AdminCompaniesToolbarComponent,
    CompanyManagementListComponent
  ],
  templateUrl: './admin-companies.component.html',
  styleUrl: './admin-companies.component.css'
})
export class AdminCompaniesComponent {

}
