import { Component } from '@angular/core';
import {NgForOf} from "@angular/common";

@Component({
  selector: 'app-company-employee-list',
    imports: [
        NgForOf
    ],
  templateUrl: './company-employee-list.component.html',
  styleUrl: './company-employee-list.component.css'
})
export class CompanyEmployeeListComponent {

  companyEmployees: string[] = [
    'John Doe',
    'John Doe',
    'John Doe',
    'John Doe',
  ]

}
