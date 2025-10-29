import {Component, OnInit} from '@angular/core';
import {SearchAndSortBarComponent, SortBy} from "../../components/search-bar/search-and-sort-bar.component";
import {PaginatorComponent} from "../../components/paginator/paginator.component";
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {Address} from "../../model/gui/gui.model";
import {DropDownListItem} from "../../components/drop-down-list/drop-down-list.component";
import {AddOpinionModalComponent} from "../../components/modals/add-opinion-modal/add-opinion-modal.component";

// Temporary interface
export interface UserAppointmentItem{
  id: number;
  companyName: string;
  companyLogo: string;
  status: string;
  address: Address;
  date: Date;
  offerName: string;
  price: number;
}

export interface Status{
  status: string;
  image: string;
}

@Component({
  selector: 'app-user-appointments-page',
    imports: [
        SearchAndSortBarComponent,
        PaginatorComponent,
        NgForOf,
        NgClass,
        NgIf,
        AddOpinionModalComponent
    ],
  templateUrl: './user-appointments-page.component.html',
  styleUrl: './user-appointments-page.component.css'
})
export class UserAppointmentsPageComponent implements OnInit {

  ngOnInit(): void {
      this.createList(10)
  }

  tmpList:Status [] = []

  isAddModalOpen = false;

  filterByItems: DropDownListItem[] = [
    { content: 'Pending', image:'icons/pending_icon.svg' },
    { content: 'Realized', image:'icons/realized_icon.svg' },
    { content: 'Rejected', image:'icons/reject_icon.svg' },
    { content: 'Canceled', image:'icons/canceled_icon.svg' },
    { content: 'Accepted', image:'icons/accepted_icon.svg' },
  ]

  sortByItems: DropDownListItem[] = [
    {content: 'Date', image: 'icons/sort_number_asc_icon.svg', option: 'asc'},
    {content: 'Date', image: 'icons/sort_number_desc_icon.svg', option: 'desc'},
    {content: 'Price', image: 'icons/sort_number_asc_icon.svg', option: 'asc'},
    {content: 'Price', image: 'icons/sort_number_desc_icon.svg', option: 'desc'}
  ]

  status: Status[] = [
    { status: 'Pending', image: 'icons/pending_icon.svg'},
    {status: 'Accepted', image: 'icons/accepted_icon.svg'},
    {status: 'Realized', image: 'icons/realized_icon.svg'},
    {status: 'Canceled', image: 'icons/canceled_icon.svg'},
    {status: 'Rejected', image: 'icons/reject_icon.svg'},
  ]

  getRandomStatus(){
    return this.status[Math.floor(Math.random() * this.status.length)];
  }


  createList(max: number){
    for (let i = 0; i < max; i++) {
      this.tmpList.push(this.getRandomStatus());
    }
  }

  cancelReservation() {

  }

  onFilterChange($event: string) {
    console.log($event);
  }

  onSortChange($event: SortBy) {
    console.log($event);
  }

  onSearchChanged($event: string) {
    console.log($event);
  }

  openAddOpinionModal() {
    this.isAddModalOpen = true;
  }

  onAddOpinionModalClose() {
    this.isAddModalOpen = false;
  }
}
