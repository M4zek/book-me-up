import {Component, EventEmitter, Input, Output} from '@angular/core';
import {NgIf} from "@angular/common";
import {DateReservationPickerComponent} from "../date-reservation-picker/date-reservation-picker.component";
import {DropDownListComponent} from "../drop-down-list/drop-down-list.component";
import {FormsModule} from "@angular/forms";

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
export class ReservationModalComponent {
  @Input() isVisible = false;
  @Output() closeModal = new EventEmitter<void>();

  startDate: string = 'No selected'
  isConfirmedRegistrationDetails: boolean = false;


  @Input() reservationData: ReservationData = {
    user_id: 1,
    firstName: 'John',
    lastName:'Doe',
    email:'john.doe@mail.com',
    phoneNumber: '888333222',
    offer: {
      id: 1,
      name: 'Man haircut',
      duration: 45,
      price: 30,
    }
  }

  close() {
    this.closeModal.emit();
  }

  onDateChange($event: any) {
    let date: Date = $event.start;
    this.startDate = this.createTextWithReservationDate(date);

  }

  changePreferredEmployee($event: string) {
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

}
