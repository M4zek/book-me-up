import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {NgIf} from "@angular/common";
import {DateReservationPickerComponent} from "../../date-reservation-picker/date-reservation-picker.component";
import {DropDownListComponent, DropDownListItem} from "../../drop-down-list/drop-down-list.component";
import {FormsModule} from "@angular/forms";
import {CompanyOffersResponse, EmployeeSummaryResponse} from "../../../model/response/company-response.model";
import {UserResponse} from "../../../model/response/user-response.model";
import {UserContextService} from "../../../service/user-context.service";
import {CompanyService} from "../../../service/company.service";
import {ReservationService} from "../../../service/reservation.service";
import {ReservationRequest} from "../../../model/request/reservation-request.model";


@Component({
    selector: 'app-reservation-modal',
    imports: [
        NgIf,
        DateReservationPickerComponent,
        DropDownListComponent,
        FormsModule
    ],
    templateUrl: './reservation-modal.component.html',
    styleUrl: './reservation-modal.component.css'
})
export class ReservationModalComponent implements OnChanges {

  @Input() isVisible = false;
  @Output() closeModal = new EventEmitter<void>();

  @Input() companyId: number = 0;
  @Input() offer: CompanyOffersResponse = {
      id: 0,
      name: '' ,
      description: '',
      duration: 45,
      price: 30,
  }
  userData!: UserResponse;
  preferredEmployees: DropDownListItem[] = []

  startDate: string = 'No selected'
  isConfirmedRegistrationDetails: boolean = false;

  reservationRequest: ReservationRequest = {
      company_offer_id: 0,
      user_id: 0,
      reservation_date: undefined,
      preferred_employee_id: undefined,
  }

  constructor(private companyService: CompanyService,
              private reservationService: ReservationService,
              private userContextService: UserContextService) {}

  ngOnChanges(changes: SimpleChanges): void {
      if (changes['isVisible']) {
          if(this.isVisible){
              this.userContextService.getUserData().subscribe(user => {
                  this.userData = user;
                  this.reservationRequest.user_id = this.userData.id;
                  this.reservationRequest.company_offer_id = this.offer.id;
                  this.readEmployeeListFromBackend(this.companyId);
              })
          }
      }
  }



  close() {
    this.closeModal.emit();
  }

  onDateChange($event: any) {
    let date: Date = $event.start;
    this.startDate = this.createTextWithReservationDate(date);
    this.reservationRequest.reservation_date = new Date(this.startDate);
  }

  changePreferredEmployee($event: DropDownListItem) {
      if($event.content == "None")
          this.reservationRequest.preferred_employee_id = undefined;
      else
        this.reservationRequest.preferred_employee_id = $event.id;
  }

  confirmReservation() {
      if (this.reservationRequest.reservation_date) {
          console.log(this.reservationRequest);
      } else {
          // TODO SHOW ERROR
      }
  }


  private createTextWithReservationDate(date: Date): string {
    let month = date.toLocaleString('en-US', { month: 'long' });
    let timeStart = date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    let year = date.getFullYear();
    let day = date.getDate();
    return `${timeStart} ${day} ${month} ${year}`;
  }

    private readEmployeeListFromBackend(companyId: number) {
        this.companyService.getCompanyEmployees(companyId).subscribe(response => {
          if (response.status === 200 && response.body) {
              let users: EmployeeSummaryResponse[] = response.body;
              let dropDownEmployeeList: DropDownListItem[] = [];
              for (let user of users) {
                  dropDownEmployeeList.push(
                      {
                          id: user.id,
                          content: `${user.firstName} ${user.lastName}`,
                          image: `data:image/jpeg;base64,${user.avatar}`
                      }
                  )
              }
              this.preferredEmployees = dropDownEmployeeList;
          }  else {
              console.log("Error");
          }
        })
    }
}
