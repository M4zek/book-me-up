import {Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {NgIf} from "@angular/common";
import {OfferManagementItem} from "../../model/gui/gui.model";
import {CompanyOffersResponse} from "../../model/http/company.model";
import {ConfirmService} from "../../service/confirm.service";

@Component({
  selector: 'app-company-management-offer-item',
  imports: [
    FormsModule,
    NgIf
  ],
  templateUrl: './company-management-offer-item.component.html',
  styleUrl: './company-management-offer-item.component.css'
})
export class CompanyManagementOfferItemComponent implements OnChanges {

  toEdit = true;

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

  constructor(private confirm: ConfirmService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if(changes['offer']) {
      this.changesOffer = {...this.offer};
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
        console.log("Changed data!");
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

}
