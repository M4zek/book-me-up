import {Component, EventEmitter, Input, Output} from '@angular/core';
import {NgIf} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {OfferManagementItem} from "../../../model/gui/gui.model";

@Component({
  selector: 'app-company-add-offer-modal',
  imports: [
    NgIf,
    FormsModule
  ],
  templateUrl: './company-add-offer-modal.component.html',
  styleUrl: './company-add-offer-modal.component.css'
})
export class CompanyAddOfferModalComponent {
  @Input() isVisible: boolean = false;
  @Output() closeModal = new EventEmitter<void>();

  offerData: OfferManagementItem = {
    name: '',
    duration: 0,
    price: 0,
    description: ''
  }

  close() {
    this.resetDataForm();
    this.closeModal.emit();
  }

  onSubmit(form: any) {
    if (form.invalid) {
      form.control.markAllAsTouched();
      return;
    }

    console.log(this.offerData);
  }



  resetDataForm(){
    this.offerData = {
      name: '',
      duration: 0,
      price: 0,
      description: ''
    }
  }
}
