import { Component } from '@angular/core';
import {StatCardComponent} from "../../../components/stat-card/stat-card.component";
import {BarChartComponent} from "../../../components/bar-chart/bar-chart.component";
import {NewCategoryFormComponent} from "../../../components/new-category-form/new-category-form.component";
import {
    CategoryManagementListComponent
} from "../../../components/category-management-list/category-management-list.component";
import {PaginatorComponent} from "../../../components/paginator/paginator.component";

@Component({
  selector: 'app-admin-categories',
    imports: [
        StatCardComponent,
        BarChartComponent,
        NewCategoryFormComponent,
        CategoryManagementListComponent,
        PaginatorComponent
    ],
  templateUrl: './admin-categories.component.html',
  styleUrl: './admin-categories.component.css'
})
export class AdminCategoriesComponent {

}
