import {Component, signal} from '@angular/core';
import {DatePipe} from "@angular/common";

interface Category {
  id: number;
  name: string;
  companiesCount: number;
  reservationsCount: number;
  activeCompaniesCount: number;
  createdAt: Date;
  updatedAt: Date;
}

@Component({
  selector: 'app-category-management-list',
    imports: [
        DatePipe
    ],
  templateUrl: './category-management-list.component.html',
  styleUrl: './category-management-list.component.css'
})
export class CategoryManagementListComponent {
  readonly isLoading = signal(true);
  readonly categories = signal<Category[]>([]);

  ngOnInit(): void {
    this.loadCategories();
  }

  private loadCategories(): void {
    this.isLoading.set(true);

    setTimeout(() => {
      let test = [];
      for (let i = 1; i < 22; i++) {
        test.push({
          id: i,
          name: 'Restaurants',
          companiesCount: 42,
          reservationsCount: 1580,
          activeCompaniesCount: 39,
          createdAt: new Date('2025-01-12T09:15:00'),
          updatedAt: new Date('2026-08-06T14:20:00')
        })
      }


      this.categories.set(test);

      this.isLoading.set(false);

    }, 5000);
  }
}
