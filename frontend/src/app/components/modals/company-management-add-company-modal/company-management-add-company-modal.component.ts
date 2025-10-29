import {Component, EventEmitter, Input, Output} from '@angular/core';
import {NgIf} from "@angular/common";
import {FormsModule, NgForm, ReactiveFormsModule} from "@angular/forms";
import {DropDownListComponent, DropDownListItem} from "../../drop-down-list/drop-down-list.component";
import {Address} from "../../../model/gui/gui.model";

export interface CompanyDetails {
  avatar: string;
  name: string;
  description: string;
  address: Address;
  category: string;
  owner_id: number;
}

@Component({
  selector: 'app-company-management-add-company-modal',
  imports: [
    NgIf,
    ReactiveFormsModule,
    FormsModule,
    DropDownListComponent
  ],
  templateUrl: './company-management-add-company-modal.component.html',
  styleUrl: './company-management-add-company-modal.component.css'
})
export class CompanyManagementAddCompanyModalComponent {

  @Input() isVisible: boolean = false;
  @Output() closeModal = new EventEmitter<void>();

  companyDetails: CompanyDetails = {
    avatar: '', description: '', name: '', category: '', owner_id: 0,
    address: {
      city: '', postalCode: '', street: '', buildingNumber: ''
    }
  };

  close(){
    this.closeModal.emit();
  }

  onSubmit(companyForm: NgForm) {
    if(companyForm.invalid){
      companyForm.control.markAllAsTouched();
      return;
    }
  }

  onClear(){
    this.companyDetails = {
      avatar: '', description: '', name: '', category: '', owner_id: 0,
      address: {
        city: '', postalCode: '', street: '', buildingNumber: ''
      }
    };
  }

  onCategoryChanged($event: DropDownListItem) {
    this.companyDetails.category = $event.content;
  }
}
