import {Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {DropDownListComponent, DropDownListItem} from "../drop-down-list/drop-down-list.component";
import {EmployeeDropDownItem} from "../../model/gui/gui.model";
import {ReservationResponse, ReservationUpdateRequest} from "../../model/http/reservation.model";
import {DatePipe, NgIf} from "@angular/common";
import {DoubleSpinnerComponent} from "../double-spinner/double-spinner.component";
import {CompanyContextService} from "../../service/company-context.service";
import {CompanyService} from "../../service/company.service";
import {ConfirmService} from "../../service/confirm.service";


@Component({
  selector: 'app-company-management-appointments-item',
    imports: [
        DropDownListComponent,
        NgIf,
        DoubleSpinnerComponent,
        DatePipe
    ],
  templateUrl: './company-management-appointments-item.component.html',
  styleUrl: './company-management-appointments-item.component.css'
})
export class CompanyManagementAppointmentsItemComponent implements OnChanges {

  @Input() isEditable = false;
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

  @Input() employeeDropDownList: EmployeeDropDownItem[] = []
  @Input() reservation!: ReservationResponse


  reservationChanged: boolean = false;
  reservationUpdateRequest: ReservationUpdateRequest = {
      reservation_id: -1,
      status: '',
      preferred_employee_id: -1
  };

  company_id: number = -1;


  constructor(
      private confirmService: ConfirmService,
      private ctx: CompanyContextService,
      private companyService: CompanyService) {}

  ngOnChanges(changes: SimpleChanges): void {
      if(changes['reservation'] && this.employeeDropDownList.length > 0) {
        this.setSelectedStatusAndEmployee();
        let company_id = this.ctx.getCompany()?.id;
        if (company_id) {
            this.company_id = company_id;
            this.reservationUpdateRequest = {
                reservation_id: this.reservation.id,
                status: this.reservation.status,
                preferred_employee_id: this.reservation.preferredEmployee?.id ? this.reservation.preferredEmployee.id : undefined
            }
        }
      }
  }

  protected setSelectedStatusAndEmployee(){
      let reservationStatus = this.reservation.status;
      let prefEmpl = this.reservation.preferredEmployee;

      if(prefEmpl){
            this.selectedEmployee = {id: prefEmpl.id, content: prefEmpl.firstName + " " + prefEmpl.lastName, image: prefEmpl.avatar ? prefEmpl.avatar : undefined};
      } else {
          this.selectedEmployee = {id: 0, content: 'None'};
      }

      let statusRes =  this.statusDropDownList.find(status => status.content.toLowerCase() === reservationStatus.toLowerCase());
      this.selectedStatus = statusRes ? statusRes : {id: 0, content: ''};

      this.reservationChanged = false;
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
    this.reservationUpdateRequest.status = $event.content;

    this.reservationChanged = this.reservationUpdateRequest.status?.toLowerCase() != this.reservation.status.toLowerCase();
  }

  onPreferredEmployeeChanged($event: DropDownListItem) {
    this.selectedEmployee = $event;
    this.reservationUpdateRequest.preferred_employee_id = $event.id;

    this.reservationChanged = this.reservationUpdateRequest.preferred_employee_id != this.reservation.preferredEmployee?.id;
  }

  protected async confirm() {
      let result = await this.confirmService.open("Confirm to continue...");
      if (result) {
          console.log(this.reservationUpdateRequest);
      } else {
          this.setSelectedStatusAndEmployee();
      }
  }

  protected reset() {
      this.setSelectedStatusAndEmployee();
  }
}
