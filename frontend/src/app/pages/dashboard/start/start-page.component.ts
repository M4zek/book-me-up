import {Component} from '@angular/core';
import {TopBarComponent} from "../../../components/top-bar/top-bar.component";
import {CategoryListComponent} from "../../../components/category-list/category-list.component";

@Component({
  selector: 'app-start',
  imports: [
    TopBarComponent,
    CategoryListComponent
  ],
  templateUrl: './start-page.component.html',
  styleUrl: './start-page.component.css'
})
export class StartPage {

}
