import {Component, OnInit} from '@angular/core';
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {CategoryResponse} from "../../model/response.model";
import {DoubleSpinnerComponent} from "../double-spinner/double-spinner.component";
import {CategoryService} from "../../service/category.service";
import {Page, Pagination} from "../../model/search/search.model";
import {PaginatorComponent} from "../paginator/paginator.component";
import {interval, Subscription, take} from "rxjs";

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
    drop_down_icon :string = 'icons/drop_down_arrow.png';
    isDropdownOpen = false;
    private categoryIntervalSub?: Subscription;

    categoryList: CategoryResponse[] = []

    pagination: Pagination = {
        totalItems: 0,
        itemsPerPage: 10,
        currentPage: 0,
        itemsPerPageOptions: [5, 10, 15, 25]
    }
    constructor(private categoryService: CategoryService) {}

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

        this.categoryService.getCategories(this.pagination).subscribe((response: Page<CategoryResponse>) => {
            const categories = response.content;
            this.pagination.totalItems = response.page.totalElements;
            this.pagination.currentPage = response.page.number;

            this.categoryIntervalSub = interval(100)
                .pipe(take(categories.length))
                .subscribe(i => {
                    this.categoryList.push(categories[i]);
                });
        });
    }
}
