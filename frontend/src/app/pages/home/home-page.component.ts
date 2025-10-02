import {Component} from '@angular/core';
import {CategoryListComponent} from "../../components/category-list/category-list.component";
import {Router, RouterOutlet} from "@angular/router";
import {FormsModule} from "@angular/forms";
import {DropDownListComponent} from "../../components/drop-down-list/drop-down-list.component";
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
    constructor(private router: Router) {}

    nameValue: string = '';
    placeValue: string = '';
    categoryValue: string = '';

    onSearch() {
        const params: SearchCompanyResult = {}

        if(this.nameValue.length > 0){
            params.name = this.nameValue;
        }

        if(this.placeValue.length > 0){
            params.place = this.placeValue ;
        }

        if(this.categoryValue.length > 0 && this.categoryValue != 'None'){
            params.category = this.categoryValue ;
        }

        this.router.navigate(
            ['guest/home/search'],
            { queryParams: params }
        )
    }

    changeCategory($event: string) {
        this.categoryValue = $event;
    }
}
