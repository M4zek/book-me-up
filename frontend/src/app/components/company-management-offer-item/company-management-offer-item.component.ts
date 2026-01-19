import {Component, ElementRef, HostListener, Input, OnChanges, SimpleChanges} from '@angular/core';
import {FormsModule, NgForm} from "@angular/forms";
import {NgIf} from "@angular/common";
import {OfferManagementItem} from "../../model/gui/gui.model";
import {CompanyOfferRequest, CompanyOffersResponse} from "../../model/http/company.model";
import {ConfirmService} from "../../service/confirm.service";
import {CompanyService} from "../../service/company.service";
import {ToastService} from "../../service/toast.service";
import {DoubleSpinnerComponent} from "../double-spinner/double-spinner.component";

@Component({
  selector: 'app-company-management-offer-item',
    imports: [
        FormsModule,
        NgIf,
        DoubleSpinnerComponent
    ],
  templateUrl: './company-management-offer-item.component.html',
  styleUrl: './company-management-offer-item.component.css'
})
export class CompanyManagementOfferItemComponent implements OnChanges {

  toEdit = true;
  isLoading = false;

  @Input() company_id: number = -1;
  @Input() offer: CompanyOffersResponse = {
    id: 1,
    name: 'Man Haircut',
    description: 'Description',
    price: 29.99,
    duration: 30,
  }

  changesOffer: OfferManagementItem = {
    id: 0,
    name: '',
    description: '',
    price: 0,
    duration: 0,
  }

  @Input() isEditable = false;

  constructor(
      private el: ElementRef,
      private confirm: ConfirmService,
      private toast: ToastService,
      private companyService: CompanyService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if(changes['offer']) {
      this.changesOffer = {...this.offer};
    }
  }

  @HostListener('document:click', ['$event.target'])
  onClick(target: HTMLElement) {
      if (!this.el.nativeElement.contains(target)) {
          this.cancelEdit();
      }
  }

  async removeOffer(offer: CompanyOffersResponse) {
        const result = await this.confirm.open(`Are you sure to archive "${offer.name}"?`);
        if(result) {
            console.log(result);
        }
  }

  cancelEdit() {
    this.changesOffer = {...this.offer};
    this.toEdit = true;
  }

  edit() {
    this.toEdit = false;
  }

  async confirmChanges() {
    const result = await this.confirm.open(`Are you sure to change information?`);

    if(result) {
        let body: CompanyOfferRequest = {
            name: this.changesOffer.name,
            description: this.changesOffer.description,
            price: this.changesOffer.price,
            duration: this.changesOffer.duration,
        }

        this.isLoading = true;
        this.companyService.updateCompanyOffer(body, this.company_id, this.offer.id).subscribe(
            {
                next: response => {
                    if(response.status === 200 && response.body){
                        this.offer = response.body;
                    } else {
                        this.changesOffer = {...this.offer};
                        this.toast.show(`Ups. Something went wrong. [${response.status}]`, 'warning');
                    }
                    this.isLoading = false;
                }, error: err => {
                    console.log(err);
                    this.changesOffer = {...this.offer};
                    this.toast.show("Ups... Something went wrong", 'error');
                    this.isLoading = false;
                }
            }
        )
        this.toEdit = true;
    }
  }


  isOfferChanged(): boolean {
    for (const key of Object.keys(this.changesOffer) as (keyof OfferManagementItem)[]) {
      if(this.changesOffer[key] !== this.offer[key]) {
        return true;
      }
    }
    return false;
  }


    protected onSubmit(offerForm: NgForm) {
        if(offerForm.invalid){
            offerForm.control.markAllAsTouched();
            return;
        }
    }
}
