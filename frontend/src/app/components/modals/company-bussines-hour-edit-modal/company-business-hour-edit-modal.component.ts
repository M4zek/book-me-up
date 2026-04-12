import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {DecimalPipe, NgForOf, NgIf} from "@angular/common";
import {CompanyHours} from "../../../model/http/company.model";
import {CompanyService} from "../../../service/company.service";
import {CompanyContextService} from "../../../service/company-context.service";
import {ToastService} from "../../../service/toast.service";

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
export class CompanyBusinessHourEditModalComponent implements OnChanges{
  @Input() isVisible = false;
  @Output() closeModal = new EventEmitter<void>();

  @Input() days: CompanyHours[] = [];
  days_copy: CompanyHours[] = [];

  constructor(
      private toast: ToastService,
      private ctx: CompanyContextService,
      private companyService: CompanyService) {}

  ngOnChanges(changes: SimpleChanges): void {
      if(changes['days']){
          this.days_copy = this.days.map(day => ({ ...day }));
      }
  }

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
      if(this.isOpeningHoursChanged()){
          let company_id = this.ctx.getCompany()?.id;
          if(company_id){
              let days = this.findChangedDays();
              this.companyService.updateCompanyOpeningHours(company_id, days).subscribe({
                  next: (response) => {
                      if(response.body && response.status === 200){
                          this.toast.show("Updated successfully.", "success");
                      }
                  }, error: (err) => {
                      this.toast.show("Error updating company details.", "error");
                      console.log(err);
                  }, complete: () => {
                      this.close();
                  }
              })
          } else {
              console.error("Error during read data")
          }
      }
  }




    isOpeningHoursChanged(): boolean {

        const days_copy_map = new Map(
            this.days_copy.map(item => [item.dayOfWeek, item])
        );

        return this.days.some(item => {
            const day_copy = days_copy_map.get(item.dayOfWeek);

            if (!day_copy) return true;

            return (
                item.openTime !== day_copy.openTime ||
                item.closeTime !== day_copy.closeTime ||
                item.open !== day_copy.open
            );
        });
    }




  findChangedDays(): CompanyHours[]{
      let changed_days: CompanyHours[] = [];

      const days_copy_map = new Map(
          this.days_copy.map(item => [item.dayOfWeek, item])
      );

      this.days.forEach(day => {
          const day_copy = days_copy_map.get(day.dayOfWeek);

          if(!day_copy){ return; }

          if (
              day.openTime !== day_copy.openTime ||
              day.closeTime !== day_copy.closeTime ||
              day.open !== day_copy.open
          ) {
              changed_days.push(day);
          }
      });

      return changed_days;
  }

}
