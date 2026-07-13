import {Component, Input} from '@angular/core';
import {NgForOf, NgIf} from "@angular/common";
import {PortfolioModel} from "../../model/gui/gui.model";
import {CompanyContextService} from "../../service/company-context.service";
import {CompanyService} from "../../service/company.service";
import {ConfirmService} from "../../service/confirm.service";
import {ToastService} from "../../service/toast.service";

@Component({
  selector: 'app-company-portfolio-list',
    imports: [
        NgForOf,
        NgIf
    ],
  templateUrl: './company-portfolio-list.component.html',
  styleUrl: './company-portfolio-list.component.css'
})
export class CompanyPortfolioListComponent {

  @Input() isEditable = false;
  @Input() images: PortfolioModel[] = [];


  constructor(
      private confirm: ConfirmService,
      private toastService: ToastService,
      private ctx: CompanyContextService,
      private companyService: CompanyService) {
  }


  async removeImage(photo_id: number, img_index: number) {
      let result = await this.confirm.open("Are you sure to delete this image?");
      let company_id = this.ctx.getCompany()?.id;

      if (!company_id) { return }

      if(result){
          this.companyService.deleteImageFromCompanyPortfolio(company_id, photo_id).subscribe({
              next: (result) => {
                  if(result.status === 200){
                      this.images.splice(img_index, 1);
                      this.toastService.show("Photo successfully deleted!", "success");
                  }
              }, error: (err) => {
                  this.toastService.show(`Error while deleting this image! [${err.status}]`, "error");
                  console.log(err);
              }
          })
      }
  }

    getImgUrl(index: number){
        return this.images[index].photo;
    }
}
