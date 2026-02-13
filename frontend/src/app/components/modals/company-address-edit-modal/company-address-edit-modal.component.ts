import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {NgIf} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {Address} from "../../../model/gui/gui.model";
import {MapComponent} from "../../map/map.component";
import {ToastService} from "../../../service/toast.service";
import {CompanyContextService} from "../../../service/company-context.service";
import {CompanyService} from "../../../service/company.service";


@Component({
  selector: 'app-company-address-edit-modal',
  imports: [
    NgIf,
    FormsModule,
    MapComponent
  ],
  templateUrl: './company-address-edit-modal.component.html',
  styleUrl: './company-address-edit-modal.component.css'
})
export class CompanyAddressEditModalComponent implements OnInit {
  @Input() isVisible = false;
  @Output() closeModal = new EventEmitter<void>();
  @ViewChild(MapComponent) mapComponent!: MapComponent;

  isAddressFound: boolean = false;

  @Input() currentLocalization: Address = {
    postalCode: '',
    city: '',
    street: '',
    buildingNumber: '',
  };

  newLocalization: Address = {
    postalCode: '',
    city: '',
    street: '',
    buildingNumber: '',
  }

  constructor(private toast: ToastService, private ctx: CompanyContextService, private companyService: CompanyService) { }

  ngOnInit(): void {
    this.newLocalization = {...this.currentLocalization};
  }

  close() {
      this.isAddressFound = false;
      this.closeModal.emit();
  }

  confirmEdit() {
      if(this.isAddressChanged(this.currentLocalization, this.newLocalization)){
          if(this.isAddressComplete(this.newLocalization)) {
              let company_id = this.ctx.getCompany()?.id;
              if(company_id){
                  this.companyService.updateCompanyAddress(company_id, this.newLocalization).subscribe({
                      next: (response) => {
                          if(response.status === 200 && response.body) {
                              this.overrideCurrentLocalization(response.body);
                              this.toast.show("Address Updated successfully.", "success");
                          }
                      }, error: (error) => {
                          console.log(error);
                          this.toast.show(`Error during update address. CODE: ${error.status}}`, 'error');
                      }, complete: () => {
                          this.close();
                      }
                  })
              }
          }
      }

  }

  isAddressChanged(oldAddress: Address, newAddress: Address): boolean {
    for (const key of Object.keys(oldAddress) as (keyof Address)[]) {
      if (oldAddress[key] !== newAddress[key]) {
        return true;
      }
    }
    return false;
  }

  private overrideCurrentLocalization(newLocalization: Address) {
    for(const key of Object.keys(this.currentLocalization) as (keyof Address)[]) {
      this.currentLocalization[key] = newLocalization[key];
    }
  }

  private isAddressComplete(address: Address | null): boolean {
    if (!address) return false;
    return Object.values(address).every(value => value.trim() !== '');
  }

  onResultMapAddressChange(result: boolean) {
    this.isAddressFound = result;
  }

  searchNewAddress() {
    if(this.isAddressChanged(this.currentLocalization, this.newLocalization)){
      if(this.isAddressComplete(this.newLocalization)){
        this.mapComponent.updateAddress(this.newLocalization);
      } else {
        this.toast.show("Incorrect address", "warning");
      }
    } else {
      this.toast.show("Address not changed", "warning");
    }
  }
}
