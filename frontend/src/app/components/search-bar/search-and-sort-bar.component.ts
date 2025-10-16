import {Component, EventEmitter, Input, Output} from '@angular/core';
import {DropDownListComponent, DropDownListItem} from "../drop-down-list/drop-down-list.component";
import {FormsModule} from "@angular/forms";
import {ToastService} from "../../service/toast.service";

export interface SortBy{
  name: string;
  sorting?: string;
}

@Component({
  selector: 'app-search-bar',
    imports: [
        DropDownListComponent,
        FormsModule
    ],
  templateUrl: './search-and-sort-bar.component.html',
  styleUrl: './search-and-sort-bar.component.css'
})
export class SearchAndSortBarComponent {
  @Output() sortChanged = new EventEmitter<SortBy>();
  @Output() searchChanged = new EventEmitter<string>();

  @Input() placeholder: string = 'Search...';

  searchValue: string = '';

  sortByItems: DropDownListItem[] = [
    {
      content: 'Name', image: 'icons/sort_string_asc_icon.svg', option: 'asc',
    }, {
      content: 'Name', image: 'icons/sort_string_desc_icon.svg', option: 'desc',
    }, {
      content: 'Price', image: 'icons/sort_number_asc_icon.svg', option: 'asc',
    }, {
      content: 'Price', image: 'icons/sort_number_desc_icon.svg', option: 'desc',
    }
  ]

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
}
