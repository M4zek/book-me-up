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
import {EmployeeDetailsResponse, FileType} from "../../../model/http/company.model";
import {MyImgComponent} from "../../my-img/my-img.component";


@Component({
  selector: 'app-company-add-employee-modal',
    imports: [
        NgIf,
        FormsModule,
        PaginatorComponent,
        NgForOf,
        DoubleSpinnerComponent,
        MyImgComponent
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

    resultEmployeeList: EmployeeDetailsResponse[] = [];

    paginator: Pagination = {
        totalItems: this.searchEmployeeList.length,
        itemsPerPage: 5,
        currentPage: 0,
        itemsPerPageOptions: [5,10,15,20]
    }

    isUsersLoading = false;

    isHireProcessing = false;

    constructor(private ctx: CompanyContextService,
                private companyService: CompanyService,
                private userService: UserService) {
    }

    close() {
        this.closeModal.emit();
    }

    confirm() {
        let company_id = this.ctx.getCompany()?.id;
        if(this.selectedEmployee.length > 0 && company_id){
            this.isUsersLoading = true;
            let user_ids = this.selectedEmployee.map(employee => {
                return employee.id;
            })
            this.companyService.hireEmployeesToCompany(company_id, user_ids).subscribe({
                next: result => {
                    if(result.body && result.status === 200){
                        this.resultEmployeeList = result.body;
                    }
                    this.isHireProcessing = false;
                }, error: err => {
                    console.log(err);
                    this.isHireProcessing = false;
                }
            })
        }
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

    isEmployeeAlreadyHired(companyIds: number[]): boolean {
        let companyId = this.ctx.getCompany()?.id;
        if (companyId) {
            return companyIds.includes(companyId);
        }
        return false;
    }

    protected onSearchClick() {

        let company_id = this.ctx.getCompany()?.id;

        if(this.isSearchModelCorrect() && company_id) {
            this.searchEmployeeList = [];
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
                    this.clearForm();
                }
            })
        }
    }

    // Simple method to check field is not empty
    protected isSearchModelCorrect() {
        return this.searchModel.lastName !== '' || this.searchModel.firstName !== '';
    }

    protected onPaginatorChanged($event: void) {
        this.onSearchClick();
    }

    protected clearForm() {
        this.searchModel = {
            firstName: '',
            lastName: '',
        }

        this.searchEmployeeList = [];
        this.selectedEmployee = [];
        this.paginator.totalItems = this.searchEmployeeList.length;
    }

    protected onResultClickOkAndResetModal() {
        this.searchModel = { firstName: "", lastName: "" };
        this.isHireProcessing = false;
        this.isUsersLoading = false;
        this.resultEmployeeList = [];
        this.selectedEmployee = [];
        this.searchEmployeeList = [];
    }

    protected readonly FileType = FileType;
}
