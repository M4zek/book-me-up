import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {NgIf} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {ConfirmService} from "../../../service/confirm.service";
import {CompanyContextService} from "../../../service/company-context.service";
import {CompanyService} from "../../../service/company.service";
import {ToastService} from "../../../service/toast.service";

@Component({
  selector: 'app-company-description-edit-modal',
    imports: [
        NgIf,
        FormsModule
    ],
  templateUrl: './company-description-edit-modal.component.html',
  styleUrl: './company-description-edit-modal.component.css'
})
export class CompanyDescriptionEditModalComponent implements OnChanges{

  @Input() isVisible = false;
  @Output() closeModal = new EventEmitter<void>();
  @Output() descriptionChanged = new EventEmitter<string>();

  @Input() description: string = '';
  descriptionCopy: string = '';

  constructor(
      private toast: ToastService,
      private confirm: ConfirmService,
      private ctx: CompanyContextService,
      private companyService: CompanyService) {}

  ngOnChanges(changes: SimpleChanges): void {
      if (changes['description']) {
        this.descriptionCopy = this.description;
      }
  }

  close() {
    this.closeModal.emit();
  }

  async confirmEdit() {
    let result = await this.confirm.open("Are you sure to change company description?");

    let company_id = this.ctx.getCompany()?.id;

    if(!company_id){ return; }

    if (result) {
        this.companyService.updateCompanyDescription(company_id, this.description).subscribe({
            next: (response) => {
                if(response.body && response.status === 200){
                    this.description = response.body.description;
                    this.descriptionCopy = response.body.description;
                    this.toast.show("Description updated successfully.", "success");
                    this.descriptionChanged.emit(this.description);
                } else {
                    this.toast.show("Something went wrong.", "error");
                }
            }, error: (err) => {
                this.toast.show(`Something went wrong - ${err.status}`, "error");
                console.error(err);
            }, complete: () => {
                this.close()
            }
        })
    }
  }



  // Method to calculate color from white to red based on description length. (
  get textColor(): string {
      const max = 2048;

      const ratio = Math.min(Math.max(this.description.length / max, 0), 1);

      const red = 255;
      const green = Math.round(255 * (1 - ratio));
      const blue = Math.round(255 * (1 - ratio));

      return `rgb(${red}, ${green}, ${blue})`;
  }

}
