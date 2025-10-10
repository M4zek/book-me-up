import {Component, EventEmitter, HostListener, Input, Output} from '@angular/core';
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {FormsModule} from "@angular/forms";

export interface DropDownListItem{
  content: string;
  image?: string;
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
export class DropDownListComponent {
  @Output() valueChanged = new EventEmitter<string>();
  @Input() placeholder: string = 'Select option'

  menuOpen: boolean = false;
  @Input() selectedOption: DropDownListItem = {content: ''};

  @Input() options: DropDownListItem[] = [
    {
      content: 'Item 1',
      image: 'images/user_default_avatar.png',
    },
    {
      content: 'Item 2',
    }
  ]

  select(option: DropDownListItem): any {
    if(option.content === 'None') {
      this.selectedOption.content = '';
    } else {
      this.selectedOption = option;
    }
    this.valueChanged.emit(this.selectedOption.content);
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
