import {Component, OnInit} from '@angular/core';
import {PaginatorComponent} from "../../../components/paginator/paginator.component";
import {Pagination} from "../../../model/search/search.model";
import {NgForOf} from "@angular/common";
import {DropDownListComponent, DropDownListItem} from "../../../components/drop-down-list/drop-down-list.component";
import {HiredEmployeeData, HiredEmployeeRole} from "../../../model/gui/gui.model";
import {
    CompanyAddEmployeeModalComponent
} from "../../../components/modals/company-add-employee-modal/company-add-employee-modal.component";


@Component({
  selector: 'app-company-management-employee',
    imports: [
        PaginatorComponent,
        NgForOf,
        DropDownListComponent,
        CompanyAddEmployeeModalComponent
    ],
  templateUrl: './company-management-employee.component.html',
  styleUrl: './company-management-employee.component.css'
})
export class CompanyManagementEmployeeComponent implements OnInit {

  isAddEmployeeModalOpen = false;

  roles: DropDownListItem[] = []


  pagination: Pagination = {
    totalItems: 50,
    itemsPerPage: 10,
    currentPage: 1,
    itemsPerPageOptions: [5, 10, 25, 50, 100],
  }

  users: HiredEmployeeData[] = [
    {
      id: 1,
      name: 'John Doe',
      role: 'Admin',
      email: 'someemail@@gm.com',
      phone: '+45 222-333-444',
      photo: 'images/user_default_avatar.png'
    },
    {
      id: 2,
      name: 'John Doe',
      role: 'Owner',
      email: 'someemail@@gm.com',
      phone: '+45 222-333-444',
      photo: 'images/user_default_avatar.png'
    }
  ];

  ngOnInit() {
    this.roles = Object.values(HiredEmployeeRole).map(role => ({
      content: role
    }));

    this.pagination.totalItems = this.users.length;

  }

  onPageChange(page: number) {
    this.pagination.currentPage = page;
    console.log(this.pagination);
  }

  onItemsPerPageChange(count: number) {
    this.pagination.itemsPerPage = count;
    this.pagination.currentPage = 1;
    console.log(this.pagination);

  }

  getEmployeeRole(role: string) {
    let found = this.roles.find(item => item.content === role);
    if (found) {
      return found;
    } else {
      found = {
        content: '',
      }
      return found;
    }
  }

  fireEmployee(id: number) {

  }

  openAddEmployeeModal(): void {
      this.isAddEmployeeModalOpen = true;
  }

  closeAddEmployeeModal() {
      this.isAddEmployeeModalOpen = false;
  }
}
