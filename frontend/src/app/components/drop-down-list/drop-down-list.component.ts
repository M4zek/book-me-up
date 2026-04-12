import {Component, EventEmitter, HostListener, Input, OnChanges, OnDestroy, Output, SimpleChanges} from '@angular/core';
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
export class DropDownListComponent implements OnChanges, OnDestroy {

  @Output() valueChanged = new EventEmitter<DropDownListItem>();
  @Input() placeholder: string = 'Select option'
  @Input() noneValue: boolean = true;
  @Input() disabled: boolean = false;
  @Input() itemsDisabled: boolean = false;
  menuOpen: boolean = false;

  @Input() selectedOption: DropDownListItem = {id: -1, content: ''};
  @Input() options: DropDownListItem[] = []
  @Input() disabledIds: number[] = [];

  ngOnChanges(changes: SimpleChanges): void {
      if (changes['options']) {
          if(this.noneValue) {
              const defaultOptions: DropDownListItem = {content: 'None'};
              this.options = [defaultOptions, ...this.options].map((item: DropDownListItem, index: number) => ({
                  ...item,
                  id: item.id ?? index,
              }));
          } else {
              this.options = this.options.map((item: DropDownListItem, index: number) => ({
                  ...item,
                  id: item.id ?? index,
              }));
          }
      }
      if(changes['disabledIds'] && !changes['disabledIds'].firstChange) {
            if(this.selectedOption.id && !this.disabledIds.includes(this.selectedOption.id)){
                this.selectNone();
            }
      }
  }


  ngOnDestroy() {
      console.log("DESTROY")
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

  selectNone(){
      this.selectedOption = this.options[0];
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

  isDisabled(id: number | undefined) {
      if(id && this.itemsDisabled){
          return !this.disabledIds.includes(id);
      }
      return false;
  }
}
