import {Component} from '@angular/core';
import {SearchAndSortBarComponent, SortBy} from "../../../components/search-bar/search-and-sort-bar.component";
import {PaginatorComponent} from "../../../components/paginator/paginator.component";
import {
  CompanyManagementAppointmentsItemComponent
} from "../../../components/company-management-appointments-item/company-management-appointments-item.component";

@Component({
  selector: 'app-company-management-appointments',
  imports: [
    SearchAndSortBarComponent,
    PaginatorComponent,
    CompanyManagementAppointmentsItemComponent
  ],
  templateUrl: './company-management-appointments.component.html',
  styleUrl: './company-management-appointments.component.css'
})
export class CompanyManagementAppointmentsComponent {


  onSearchChange($event: string) {
    
  }

  onSortChange($event: SortBy) {

  }


}
