import {Component, EventEmitter, HostListener, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {FormsModule} from "@angular/forms";

export interface DropDownListItem {
  id?: number,
  content: string;
  image?: string;
  option?: string;
}

@Component({
  selector: 'app-drop-down-list',
  imports: [
    NgClass,
    NgIf,
    NgForOf,
    FormsModule
  ],
  templateUrl: './drop-down-list.component.html',
  styleUrl: './drop-down-list.component.css'
})
export class DropDownListComponent implements OnChanges {

  @Output() valueChanged = new EventEmitter<DropDownListItem>();
  @Input() placeholder: string = 'Select option'

  menuOpen: boolean = false;

  @Input() selectedOption: DropDownListItem = {id: -1, content: ''};
  @Input() options: DropDownListItem[] = []

  ngOnChanges(changes: SimpleChanges): void {
      if (changes['options']) {
          const defaultOptions: DropDownListItem = {content: 'None'};
          this.options = [defaultOptions, ...this.options].map((item: DropDownListItem, index: number) => ({
              ...item,
              id: item.id ?? index,
          }));
      }
  }

  select(option: DropDownListItem): any {
    if(option.content === 'None') {
      this.selectedOption = this.options[0];
    } else {
      this.selectedOption = option;
    }
    this.valueChanged.emit(this.selectedOption);
    this.toggleMenu();
  }

  toggleMenu(): void {
    this.menuOpen = !this.menuOpen;
  }

  @HostListener('document:click', ['$event'])
  clickOutside(event: Event): void {
    const target = event.target as HTMLElement;
    if (!target.closest('.drop-down-icon')) {
      this.menuOpen = false;
    }
  }
}
