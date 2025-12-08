import {Component, OnInit} from '@angular/core';
import {PaginatorComponent} from "../../../components/paginator/paginator.component";
import {Pagination} from "../../../model/search/search.model";
import {NgForOf, NgIf} from "@angular/common";
import {DropDownListComponent, DropDownListItem} from "../../../components/drop-down-list/drop-down-list.component";
import {HiredEmployeeRole} from "../../../model/gui/gui.model";
import {
    CompanyAddEmployeeModalComponent
} from "../../../components/modals/company-add-employee-modal/company-add-employee-modal.component";
import {CompanyContextService} from "../../../service/company-context.service";
import {CompanyService} from "../../../service/company.service";
import {CompanyEmployeeDetailsResponse} from "../../../model/http/company.model";
import {DoubleSpinnerComponent} from "../../../components/double-spinner/double-spinner.component";


@Component({
  selector: 'app-company-management-employee',
    imports: [
        PaginatorComponent,
        NgForOf,
        DropDownListComponent,
        CompanyAddEmployeeModalComponent,
        DoubleSpinnerComponent,
        NgIf
    ],
  templateUrl: './company-management-employee.component.html',
  styleUrl: './company-management-employee.component.css'
})
export class CompanyManagementEmployeeComponent implements OnInit {

  isAddEmployeeModalOpen = false;
  isEmployeesLoading = false;
  isDataEditable = false;

  roles: DropDownListItem[] = []

  pagination: Pagination = {
    totalItems: 50,
    itemsPerPage: 5,
    currentPage: 0,
    itemsPerPageOptions: [5, 10, 25, 50, 100],
  }

  employeeList: CompanyEmployeeDetailsResponse[] = [];

  constructor(private ctx: CompanyContextService, private companyService: CompanyService) {
  }

  ngOnInit() {
    this.roles = Object.values(HiredEmployeeRole).map((role, index) => ({
        id: index + 1,
        content: role
    }));

    this.isDataEditable = this.ctx.isOwnerLoggedIn();
    this.initEmployeeList();
  }



   protected getEmployeeRole(role: string) {
    role = role.toLowerCase().replace("company_", '');
    let found = this.roles.find(item => item.content.toLowerCase().includes(role));

    if (found) {
      return found;
    }

    return { id:-1, content: '' };
  }

  protected initEmployeeList() {
      this.isEmployeesLoading = true;
      this.ctx.currentCompany$.subscribe(company => {
          if(company) {
              this.companyService.getCompanyEmployeesDetails(company.id, this.pagination).subscribe(response => {
                  if(response.status == 200 && response.body) {
                      this.employeeList = response.body.content;
                      this.pagination.currentPage = response.body.page.number;
                      this.pagination.totalItems = response.body.page.totalElements;
                  }
              })
          }
          this.isEmployeesLoading = false;
      })
  }

  fireEmployee(id: number) {

  }

  openAddEmployeeModal(): void {
      this.isAddEmployeeModalOpen = true;
  }

  closeAddEmployeeModal() {
      this.isAddEmployeeModalOpen = false;
  }

  protected onPaginationChanged() {
      this.initEmployeeList();
  }

  protected onRoleChanged($event: DropDownListItem) {
      const role: string = $event.content;
      console.log(role);
  }
}
