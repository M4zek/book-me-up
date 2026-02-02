import {Component, EventEmitter, Input, Output} from '@angular/core';
import {NgForOf, NgIf} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {PaginatorComponent} from "../../paginator/paginator.component";
import {Pagination} from "../../../model/search/search.model";
import {CompanyContextService} from "../../../service/company-context.service";
import {CompanyService} from "../../../service/company.service";
import {UserService} from "../../../service/user.service";
import {UserHireDetails} from "../../../model/http/user.model";
import {DoubleSpinnerComponent} from "../../double-spinner/double-spinner.component";


@Component({
  selector: 'app-company-add-employee-modal',
    imports: [
        NgIf,
        FormsModule,
        PaginatorComponent,
        NgForOf,
        DoubleSpinnerComponent
    ],
  templateUrl: './company-add-employee-modal.component.html',
  styleUrl: './company-add-employee-modal.component.css'
})
export class CompanyAddEmployeeModalComponent {
    @Input() isVisible: boolean = false;
    @Output() closeModal = new EventEmitter<void>();

    searchModel = {
        firstName: '',
        lastName: '',
    };

    searchEmployeeList: UserHireDetails[] = [];
    selectedEmployee: UserHireDetails[] = [];

    paginator: Pagination = {
        totalItems: this.searchEmployeeList.length,
        itemsPerPage: 5,
        currentPage: 1,
        itemsPerPageOptions: [5,10,15,20]
    }

    isUsersLoading = false;

    constructor(private ctx: CompanyContextService,
                private companyService: CompanyService,
                private userService: UserService) {
    }

    close() {
        this.closeModal.emit();
    }

    confirm() {

    }

    addEmployeeToSelected(employee: UserHireDetails) {
        const alreadyAdded = this.selectedEmployee.some(emp => emp.id === employee.id);
        if (!alreadyAdded) {
            this.selectedEmployee.push(employee);
        }
    }

    removeEmployeeFromSelected(employeeIdToRemove: number) {
        this.selectedEmployee = this.selectedEmployee.filter(
            emp => emp.id !== employeeIdToRemove);

    }

    isEmployeeSelected(employeeId: number): boolean {
        return this.selectedEmployee.some(emp => emp.id === employeeId);
    }

    isEmployeeAlreadyHired(employeeId: number): boolean {
        let company_id = this.ctx.getCompany()?.id;
        if (company_id) {
            return this.searchEmployeeList.some(emp => emp.companyIds.some(id => id === employeeId));
        }
        return false;
    }

    protected onSearchClick() {

        let company_id = this.ctx.getCompany()?.id;

        if(this.isSearchModelCorrect() && company_id) {
            this.isUsersLoading = true;
            this.userService.searchUsersByNameAndSurname(this.searchModel, this.paginator).subscribe({
                next: (response) => {
                    if(response.body && response.status === 200) {
                        this.searchEmployeeList = response.body.content;
                        this.paginator.currentPage = response.body.page.number;
                        this.paginator.totalItems = response.body.page.totalElements;
                    }

                    this.isUsersLoading = false;
                }, error: (err) => {
                    this.isUsersLoading = false;
                    console.log(err);
                }
            })
        }
    }

    // Simple method to check field is not empty
    protected isSearchModelCorrect() {
        return this.searchModel.lastName !== '' || this.searchModel.firstName !== '';
    }
}
