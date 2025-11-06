import {Component, Input} from '@angular/core';
import {NgForOf} from "@angular/common";
import {EmployeeSummaryResponse} from "../../model/response.model";

@Component({
  selector: 'app-company-employee-list',
    imports: [
        NgForOf
    ],
  templateUrl: './company-employee-list.component.html',
  styleUrl: './company-employee-list.component.css'
})
export class CompanyEmployeeListComponent {

  @Input() companyEmployees: EmployeeSummaryResponse[] = []

}
