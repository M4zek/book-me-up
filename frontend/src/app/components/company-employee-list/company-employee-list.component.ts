import {Component, Input} from '@angular/core';
import {NgForOf} from "@angular/common";
import {EmployeeSummaryResponse, FileType} from "../../model/http/company.model";
import {MyImgComponent} from "../my-img/my-img.component";

@Component({
  selector: 'app-company-employee-list',
    imports: [
        NgForOf,
        MyImgComponent
    ],
  templateUrl: './company-employee-list.component.html',
  styleUrl: './company-employee-list.component.css'
})
export class CompanyEmployeeListComponent {

  @Input() companyEmployees: EmployeeSummaryResponse[] = []

    protected readonly FileType = FileType;
}
