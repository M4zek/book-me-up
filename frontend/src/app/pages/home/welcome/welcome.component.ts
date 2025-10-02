import { Component } from '@angular/core';
import {CompanyListComponent} from "../../../components/company-list-item/company-list/company-list.component";

@Component({
  selector: 'app-welcome',
    imports: [
        CompanyListComponent
    ],
  templateUrl: './welcome.component.html',
  styleUrl: './welcome.component.css'
})
export class WelcomeComponent {
 // TODO MAKE A REQUEST TO THE BACKEND TO SEARCH MOST POPULAR COMPANIES
}
