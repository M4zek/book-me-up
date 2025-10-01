import {Component} from '@angular/core';
import {NgClass, NgForOf, NgIf} from "@angular/common";

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
  openingHours = [
    {
      dayOfWeek: "Monday",
      isOpen: true,
      openTime: "10:00",
      closeTime: "12:00",
    },
    {
      dayOfWeek: "Tuesday",
      isOpen: true,
      openTime: "10:00",
      closeTime: "12:00",
    },
    {
      dayOfWeek: "Wednesday",
      isOpen: true,
      openTime: "10:00",
      closeTime: "12:00",
    },
    {
      dayOfWeek: "Thursday",
      isOpen: true,
      openTime: "10:00",
      closeTime: "12:00",
    },
    {
      dayOfWeek: "Friday",
      isOpen: true,
      openTime: "10:00",
      closeTime: "12:00",
    },
    {
      dayOfWeek: "Saturday",
      isOpen: false,
      openTime: "10:00",
      closeTime: "12:00",
    },
    {
      dayOfWeek: "Sunday",
      isOpen: false,
      openTime: "10:00",
      closeTime: "12:00",
    }
  ]
}