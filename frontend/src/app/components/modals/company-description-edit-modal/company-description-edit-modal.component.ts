import {Component, EventEmitter, Input, Output} from '@angular/core';
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-company-description-edit-modal',
  imports: [
    NgIf
  ],
  templateUrl: './company-description-edit-modal.component.html',
  styleUrl: './company-description-edit-modal.component.css'
})
export class CompanyDescriptionEditModalComponent {
  @Input() isVisible = false;
  @Output() closeModal = new EventEmitter<void>();

  @Input() description: string = '';


  close() {
    this.closeModal.emit();
  }

  confirmEdit() {

  }
}
