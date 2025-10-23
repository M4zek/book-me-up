import {Component} from '@angular/core';
import {SearchAndSortBarComponent} from "../../components/search-bar/search-and-sort-bar.component";
import {PaginatorComponent} from "../../components/paginator/paginator.component";
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {Address} from "../../model/gui/gui.model";

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
    NgIf
  ],
  templateUrl: './user-appointments-page.component.html',
  styleUrl: './user-appointments-page.component.css'
})
export class UserAppointmentsPageComponent {

  status: Status[] = [
    {
      status: 'Pending', image: 'icons/pending_icon.svg',
    },
    {
      status: 'Accepted', image: 'icons/accepted_icon.svg',
    },
    {
      status: 'Realized', image: 'icons/realized_icon.svg',
    },
    {
      status: 'Canceled', image: 'icons/canceled_icon.svg',
    },
    {
      status: 'Rejected', image: 'icons/reject_icon.svg',
    },
  ]

  getRandomStatus(){
    return this.status[Math.floor(Math.random() * this.status.length)];
  }


  createList(max: number){
    let list: Status[] = [];
    for (let i = 0; i < max; i++) {
      list.push(this.getRandomStatus());
    }
    return list;
  }

  addOpinionOffer() {

  }

  cancelReservation() {

  }
}
