import {Component, EventEmitter, HostListener, Input, Output} from '@angular/core';
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {FormsModule} from "@angular/forms";

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
  selectedOption: string = ''

  @Input() options: string[] = [
      'None',
      'Test 1',
      'Test 2',
      'Test 3',
      'Test 4',
      'Test 5',
      'Test 6'
  ]

  select(option: string): any {
    if(option === 'None') {
      this.selectedOption = '';
    } else {
      this.selectedOption = option;
    }
    this.valueChanged.emit(this.selectedOption);
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
