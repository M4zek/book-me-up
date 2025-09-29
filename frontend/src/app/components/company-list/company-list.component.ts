import {Component, ElementRef, HostListener, ViewChild} from '@angular/core';
import {CompanyListItemComponent} from "../company-list-item/company-list-item.component";
import {NgForOf} from "@angular/common";

@Component({
  selector: 'app-company-list',
  imports: [
    NgForOf,
    CompanyListItemComponent
  ],
  templateUrl: './company-list.component.html',
  styleUrl: './company-list.component.css'
})
export class CompanyListComponent {
  @ViewChild('container') container!: ElementRef<HTMLDivElement>;


  left_arrow_icon_path: string = '/icons/left_arrow_icon.svg';
  right_arrow_icon_path: string = '/icons/right_arrow_icon.svg';

  company_list_size = 10;
  currentIndex = 0;
  itemWidth = 345;
  visibleItems = 0;

  ngAfterViewInit() {
    this.calculateElementsInContainer();
  }

  rows(n: number): number[] {
    return Array(n).fill(0).map((_, i) => i);
  }

  next() {
    if (this.currentIndex < this.company_list_size - this.visibleItems &&
        this.currentIndex < this.company_list_size - 1)
    {
        this.currentIndex++;

    }
  }

  prev() {
    if (this.currentIndex > 0) {
      this.currentIndex--;
    }
  }


  @HostListener('window:resize')
  onResize() {
    this.calculateElementsInContainer();
    this.next();
    this.prev();
  }

  calculateElementsInContainer(){
    const containerWidth = this.container.nativeElement.offsetWidth;
    this.visibleItems = Math.floor(containerWidth / this.itemWidth);
  }
}
