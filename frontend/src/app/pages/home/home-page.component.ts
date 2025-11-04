import {Component} from '@angular/core';
import {CategoryListComponent} from "../../components/category-list/category-list.component";
import {Router, RouterOutlet} from "@angular/router";
import {FormsModule} from "@angular/forms";
import {DropDownListComponent, DropDownListItem} from "../../components/drop-down-list/drop-down-list.component";
import {SearchCompanyResult} from "../../model/search/search.model";

@Component({
  selector: 'app-home',
    imports: [
        CategoryListComponent,
        RouterOutlet,
        FormsModule,
        DropDownListComponent
    ],
  templateUrl: './home-page.component.html',
  styleUrl: './home-page.component.css'
})
export class HomePage {
    constructor() {}

    nameValue: string = '';
    placeValue: string = '';
    categoryValue: string = '';

    onSearch() {

    }

    changeCategory($event: DropDownListItem) {
        this.categoryValue = $event.content;
    }
}
