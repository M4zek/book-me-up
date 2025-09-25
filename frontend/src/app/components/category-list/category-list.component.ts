import {Component} from '@angular/core';
import {NgClass} from "@angular/common";

@Component({
  selector: 'app-category-list',
  imports: [
    NgClass
  ],
  templateUrl: './category-list.component.html',
  styleUrl: './category-list.component.css'
})
export class CategoryListComponent {
  drop_down_icon :string = 'icons/drop_down_arrow.png';
  isDropdownOpen = false;

  toggleDropdown() {
    this.isDropdownOpen = !this.isDropdownOpen;
  }
}
