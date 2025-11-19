import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {NgIf} from "@angular/common";
import {DateReservationPickerComponent} from "../../date-reservation-picker/date-reservation-picker.component";
import {DropDownListComponent, DropDownListItem} from "../../drop-down-list/drop-down-list.component";
import {FormsModule} from "@angular/forms";
import {
    CompanyHours,
    CompanyOffersResponse,
    EmployeeSummaryResponse
} from "../../../model/response/company-response.model";
import {UserResponse} from "../../../model/response/user-response.model";
import {UserContextService} from "../../../service/user-context.service";
import {CompanyService} from "../../../service/company.service";
import {ReservationService} from "../../../service/reservation.service";

export interface ReservationData {
    user_id: number;
    firstName: string;
    lastName: string;
    email: string;
    phoneNumber: string;
    offer: {
        id: number;
        name: string;
        duration: number;
        price: number;
    }
}

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
  companyHours: CompanyHours[] = []

  startDate: string = 'No selected'
  isConfirmedRegistrationDetails: boolean = false;


  constructor(private companyService: CompanyService,
              private reservationService: ReservationService,
              private userContextService: UserContextService) {}

  ngOnChanges(changes: SimpleChanges): void {
      if (changes['isVisible']) {
          if(this.isVisible){
              this.userContextService.getUserData().subscribe(user => {
                  this.userData = user;
              })
              this.readEmployeeListFromBackend(this.companyId);
              this.readCompanyHours(this.companyId);
          }
      }
  }



  close() {
    this.closeModal.emit();
  }

  onDateChange($event: any) {
    let date: Date = $event.start;
    this.startDate = this.createTextWithReservationDate(date);

  }

  changePreferredEmployee($event: DropDownListItem) {
      console.log($event);
  }

  confirmReservation() {
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
              for (let user of users) {
                  this.preferredEmployees.push(
                      {
                          id: user.id,
                          content: `${user.firstName} ${user.lastName}`,
                          image: `data:image/jpeg;base64,${user.avatar}`
                      }
                  )
              }
          }  else {
              console.log("Error");
          }
        })
    }


    private readCompanyHours(companyId: number) {
      this.companyService.getCompanyBusinessHours(companyId).subscribe(response => {
          if (response.status === 200 && response.body) {
              this.companyHours = response.body;
          } else {
              console.log("Error");
          }
      })

    }
}
