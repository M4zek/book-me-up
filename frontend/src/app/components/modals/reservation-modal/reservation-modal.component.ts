import {Component, EventEmitter, Input, OnChanges, OnDestroy, Output, SimpleChanges} from '@angular/core';
import {formatDate, NgIf} from "@angular/common";
import {
    ChangeDateEvent,
    ChangeDateEventType,
    DateReservationPickerComponent
} from "../../date-reservation-picker/date-reservation-picker.component";
import {DropDownListComponent, DropDownListItem} from "../../drop-down-list/drop-down-list.component";
import {FormsModule} from "@angular/forms";
import {CompanyOffersResponse, EmployeeSummaryResponse} from "../../../model/http/company.model";
import {UserResponse} from "../../../model/http/user.model";
import {UserContextService} from "../../../service/user-context.service";
import {ReservationService} from "../../../service/reservation.service";
import {
    AvailableReservationSlots,
    AvailableSlot,
    ReservationRequest,
    ReservationResponse
} from "../../../model/http/reservation.model";
import {ToastService} from "../../../service/toast.service";
import {ReservationAvailability} from "../../../model/gui/gui.model";


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
export class ReservationModalComponent implements OnChanges, OnDestroy {

  @Input() isVisible = false;
  @Output() closeModal = new EventEmitter<void>();

  @Input() companyId: number = 0;

  @Input() offer: CompanyOffersResponse = {
      id: 0, name: '' , description: '', duration: 45, price: 30,
  }

  userData!: UserResponse | null;
  reservationResponse!: ReservationResponse | null;

  isReservationConfirmed: boolean = false;

  reservationRequest: ReservationRequest = {
      company_offer_id: 0, user_id: 0,
      reservation_date: undefined, preferred_employee_id: undefined,
  }

  protected responseStatus: number | undefined = undefined;
  protected loadingStatus: boolean = false;


  protected slots: AvailableReservationSlots | null = null;
  protected fromDate: Date = new Date();

  protected selectedSlot: AvailableSlot | null = null;
  protected selectedDay: Date | null = null;

  constructor(private toast: ToastService,
              private reservationService: ReservationService,
              private userContextService: UserContextService) {}


  ngOnDestroy(): void {
      this.responseStatus = undefined;
      this.loadingStatus = false;
      this.isReservationConfirmed = false;
      this.selectedSlot = null;
      this.selectedDay = null;
      this.userData = null;
      this.reservationResponse = null;
  }

  ngOnChanges(changes: SimpleChanges): void {
      if (changes['isVisible']) {
          if(this.isVisible){
              this.userContextService.getUserData().subscribe(user => {
                  this.userData = user;
                  this.reservationRequest.user_id = this.userData.id;
                  this.reservationRequest.company_offer_id = this.offer.id;

                  this.fromDate = this.getStartOfWeek()
                  this.getReservationSlotsRequest();
              })
          }
      }
  }

  protected getReservationSlotsRequest() {
      const toDate = new Date(this.fromDate);
      toDate.setDate(toDate.getDate() + 7);

      let reservationAvailabilityRequestData: ReservationAvailability = {
          companyId: this.companyId,
          fromDate: this.fromDate,
          toDate: toDate,
          duration: this.offer.duration,
      }

      this.loadingStatus = true;
      this.reservationService.getReservationSlots(reservationAvailabilityRequestData).subscribe({
          next: response => {
              if(response.status == 200 && response.body) {
                  this.slots = response.body
              }
          }, error: error => {
              console.error(error);
              this.toast.show("Something went wrong! Try again later", "error");
              this.close();
          }, complete: () => {
              this.loadingStatus = false
          }
      })
  }


  close() {
      this.ngOnDestroy();
      this.closeModal.emit();
  }

  onDateChange($event: ChangeDateEvent) {
      switch ($event.type){
          case ChangeDateEventType.SET_DAY:
              let day: Date = $event.payload.date;
              this.selectedDay = day;
              this.selectedSlot = null;
              break;

          case ChangeDateEventType.SET_SLOT:
              let slot: AvailableSlot = $event.payload.slot;
              this.selectedSlot = slot;
              this.updateReservationData(this.selectedDay, this.selectedSlot);
              break;

          case ChangeDateEventType.NEXT_WEEK:
              this.fromDate = this.addDays(this.fromDate, 7);
              this.getReservationSlotsRequest()
              break;

          case ChangeDateEventType.PREV_WEEK:
              this.fromDate = this.addDays(this.fromDate, -7);
              this.getReservationSlotsRequest()
              break;

          case ChangeDateEventType.NEXT_MONTH:
              this.fromDate = this.addMonths(this.fromDate, 1);
              this.getReservationSlotsRequest()
              break;

          case ChangeDateEventType.PREV_MONTH:
              this.fromDate = this.addMonths(this.fromDate, -1);
              this.getReservationSlotsRequest()
              break;
      }
  }

  changePreferredEmployee($event: DropDownListItem) {
      this.reservationRequest.preferred_employee_id =
          $event.content == 'None' || $event.id == undefined ? undefined : $event.id;
  }

  confirmReservation() {
      if (this.isReservationDataValid()) {
          this.reservationService.makeAnReservation(this.reservationRequest)
              .subscribe({
                  next: response => {
                      if (response.status == 200 && response.body) {
                          this.reservationResponse = response.body;
                          this.responseStatus = response.status;

                          this.toast.show("Reservation successfully created", "success");
                      } else {
                          this.responseStatus = response.status;
                      }
                  },
                  error: error => {
                      switch (error.status) {
                          case 409:
                              this.toast.show("A similar reservation already exists [Offer and time]", "error");
                              break;

                          default:
                              this.toast.show("Something went wrong!", "error");
                              break;
                      }
                  }
              });
      }
  }



  protected isReservationDataValid() {
      return (
          this.isReservationConfirmed &&
          this.reservationRequest.preferred_employee_id &&
          this.reservationRequest.company_offer_id !== 0 &&
          this.reservationRequest.user_id !== 0 &&
          this.reservationRequest.reservation_date !== undefined
      );
  }

  protected updateReservationData(baseDate: Date | null, slot: AvailableSlot){
      if (!baseDate || !slot?.start) {
          throw new Error('Invalid input data');
      }

      const result = new Date(baseDate);

      const [hours, minutes] = slot.start.split(':').map(Number);

      result.setHours(hours, minutes, 0, 0);

      this.reservationRequest.reservation_date= formatDate(result, "yyyy-MM-dd'T'HH:mm:ss", 'en-US')
  }

  convertEmployeeToDropData(employees: EmployeeSummaryResponse[]): DropDownListItem[] {
      return employees.map(employee => {
         let elem: DropDownListItem = {
             id: employee.id,
             content: `${employee.firstName} ${employee.lastName}`,
             image: employee.avatar
         }
         return elem;
      });
  }


  protected addDays(date: Date, days: number) {
      const result = new Date(date);
      result.setDate(result.getDate() + days);
      return result;
  }



  protected addMonths(date: Date, months: number): Date {
      let result = new Date(date);
      result.setMonth(result.getMonth() + months);
      result = this.getStartOfWeek(result);

      return result;
  }


  protected getStartOfWeek(date: Date = new Date()): Date {
      const today = new Date();

      if(today > date){
          date = today;
      }

      const d = new Date(date);
      const day = d.getDay();

      const diff = (day === 0 ? -6 : 1) - day;

      d.setDate(d.getDate() + diff);
      d.setHours(0, 0, 0, 0);

      return d;
  }

}
