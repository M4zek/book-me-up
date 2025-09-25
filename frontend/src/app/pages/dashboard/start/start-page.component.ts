import {Component} from '@angular/core';
import {TopBarComponent} from "../../../components/top-bar/top-bar.component";
import {CategoryListComponent} from "../../../components/category-list/category-list.component";
import {CompanyListComponent} from "../../../components/company-list/company-list.component";

@Component({
  selector: 'app-start',
    imports: [
        TopBarComponent,
        CategoryListComponent,
        CompanyListComponent
    ],
  templateUrl: './start-page.component.html',
  styleUrl: './start-page.component.css'
})
export class StartPage {
    img_1_path: string = '/images/img_1.png';
}
