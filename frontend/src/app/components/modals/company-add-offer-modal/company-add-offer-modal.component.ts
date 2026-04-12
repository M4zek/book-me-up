import {Component, EventEmitter, Input, Output} from '@angular/core';
import {NgIf} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {CompanyOfferRequest} from "../../../model/http/company.model";
import {CompanyService} from "../../../service/company.service";
import {CompanyContextService} from "../../../service/company-context.service";
import {ToastService} from "../../../service/toast.service";

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

  offerData: CompanyOfferRequest = {
    name: '',
    duration: 0,
    price: 0,
    description: ''
  }

  constructor(
      private toast: ToastService,
      private companyService: CompanyService,
      private ctx: CompanyContextService) {}

  close() {
    this.resetDataForm();
    this.closeModal.emit();
  }

  onSubmit(form: any) {
    if (form.invalid) {
      form.control.markAllAsTouched();
      return;
    }
  }

  createOffer() {
      let company_id: number | undefined = this.ctx.getCompany()?.id;
      if(company_id) {
          if(this.ctx.hasAnyRole("COMPANY_MANAGER", "COMPANY_OWNER")){
              this.companyService.createNewCompanyOffer(this.offerData, company_id).subscribe(response => {
                  if(response.status === 200){
                      this.toast.show("Offer successfully created");
                      this.close();
                  } else {
                      this.toast.show("Can't create offer",'error');
                  }
              })
          } else {
              this.toast.show("Unauthorized!", "warning");
          }
      }
      else {
          console.log("Company not selected!")
      }
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
