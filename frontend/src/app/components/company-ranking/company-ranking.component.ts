import {Component, EventEmitter, input, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {DecimalPipe} from "@angular/common";
import {CompanyRankingItem} from "../../model/gui/admin.gui.model";
import {Page, Pagination} from "../../model/search/search.model";
import {PaginatorComponent} from "../paginator/paginator.component";


@Component({
  selector: 'app-company-ranking',
  imports: [
    DecimalPipe,
    PaginatorComponent
  ],
  templateUrl: './company-ranking.component.html',
  styleUrl: './company-ranking.component.css'
})
export class CompanyRankingComponent implements OnChanges {
  isLoading = input(true);

  @Input() companies: Page<CompanyRankingItem> | null = null;

  @Output() paginatorChanged = new EventEmitter<Pagination>();

  paginator: Pagination = {
    totalItems: this.companies?.page.totalElements ?? 0,
    itemsPerPage: 5,
    currentPage: this.companies?.page?.number ?? 1,
    itemsPerPageOptions: [5,10,15,20]
  }

  skeletonItems = Array(5).fill(0)

  ngOnChanges(changes: SimpleChanges): void {
    if(changes['companies']){
      this.paginator.totalItems = this.companies?.page.totalElements ?? 0;
      this.paginator.currentPage = this.companies?.page?.number ?? 1;
    }
  }

  protected onPaginatorChanged($event: void) {
    this.paginatorChanged.emit(this.paginator);
  }
}
