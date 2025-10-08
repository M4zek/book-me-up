import {Component, EventEmitter, Input, Output} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {DecimalPipe, NgForOf, NgIf} from "@angular/common";

@Component({
  selector: 'app-company-business-hour-edit-modal',
  imports: [
    FormsModule,
    DecimalPipe,
    NgForOf,
    NgIf
  ],
  templateUrl: './company-business-hour-edit-modal.component.html',
  styleUrl: './company-business-hour-edit-modal.component.css'
})
export class CompanyBusinessHourEditModalComponent {
  @Input() isVisible = false;
  @Output() closeModal = new EventEmitter<void>();

  @Input() days = [
    { name: 'Monday', openHour: 10, openMinute: 0, closeHour: 12, closeMinute: 0, closed: false },
    { name: 'Tuesday', openHour: 10, openMinute: 0, closeHour: 12, closeMinute: 0, closed: false },
    { name: 'Wednesday', openHour: 10, openMinute: 0, closeHour: 12, closeMinute: 0, closed: false },
    { name: 'Thursday', openHour: 10, openMinute: 0, closeHour: 12, closeMinute: 0, closed: false },
    { name: 'Friday', openHour: 10, openMinute: 0, closeHour: 12, closeMinute: 0, closed: false },
    { name: 'Saturday', openHour: 10, openMinute: 0, closeHour: 12, closeMinute: 0, closed: false },
    { name: 'Sunday', openHour: 0, openMinute: 0, closeHour: 0, closeMinute: 0, closed: true },
  ];

  adjustTime(day: any, field: string, delta: number) {
    day[field] += delta;

    if (field.includes('Hour')) {
      if (day[field] > 23) day[field] = 0;
      if (day[field] < 0) day[field] = 23;
    } else {
      if (day[field] >= 60) day[field] = 0;
      if (day[field] < 0) day[field] = 55;
    }
  }

  close() {
    this.closeModal.emit();
  }

  confirmEdit() {

  }
}
