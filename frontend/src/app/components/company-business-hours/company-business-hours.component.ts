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

    findDayOfWeek(): CompanyHours  | undefined {
      const todayName = new Date().toLocaleDateString("en-US", {weekday: "long"});
      return this.openingHours.find(
          item => item.dayOfWeek == todayName
      );
    }

    calculateProgressToTimeEnd(d: CompanyHours): number {
      let totalTime = this.getMinutesDifference(d.openTime, d.closeTime);
      let currentTime = this.getCurrentTime();

      let timeBetweenCurrentAndStart = this.getMinutesDifference(d.openTime, currentTime);

      let percent = (timeBetweenCurrentAndStart / totalTime) * 100;

      if(percent >= 100) percent = 100;
      if(percent <= 0) percent = 0;

      return percent;
    }


    protected getCurrentTime(){
      const now = new Date();

      const hours = String(now.getHours()).padStart(2, '0');
      const minutes = String(now.getMinutes()).padStart(2, '0');

      return `${hours}:${minutes}`;
    }

    protected getMinutesDifference(start: string, end: string): number {
      const [sh, sm] = start.split(':').map(Number);
      const [eh, em] = end.split(':').map(Number);

      const startMinutes = sh * 60 + sm;
      let endMinutes = eh * 60 + em;

      if (endMinutes < startMinutes) {
        endMinutes += 24 * 60;
      }

      return endMinutes - startMinutes;
    }

    protected isOpen(d: CompanyHours) {
      return d.open && this.isBetweenTwoTime(d.openTime, d.closeTime);
    }

    protected isBetweenTwoTime(start: string, end: string){
      const [sh, sm] = start.split(':').map(Number);
      const [eh, em] = end.split(':').map(Number);
      const [ch, cm] = this.getCurrentTime().split(':').map(Number);


      const startMinutes = sh * 60 + sm;
      const endMinutes = eh * 60 + em;
      const currentMinutes =  ch * 60 + cm;

      return (startMinutes <= currentMinutes) && (currentMinutes <= endMinutes);
    }

    protected getCurrentTimePosition(percentage: number) {
      if(percentage >= 0 && percentage <= 5){
        return 0;
      }
      if(percentage > 95 && percentage <= 100){
        return -30;
      }
      return -14.5;
    }


    protected textWhyClosed(d: CompanyHours): string {
        if(d.open){
            if(this.isBeforeTime(d.openTime)){
                return "The company isn't open yet today";
            } else if(this.isAfterTime(d.closeTime)){
                return "Closed today"
            } else {
                return ""
            }
        } else {
            return `It's closed`;
        }
    }


    protected isBeforeTime(time: string) {

        const [ch, cm] = this.getCurrentTime().split(':').map(Number);
        const [sh, sm] = time.split(':').map(Number);

        const currentMinutes = ch * 60 + cm;
        const openMinutes = sh * 60 + sm;

        return currentMinutes <= openMinutes;
    }

    protected isAfterTime(time: string) {

        const [ch, cm] = this.getCurrentTime().split(':').map(Number);
        const [sh, sm] = time.split(':').map(Number);

        const currentMinutes = ch * 60 + cm;
        const openMinutes = sh * 60 + sm;

        return currentMinutes >= openMinutes;
    }




}