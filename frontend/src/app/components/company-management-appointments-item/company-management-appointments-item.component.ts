import {Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {DropDownListComponent, DropDownListItem} from "../drop-down-list/drop-down-list.component";
import {EmployeeDropDownItem} from "../../model/gui/gui.model";
import {ReservationResponse, ReservationUpdateModel} from "../../model/http/reservation.model";
import {DatePipe, NgIf} from "@angular/common";
import {DoubleSpinnerComponent} from "../double-spinner/double-spinner.component";
import {CompanyContextService} from "../../service/company-context.service";
import {ConfirmService} from "../../service/confirm.service";
import {ReservationService} from "../../service/reservation.service";
import {ToastService} from "../../service/toast.service";


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
      content: 'Cancelled', image: 'icons/canceled_icon.svg'
    },
  ]

  @Input() employeeDropDownList: EmployeeDropDownItem[] = []
  @Input() reservation!: ReservationResponse

  reservationChanged: boolean = false;
  reservationUpdateRequest: ReservationUpdateModel = {
      company_id: -1,
      reservation_id: -1,
      request: {
          status: '',
          preferred_employee_id: -1
      }
  };


  constructor(
      private toast: ToastService,
      private confirmService: ConfirmService,
      private ctx: CompanyContextService,
      private reservationService: ReservationService) {}

  ngOnChanges(changes: SimpleChanges): void {
      if(changes['reservation'] && this.employeeDropDownList.length > 0) {
        this.setSelectedStatusAndEmployee();
        let company_id = this.ctx.getCompany()?.id;
        if (company_id) {
            this.reservationUpdateRequest = {
                company_id: company_id,
                reservation_id: this.reservation.id,
                request: {
                    status: this.reservation.status,
                    preferred_employee_id: this.reservation.preferredEmployee?.id ? this.reservation.preferredEmployee.id : undefined
                }
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
    this.reservationUpdateRequest.request.status = $event.content.toUpperCase();

    this.reservationChanged = this.reservationUpdateRequest.request.status?.toLowerCase() != this.reservation.status.toLowerCase();
  }

  onPreferredEmployeeChanged($event: DropDownListItem) {
    this.selectedEmployee = $event;
    this.reservationUpdateRequest.request.preferred_employee_id = $event.id;

    this.reservationChanged = this.reservationUpdateRequest.request.preferred_employee_id != this.reservation.preferredEmployee?.id;
  }

  protected async confirm() {
      let result = await this.confirmService.open("Confirm to continue...");
      if (result) {
          this.reservationService.patchReservation(this.reservationUpdateRequest)
              .subscribe({
                  next: result => {
                    if(result.body && result.status === 200) {
                        this.reservation = result.body;
                        this.reservationChanged = false;
                        this.toast.show("Reservation successfully changed", 'success');
                    } else {
                        this.toast.show("Reservation change was unsuccessful", 'warning');
                    }
                  }, error: err => {
                      switch (err.status) {
                          case 400:
                              let err_text = err.error.message;
                              this.toast.show(err_text, 'warning');
                              break;

                          default:
                              this.toast.show("Ups... Something went wrong!", 'error');
                              break;
                      }
                      console.error(err.error.message);
                      this.reset();
                  }
              })
      } else {
          this.setSelectedStatusAndEmployee();
      }
  }

  protected reset() {
      this.setSelectedStatusAndEmployee();
  }
}
