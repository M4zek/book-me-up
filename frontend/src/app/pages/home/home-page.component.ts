import {Component, OnInit} from '@angular/core';
import {CategoryListComponent} from "../../components/category-list/category-list.component";
import {Router, RouterOutlet} from "@angular/router";
import {FormsModule} from "@angular/forms";
import {DropDownListComponent, DropDownListItem} from "../../components/drop-down-list/drop-down-list.component";
import {CategoryService} from "../../service/category.service";
import {ErrorMessage} from "../../model/http/error.model";
import {CategoryResponse} from "../../model/http/company.model";
import {UserContextService} from "../../service/user-context.service";
import {SearchCompanyOptions} from "../../model/search/search.model";


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

    searchOption: SearchCompanyOptions = {}

    constructor(private categoryService: CategoryService,
                private router: Router,
                private userContextService: UserContextService) {}

    ngOnInit(): void {
        this.getCategoriesFromApi();
    }

    onSearch() {
        Object.keys(this.searchOption).forEach((key) => {
            const value = this.searchOption[key as keyof SearchCompanyOptions];
            if (value === '') {
                this.searchOption[key as keyof SearchCompanyOptions] = null;
            }
        });

        this.userContextService.isLoggedIn().subscribe(isLoggedIn => {
            if (isLoggedIn) {
                this.router.navigate(['/app/home/search'], {queryParams: this.searchOption})
                    .then(r => console.log("Redirect to APP/home/search: ",r));
            } else {
                this.router.navigate(['/guest/home/search'],  {queryParams: this.searchOption})
                    .then(r => console.log("Redirect to GUEST/home/search:: ",r));
            }
        })
    }

    changeCategory($event: DropDownListItem) {
        if ($event.content === "None") {
            this.searchOption.category = null;
        } else {
            this.searchOption.category = $event.content;
        }
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
