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
import {ConfirmService} from "../../../service/confirm.service";
import {ToastService} from "../../../service/toast.service";
import {getUserAvatar} from "../../../utils.functions";
import {FormsModule} from "@angular/forms";
import {SearchAndSortBarComponent} from "../../../components/search-bar/search-and-sort-bar.component";


export interface EmployeeSearchModel{
    firstName: string;
    lastName: string | null;
}

@Component({
  selector: 'app-company-management-employee',
    imports: [
        PaginatorComponent,
        NgForOf,
        DropDownListComponent,
        CompanyAddEmployeeModalComponent,
        DoubleSpinnerComponent,
        NgIf,
        FormsModule,
        SearchAndSortBarComponent
    ],
  templateUrl: './company-management-employee.component.html',
  styleUrl: './company-management-employee.component.css'
})
export class CompanyManagementEmployeeComponent implements OnInit {
  protected readonly getUserAvatar = getUserAvatar;

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
  company_id: number | null = null;
  searchModel!: EmployeeSearchModel;

  constructor(private ctx: CompanyContextService,
              private toast: ToastService,
              private confirmService: ConfirmService,
              private companyService: CompanyService) {
  }

  ngOnInit() {
    this.roles = Object.values(HiredEmployeeRole).map((role, index) => ({
        id: index + 1,
        content: role
    }));

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
      this.ctx.currentCompany$.subscribe(company => {
          if(company) {
            this.company_id = company.id;
            this.isDataEditable = this.ctx.isOwnerLoggedIn();
            this.searchEmployees();
          }
      })
  }

  async fireEmployee(employee: CompanyEmployeeDetailsResponse) {
      const result = await this.confirmService.open(`Are you sure to fire ${employee.firstName} ${employee.lastName} ?`);
      const company_id = this.ctx.getCompany()?.id;

      if(result && company_id) {
          const employee_id = employee.id;

          this.companyService.fireEmployee(company_id, employee_id).subscribe({
              next: (result) => {
                  if(result.status == 200) {
                      this.employeeList = this.employeeList.filter(employee => employee.id !== employee_id);
                      this.toast.show(`Employee ${employee.firstName} ${employee.lastName} successfully fired`, "success");
                  }
              }, error: (err) => {
                  console.log(err);
                  this.toast.show("Something went wrong!", "error");
              }
          })
      }

  }

  openAddEmployeeModal(): void {
      this.isAddEmployeeModalOpen = true;
  }

  closeAddEmployeeModal() {
      this.isAddEmployeeModalOpen = false;
  }

  protected onPaginationChanged() {
      this.searchEmployees();
  }

  // Listen to changed role event.
  async onRoleChanged($event: DropDownListItem, employee: CompanyEmployeeDetailsResponse) {
      const role: string =  `COMPANY_${$event.content.toUpperCase()}`;

      const refEmpl = {...employee}

      if(role!==employee.role_in_company.toUpperCase()) {
          const message = `Are you sure you want to change the roles from 
          ${employee.role_in_company.split("_")[1].toLowerCase()} 
          to ${role.split("_")[1].toLowerCase()} for ${employee.firstName} ${employee.lastName}?`

          const result = await this.confirmService.open(message);
          let company_id = this.ctx.getCompany()?.id;

          if (result && company_id) {
              this.companyService.updateEmployeeRole(company_id, employee.id, role).subscribe({
                  next: result => {
                      if (result.status == 200 && result.body) {
                          const updatedEmployee = result.body as CompanyEmployeeDetailsResponse;
                          this.onEmployeeChanged(updatedEmployee);
                          this.toast.show("Role has been changed","success");
                      }
                  }, error: error => {
                      console.error(error);
                      this.onEmployeeChanged(refEmpl);
                  }
              })
              return
          }
      }
      this.onEmployeeChanged(refEmpl);
  }


  protected onEmployeeChanged(employee: CompanyEmployeeDetailsResponse) {
      const index = this.employeeList.findIndex(
          e => e.id === employee.id
      );

      console.log(index);

      if (index !== -1) {
          this.employeeList[index] = employee;
      }
  }


  protected onSearchChanged($event: string) {
      const [firstName, lastName] = $event.split(" ");

      this.searchModel = {
          firstName: firstName,
          lastName: lastName
      }

      this.searchEmployees();
  }


  private searchEmployees(){
      if(this.company_id){
          this.isEmployeesLoading = true;
          this.companyService.getCompanyEmployeesDetails(this.company_id, this.pagination, this.searchModel).subscribe(response => {
              if(response.status == 200 && response.body) {
                  this.employeeList = response.body.content;
                  this.pagination.currentPage = response.body.page.number;
                  this.pagination.totalItems = response.body.page.totalElements;
              }
              this.isEmployeesLoading = false;
          })
      } else {
          console.error("Ups... Something went wrong!");
      }
  }
}
