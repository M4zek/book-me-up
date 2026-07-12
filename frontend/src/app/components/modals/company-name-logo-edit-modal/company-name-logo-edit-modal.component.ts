import {Component, ElementRef, EventEmitter, Input, OnChanges, Output, SimpleChanges, ViewChild} from '@angular/core';
import {NgIf} from "@angular/common";
import {CompanyNameAndLogo} from "../../../model/gui/gui.model";
import {FormsModule} from "@angular/forms";
import {ToastService} from "../../../service/toast.service";
import {CompanyService} from "../../../service/company.service";
import {CompanyContextService} from "../../../service/company-context.service";
import {CompanyDetailsResponse, FileType} from "../../../model/http/company.model";
import {ConfirmService} from "../../../service/confirm.service";
import {MyImgComponent} from "../../my-img/my-img.component";


export interface LogoName {
    name: string | undefined;
    file: File | undefined;
}

@Component({
  selector: 'app-company-name-logo-edit-modal',
    imports: [
        NgIf,
        FormsModule,
        MyImgComponent
    ],
  templateUrl: './company-name-logo-edit-modal.component.html',
  styleUrl: './company-name-logo-edit-modal.component.css'
})
export class CompanyNameLogoEditModalComponent implements OnChanges{
    protected readonly FileType = FileType;


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

  logoName: LogoName = {file: undefined, name: undefined};
  file: File | undefined;


  constructor(
      private confirm: ConfirmService,
      private toast: ToastService,
              private companyService: CompanyService,
              private ctx: CompanyContextService) {}

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
    this.file = input.files?.[0];

    if (!this.file) return;

    if (!this.file.type.startsWith('image/')) {
      this.toast.show("File must be a image", 'error');
      this.resetFile();
      return;
    }

    const maxSize = 2 * 1024 * 1024;
    if (this.file.size > maxSize) {
      this.toast.show("The image can be up to 2 MB in size.", 'error');
      this.resetFile();
      return;
    }

    const reader = new FileReader();

    reader.onload = () => {
        this.copyLogoAndName.logo = reader.result as string;
    };

    reader.readAsDataURL(this.file);

    this.copyLogoAndName.logoName = this.file.name;
  }

  private resetFile() {
    this.copyLogoAndName.logo = this.logoAndName.logo;
    this.copyLogoAndName.logoName = this.logoAndName.logoName;
  }

  close() {
    this.copyLogoAndName = { ...this.logoAndName };

    this.file = undefined;
    this.logoName = {file: undefined, name: undefined}

    this.closeModal.emit();
  }

  async confirmEdit() {
      if(this.isDataChange(this.logoAndName, this.copyLogoAndName)){

          let data: LogoName = {file: undefined, name: undefined};
          let company_id = this.ctx.getCompany()?.id;

          if(this.logoAndName.logo != this.copyLogoAndName.logo){
              data.file = this.file;
          }

          if(this.logoAndName.companyName != this.copyLogoAndName.companyName){
              data.name = this.copyLogoAndName.companyName;
          }

          const result = await this.confirm.open("Are you sure to change company logo or name?")
          if(!result){ return }

          if(company_id){
              this.companyService.updateLogoOrNameInCompany(company_id, data)
                  .subscribe({
                      next: result => {
                          if(result.body && result.status === 200){
                              let response = result.body as CompanyDetailsResponse;
                              this.logoAndName.logo = response.logo_url;
                              this.logoAndName.companyName = response.name;
                              this.close();

                          } else {
                              this.toast.show("Something went wrong", 'error');
                          }
                      }, error: err => {
                          this.toast.show("Something went wrong", 'error');
                          console.error(err);
                      }
                  })

          } else {
           this.toast.show("Ups... Something went wrong.", "error");
          }
      } else {
          this.toast.show("No changes have been made", "info");
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
