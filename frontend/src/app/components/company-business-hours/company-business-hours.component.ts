import {Component, Input} from '@angular/core';
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {CompanyHours} from "../../model/http/company.model";

@Component({
  selector: 'app-company-business-hours',
  imports: [
    NgForOf,
    NgClass,
    NgIf
  ],
  templateUrl: './company-business-hours.component.html',
  styleUrl: './company-business-hours.component.css'
})
export class CompanyBusinessHoursComponent {
  @Input() openingHours: CompanyHours[] = []


    isToday(dayOfWeek: string) {
        const todayName = new Date().toLocaleDateString("en-US", {weekday: "long"});
        return dayOfWeek == todayName;
    }
}