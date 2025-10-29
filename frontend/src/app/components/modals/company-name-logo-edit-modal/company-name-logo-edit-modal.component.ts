import {Component, ElementRef, EventEmitter, Input, OnChanges, Output, SimpleChanges, ViewChild} from '@angular/core';
import {NgIf} from "@angular/common";
import {CompanyNameAndLogo} from "../../../model/gui/gui.model";
import {FormsModule} from "@angular/forms";
import {ToastService} from "../../../service/toast.service";

@Component({
  selector: 'app-company-name-logo-edit-modal',
  imports: [
    NgIf,
    FormsModule
  ],
  templateUrl: './company-name-logo-edit-modal.component.html',
  styleUrl: './company-name-logo-edit-modal.component.css'
})
export class CompanyNameLogoEditModalComponent implements OnChanges{
  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;

  @Input() isVisible = false;
  @Output() closeModal = new EventEmitter<void>();

  @Input() logoAndName: CompanyNameAndLogo = {
    logo: 'images/default_logo_company.png',
    logoName: '',
    companyName: ''
  }
  copyLogoAndName: CompanyNameAndLogo = {
    logo: 'images/default_logo_company.png',
    logoName: '',
    companyName: ''
  }

  constructor(private toast: ToastService) {}

  ngOnChanges(changes: SimpleChanges) {
    if (changes['logoAndName']) {
      this.copyLogoAndName = { ...this.logoAndName };
    }
  }

  openDialogFile() {
    this.fileInput.nativeElement.click();
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];

    if (!file) return;

    if (!file.type.startsWith('image/')) {
      this.toast.show("File must be a image", 'error');
      this.resetFile();
      return;
    }

    const maxSize = 2 * 1024 * 1024;
    if (file.size > maxSize) {
      this.toast.show("The image can be up to 2 MB in size.", 'error');
      this.resetFile();
      return;
    }

    const reader = new FileReader();
    reader.onload = () => {
      this.copyLogoAndName.logo = reader.result as string;
      this.copyLogoAndName.logoName = file.name;
    };
    reader.readAsDataURL(file);
  }

  private resetFile() {
    this.copyLogoAndName.logo = this.logoAndName.logo;
    this.copyLogoAndName.logoName = this.logoAndName.logoName;
  }

  close() {
    this.copyLogoAndName = { ...this.logoAndName };
    this.closeModal.emit();
  }

  confirmEdit() {
    if(this.isDataChange(this.logoAndName, this.copyLogoAndName)) {
      console.log(this.copyLogoAndName, this.logoAndName);
      this.logoAndName.logo = this.copyLogoAndName.logo;
      this.logoAndName.logoName = this.copyLogoAndName.logoName;
      this.logoAndName.companyName = this.copyLogoAndName.companyName;
      this.close();
    } else {
      this.toast.show('No changes have been made', 'info')
    }
  }


  isDataChange(oldData: CompanyNameAndLogo, newData: CompanyNameAndLogo): boolean {
    for (const key of Object.keys(oldData) as (keyof CompanyNameAndLogo)[]) {
      if (oldData[key] !== newData[key]) {
        return true;
      }
    }
    return false;
  }
}
