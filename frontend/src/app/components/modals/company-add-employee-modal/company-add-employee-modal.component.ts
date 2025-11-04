import {Component, EventEmitter, Input, Output} from '@angular/core';
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {PaginatorComponent} from "../../paginator/paginator.component";
import {Pagination} from "../../../model/search/search.model";


export interface EmployeeListItem {
    id: number;
    name: string;
    avatar: string;
}

@Component({
  selector: 'app-company-add-employee-modal',
    imports: [
        NgIf,
        FormsModule,
        PaginatorComponent,
        NgForOf,
        NgClass
    ],
  templateUrl: './company-add-employee-modal.component.html',
  styleUrl: './company-add-employee-modal.component.css'
})
export class CompanyAddEmployeeModalComponent {
    @Input() isVisible: boolean = false;
    @Output() closeModal = new EventEmitter<void>();

    searchModel: string = '';

    searchEmployeeList: EmployeeListItem[] = [
        {
            id: 1,
            name: 'John Doe',
            avatar: 'images/user_default_avatar.png',
        },
        {
            id: 2,
            name: 'John Doe',
            avatar: 'images/user_default_avatar.png',
        },
        {
            id: 3,
            name: 'John Doe',
            avatar: 'images/user_default_avatar.png',
        },
        {
            id: 4,
            name: 'John Doe',
            avatar: 'images/user_default_avatar.png',
        },
        {
            id: 5,
            name: 'John Doe',
            avatar: 'images/user_default_avatar.png',
        },
        {
            id: 6,
            name: 'John Doe',
            avatar: 'images/user_default_avatar.png',
        }
    ];

    selectedEmployee: EmployeeListItem[] = [];

    paginator: Pagination = {
        totalItems: this.searchEmployeeList.length,
        itemsPerPage: 5,
        currentPage: 1,
        itemsPerPageOptions: [5,10,15,20]
    }

    close() {
        this.closeModal.emit();
    }

    confirm() {

    }

    addEmployeeToSelected(employee: EmployeeListItem) {
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

}
