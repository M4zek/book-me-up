import {Component, OnInit} from '@angular/core';
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {DoubleSpinnerComponent} from "../double-spinner/double-spinner.component";
import {CategoryService} from "../../service/category.service";
import {Pagination, SearchCompanyOptions} from "../../model/search/search.model";
import {PaginatorComponent} from "../paginator/paginator.component";
import {interval, Subscription, take} from "rxjs";
import {CategoryResponse} from "../../model/http/company.model";
import {Router} from "@angular/router";
import {UserContextService} from "../../service/user-context.service";

@Component({
  selector: 'app-category-list',
    imports: [
        NgClass,
        NgIf,
        NgForOf,
        DoubleSpinnerComponent,
        PaginatorComponent
    ],
  templateUrl: './category-list.component.html',
  styleUrl: './category-list.component.css'
})
export class CategoryListComponent implements OnInit {
    protected readonly encodeURIComponent = encodeURIComponent;
    private categoryIntervalSub?: Subscription;

    drop_down_icon :string = 'icons/drop_down_arrow.png';

    isDropdownOpen = false;

    searchOption: SearchCompanyOptions = {}
    categoryList: CategoryResponse[] = []
    pagination: Pagination = {
        totalItems: 0,
        itemsPerPage: 10,
        currentPage: 0,
        itemsPerPageOptions: [5, 10, 15, 25]
    }

    constructor(private categoryService: CategoryService,
                private userContextService: UserContextService,
                protected router: Router) {}

    ngOnInit(): void {
        this.getCategories();
    }

    toggleDropdown() {
      this.isDropdownOpen = !this.isDropdownOpen;
    }

    protected onPaginationChange() {
        this.getCategories();
    }

    private getCategories(){
        if (this.categoryIntervalSub) {
            this.categoryIntervalSub.unsubscribe();
        }

        this.categoryList = [];

        this.categoryService.getCategories(this.pagination).subscribe({
            next: (response) =>{
                if(response.status === 200 && response.body){
                        const categories = response.body.content;
                        this.pagination.totalItems = response.body.page.totalElements;
                        this.pagination.currentPage = response.body.page.number;

                        this.categoryIntervalSub = interval(100)
                            .pipe(take(categories.length))
                            .subscribe(i => {
                                this.categoryList.push(categories[i]);
                            });
                }
            },
            error: (error) => {
                const message = error.error;
                console.log(message);
            }
        })
    }

    protected onCategoryClick(name: string) {

        this.searchOption.category = name;

        this.userContextService.isLoggedIn().subscribe(userLogged => {
            if (userLogged) {
                this.router.navigate(['/app/home/search'], {queryParams: this.searchOption})
                    .then(r => console.log("Redirect to APP/home/search: ",r));
            } else {
                this.router.navigate(['/guest/home/search'],  {queryParams: this.searchOption})
                    .then(r => console.log("Redirect to GUEST/home/search:: ",r));
            }
        })
    }
}
