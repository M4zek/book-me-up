import {Component, OnInit} from '@angular/core';
import {CategoryListComponent} from "../../components/category-list/category-list.component";
import {RouterOutlet} from "@angular/router";
import {FormsModule} from "@angular/forms";
import {DropDownListComponent, DropDownListItem} from "../../components/drop-down-list/drop-down-list.component";
import {CategoryService} from "../../service/category.service";
import {CategoryResponse, ErrorMessage} from "../../model/response.model";

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
export class HomePage implements OnInit {
    dropDownListItems: DropDownListItem[] = [];

    nameValue: string = '';
    placeValue: string = '';
    categoryValue: string = '';

    constructor(private categoryService: CategoryService) {}

    ngOnInit(): void {
        this.getCategoriesFromApi();
    }

    onSearch() {

    }

    changeCategory($event: DropDownListItem) {
        this.categoryValue = $event.content;
        console.log(this.categoryValue);
    }

    protected getCategoriesFromApi() {
        this.dropDownListItems = [];
        this.categoryService.getCategories().subscribe({
            next: (response) => {
                if(response.status === 200 && response.body) {
                    const categories: CategoryResponse[] = response.body.content;
                    this.dropDownListItems = categories.map((category: CategoryResponse) => {
                        return {
                            id: category.id,
                            content: category.name,
                        }
                    })
                }
            }, error: (error) => {
                const message: ErrorMessage = error.error;
                console.error(message);
            }
        })
    }



}
