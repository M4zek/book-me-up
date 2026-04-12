import {Component, EventEmitter, Input, Output} from '@angular/core';
import {DropDownListComponent, DropDownListItem} from "../drop-down-list/drop-down-list.component";
import {FormsModule} from "@angular/forms";
import {ToastService} from "../../service/toast.service";
import {NgIf} from "@angular/common";

export interface SortBy{
  name: string;
  sorting?: string;
}

@Component({
  selector: 'app-search-and-sort-bar',
  imports: [
    DropDownListComponent,
    FormsModule,
    NgIf
  ],
  templateUrl: './search-and-sort-bar.component.html',
  styleUrl: './search-and-sort-bar.component.css'
})
export class SearchAndSortBarComponent {
  @Output() sortChanged = new EventEmitter<SortBy>();
  @Output() searchChanged = new EventEmitter<string>();
  @Output() filterChanged = new EventEmitter<string>();
  @Output() filterChangeAllItem = new EventEmitter<DropDownListItem>();

  @Input() placeholder: string = 'Search...';
  @Input() showFilter: boolean = false;
  @Input() showSort: boolean = true;

  searchValue: string = '';

  @Input() filterByItems: DropDownListItem[] = []

  @Input() sortByItems: DropDownListItem[] = []

  constructor(private toast: ToastService) { }

  onSortChanged($event: DropDownListItem) {
    if ($event.option) {
      this.sortChanged.emit({
        name: $event.content,
        sorting: $event.option,
      });
    } else {
      this.sortChanged.emit({
        name: $event.content
      });
    }

  }

  clearSearch() {
    this.searchValue = '';
    this.searchChanged.emit(this.searchValue);
  }

  searchConfirm() {
    if(this.searchValue.trim() !== '') {
      this.searchChanged.emit(this.searchValue);
    } else {
      this.toast.show('The search field cannot be empty', 'warning');
    }
  }

  onFilterChanged($event: DropDownListItem) {
    if($event.content) {
      this.filterChanged.emit($event.content);
    }
    if($event) {
        this.filterChangeAllItem.emit($event);
    }
  }
}
