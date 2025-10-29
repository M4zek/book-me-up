import {Component} from '@angular/core';
import {ImageListComponent} from "../../components/image-list/image-list.component";
import {CompanyOfferListComponent} from "../../components/company-offer-list/company-offer-list.component";
import {MapComponent} from "../../components/map/map.component";
import {CompanyEmployeeListComponent} from "../../components/company-employee-list/company-employee-list.component";
import {CompanyBusinessHoursComponent} from "../../components/company-business-hours/company-business-hours.component";
import {CompanyOpinionsComponent} from "../../components/company-opinions/company-opinions.component";
import {Address} from "../../model/gui/gui.model";

@Component({
  selector: 'app-company-view',
    imports: [
        ImageListComponent,
        CompanyOfferListComponent,
        MapComponent,
        CompanyEmployeeListComponent,
        CompanyBusinessHoursComponent,
        CompanyOpinionsComponent
    ],
  templateUrl: './company-view.component.html',
  styleUrl: './company-view.component.css'
})
export class CompanyViewComponent {

  companyName: string = 'Default company name';
  companyAddress: Address = {
      city: 'Warszawa',
      postalCode: '00-901',
      street: 'Defilad',
      buildingNumber: '1'
  };
  companyDescription: string = 'Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry\'s standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum';



  likeCompany(){
    console.log("Liked company")
  }

  shareCompany(){
    console.log("Shared company")
  }

}
