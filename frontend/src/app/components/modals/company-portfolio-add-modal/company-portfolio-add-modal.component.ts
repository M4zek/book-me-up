import {Component, ElementRef, EventEmitter, Input, Output, ViewChild} from '@angular/core';
import {NgIf} from "@angular/common";
import {CompanyPortfolioListComponent} from "../../company-portfolio-list/company-portfolio-list.component";
import {PortfolioModel} from "../../../model/gui/gui.model";
import {DoubleSpinnerComponent} from "../../double-spinner/double-spinner.component";
import {ToastService} from "../../../service/toast.service";
import {CompanyContextService} from "../../../service/company-context.service";
import {CompanyService} from "../../../service/company.service";
import {ConfirmService} from "../../../service/confirm.service";


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
  fileToSend: File[] = [];

  constructor(
      private ctx: CompanyContextService,
      private companyService: CompanyService,
      private confirmServices: ConfirmService,
      private toast: ToastService) {}


  close() {
    this.imagesToSend = [];
    this.fileToSend = [];
    this.closeModal.emit();
  }

  async confirm() {
    if(this.fileToSend.length > 0 && this.fileToSend.length === this.imagesToSend.length) {

        let result = await this.confirmServices.open(`Are you sure you want to save additional ${this.fileToSend.length} images?`);

        if (!result) {return}

        let company_id = this.ctx.getCompany()?.id;

        if (company_id) {
            this.companyService.uploadNewImagesToCompanyPortfolio(company_id, this.fileToSend).subscribe({
                next: (response) => {
                    if(response.status === 200 && response.body){

                        // Add new images to main list (Update in home view portfolio list)
                        response.body.forEach(img => {
                            this.portfolioList.push({
                                id: img.id,
                                name: img.filename,
                                photo: img.image
                            })
                        })

                        this.toast.show("Images successfully uploaded", "success");
                    } else {
                        this.toast.show("Something went wrong", "error");
                    }
                }, error: (error) => {
                    console.error(error);
                    this.toast.show(`Something went wrong [${error.status}]`, "error");
                }, complete: () => {
                    this.close();
                }
            })
        }

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

      // Add file to list -> send into server
      this.fileToSend.push(file);
    });
    input.value = '';
  }
}
