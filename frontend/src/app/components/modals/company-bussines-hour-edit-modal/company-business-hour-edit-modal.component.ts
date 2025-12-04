import {Component, EventEmitter, Input, Output} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {DecimalPipe, NgForOf, NgIf} from "@angular/common";
import {CompanyHours} from "../../../model/http/company.model";

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

    @Input() days: CompanyHours[] = [];

    adjustTime(companyHours: CompanyHours, field: 'openTime' | 'closeTime', part: 'hour' | 'minute', delta: number) {
        const [hourStr, minuteStr] = companyHours[field].split(':');
        let hour = Number(hourStr);
        let minute = Number(minuteStr);

        if (part === 'hour') {
            hour += delta;
            if (hour > 23) hour = 0;
            if (hour < 0) hour = 23;
        } else {
            minute += delta;
            if (minute >= 60) minute = 0;
            if (minute < 0) minute = 55;
        }

        companyHours[field] =
            `${hour.toString().padStart(2,'0')}:${minute.toString().padStart(2,'0')}`;
    }

  close() {
    this.closeModal.emit();
  }

  confirmEdit() {

  }
}
