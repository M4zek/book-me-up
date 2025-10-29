import {Component, Input, OnInit} from '@angular/core';
import {DropDownListComponent, DropDownListItem} from "../drop-down-list/drop-down-list.component";
import {EmployeeDropDownItem} from "../../model/gui/gui.model";

export interface AppointmentItem {
  id: number;
  name: string;
  user: string;
  date: string;
  price: number;
  preferredEmployee: DropDownListItem,
  status: DropDownListItem,
}


@Component({
  selector: 'app-company-management-appointments-item',
    imports: [
        DropDownListComponent
    ],
  templateUrl: './company-management-appointments-item.component.html',
  styleUrl: './company-management-appointments-item.component.css'
})
export class CompanyManagementAppointmentsItemComponent implements OnInit {

  @Input() selectedStatus: DropDownListItem = { content: ''};
  @Input() selectedEmployee: DropDownListItem = { content: '' };

  statusDropDownList: DropDownListItem[] = [
    {
      content: 'Pending', image: 'icons/pending_icon.svg'
    }, {
      content: 'Accepted', image: 'icons/accepted_icon.svg'
    }, {
      content: 'Rejected', image: 'icons/reject_icon.svg'
    }, {
      content: 'Realized', image: 'icons/realized_icon.svg'
    }, {
      content: 'Canceled', image: 'icons/canceled_icon.svg'
    },
  ]

  @Input() employeeDropDownList: EmployeeDropDownItem[] = [
    {
      id: 1, firstName: 'John', lastName: 'Doe', avatar: 'images/user_default_avatar.png'
    }, {
      id: 2, firstName: 'Max', lastName: 'Tree', avatar: 'images/user_default_avatar.png'
    }, {
      id: 3, firstName: 'Matty', lastName: 'Bush', avatar: 'images/user_default_avatar.png'
    }, {
      id: 4, firstName: 'Mathew', lastName: 'Jordan', avatar: 'images/user_default_avatar.png'
    }, {
      id: 5, firstName: 'Luis', lastName: 'Hamilton', avatar: 'images/user_default_avatar.png'
    }
  ]


  ngOnInit(): void {
    this.getRandomStatus()
    this.getRandomEmployee()
  }

  getRandomStatus(){
    this.selectedStatus = this.statusDropDownList[Math.floor(Math.random() * this.statusDropDownList.length)];
  }

  getRandomEmployee(){
    let employee = this.employeeDropDownList[Math.floor(Math.random() * this.employeeDropDownList.length)]
    this.selectedEmployee = {
      id: employee.id,
      content: employee.firstName + ' ' + employee.lastName,
      image: employee.avatar,
    }
  }

  toDropDownList() {
    let dropDownList: DropDownListItem[] = this.employeeDropDownList
        .map(item => {
          return {
            id: item.id,
            content: `${item.firstName} ${item.lastName}`,
            image: item.avatar,
          }
        })
    return dropDownList;
  }

  onStatusChanged($event: DropDownListItem) {
    this.selectedStatus = $event;
  }

  onPreferredEmployeeChanged($event: DropDownListItem) {
    this.selectedEmployee = $event;
  }
}
