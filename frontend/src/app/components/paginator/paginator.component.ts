import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Pagination} from "../../model/search/search.model";
import {NgForOf} from "@angular/common";

@Component({
  selector: 'app-paginator',
  imports: [
    NgForOf
  ],
  templateUrl: './paginator.component.html',
  styleUrl: './paginator.component.css'
})
export class PaginatorComponent {

  @Input() pagination: Pagination = {
    totalItems: 0,
    itemsPerPage: 10,
    currentPage: 1,
    itemsPerPageOptions: [5, 10, 20, 30, 40]
  }

  @Output() pageChange = new EventEmitter<number>();
  @Output() itemsPerPageChange = new EventEmitter<number>();

  get totalPages(): number {
    return Math.ceil(this.pagination.totalItems / this.pagination.itemsPerPage);
  }

  get visiblePages(): number[] {
    const pages: number[] = [];
    if (this.totalPages <= 1) return pages;

    const start = Math.max(1, this.pagination.currentPage - 1);
    const end = Math.min(this.totalPages, this.pagination.currentPage + 1);

    for (let i = start; i <= end; i++) {
      pages.push(i);
    }

    return pages;
  }

  changePage(page: number) {
    if (page < 1 || page > this.totalPages) return;
    this.pagination.currentPage = page;
    this.pageChange.emit(this.pagination.currentPage);
  }

  firstPage() {
    this.changePage(1);
  }

  lastPage() {
    this.changePage(this.totalPages);
  }

  previousPage() {
    this.changePage(this.pagination.currentPage - 1);
  }

  nextPage() {
    this.changePage(this.pagination.currentPage + 1);
  }

  changeItemsPerPage(event: Event) {
    const newValue = Number((event.target as HTMLSelectElement).value);
    this.pagination.itemsPerPage = newValue;
    this.itemsPerPageChange.emit(newValue);
    this.changePage(1);
  }
}
