import {Component} from '@angular/core';
import {CategoryListComponent} from "../../components/category-list/category-list.component";
import {CompanyListComponent} from "../../components/company-list-item/company-list/company-list.component";

@Component({
  selector: 'app-home',
    imports: [
        CategoryListComponent,
        CompanyListComponent
    ],
  templateUrl: './home-page.component.html',
  styleUrl: './home-page.component.css'
})
export class HomePage {
    img_1_path: string = '/images/img_1.png';
}
