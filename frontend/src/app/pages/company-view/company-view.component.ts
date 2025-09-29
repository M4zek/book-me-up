import {Component} from '@angular/core';
import {ImageListComponent} from "../../components/image-list/image-list.component";

@Component({
  selector: 'app-company-view',
  imports: [
    ImageListComponent
  ],
  templateUrl: './company-view.component.html',
  styleUrl: './company-view.component.css'
})
export class CompanyViewComponent {

  companyName: string = 'Default company name';
  companyAddress: string = 'Street 121, New York';



  likeCompany(){
    console.log("Liked company")
  }

  shareCompany(){
    console.log("Shared company")
  }

}
