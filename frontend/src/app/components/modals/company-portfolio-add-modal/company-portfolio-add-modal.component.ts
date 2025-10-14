import {Component, ElementRef, EventEmitter, Input, Output, ViewChild} from '@angular/core';
import {NgIf} from "@angular/common";
import {CompanyPortfolioListComponent} from "../../company-portfolio-list/company-portfolio-list.component";
import {PortfolioModel} from "../../../model/gui/gui.model";
import {DoubleSpinnerComponent} from "../../double-spinner/double-spinner.component";
import {ToastService} from "../../../service/toast.service";


@Component({
  selector: 'app-company-portfolio-add-modal',
  imports: [
    NgIf,
    CompanyPortfolioListComponent,
    DoubleSpinnerComponent
  ],
  templateUrl: './company-portfolio-add-modal.component.html',
  styleUrl: './company-portfolio-add-modal.component.css'
})
export class CompanyPortfolioAddModalComponent{
  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;

  @Input() isVisible: boolean = false;
  @Output() closeModal = new EventEmitter<void>();

  @Input() portfolioList: PortfolioModel[] = [];
  imagesToSend: PortfolioModel[] = [];

  constructor(private toast: ToastService) {}


  close() {
    this.imagesToSend = [];
    this.closeModal.emit();
  }

  confirm() {
    if(this.imagesToSend.length > 0) {

      // tmp copy added images to input images
      this.imagesToSend.forEach(element => {
        this.portfolioList.push(element);
      })
      // Send request to add new photo

    } else {
      this.toast.show('You haven\'t added any new photos.', 'warning')
    }
  }

  openFileDialog() {
    if(this.imagesToSend.length < 10) {
      this.fileInput.nativeElement.click();
    } else {
      this.toast.show('You can have up to 10 photos', 'warning');
    }
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    const files = input.files;

    if (!files || files.length === 0) return;

    const maxSize = 2 * 1024 * 1024; // 2 MB

    const newFileArray = Array.from(files);
    if((newFileArray.length + this.imagesToSend.length + this.portfolioList.length) > 10) {
      this.toast.show('Your total current and selected photos exceed the maximum number (10)', 'error');
      return;
    }

    newFileArray.forEach((file) => {
      if (!file.type.startsWith('image/')) {
        this.toast.show(`File "${file.name}" must be an image.`, 'error');
        return;
      }

      if (file.size > maxSize) {
        this.toast.show(`"${file.name}" can be up to 2 MB in size.`, 'error');
        return;
      }

      const reader = new FileReader();
      reader.onload = () => {
        const newPortfolioItem: PortfolioModel = {
          id: Math.random(),
          name: file.name,
          photo: reader.result as string
        };
        this.imagesToSend.push(newPortfolioItem);
      };
      reader.readAsDataURL(file);
    });

    input.value = '';
  }
}
