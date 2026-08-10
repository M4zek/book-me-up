import {Component} from '@angular/core';
import {StatCardComponent} from "../../../components/stat-card/stat-card.component";
import {
  AdminTicketToolbarComponent,
  TicketFilterState
} from "../../../components/admin-ticket-toolbar/admin-ticket-toolbar.component";
import {TicketListComponent} from "../../../components/ticket-list/ticket-list.component";
import {Ticket, TicketStatus, TicketType} from "../../../model/http/ticket.model";
import {PaginatorComponent} from "../../../components/paginator/paginator.component";
import {Pagination} from "../../../model/search/search.model";

@Component({
  selector: 'app-admin-tickets',
  imports: [
    StatCardComponent,
    AdminTicketToolbarComponent,
    TicketListComponent,
    PaginatorComponent
  ],
  templateUrl: './admin-tickets.component.html',
  styleUrl: './admin-tickets.component.css'
})
export class AdminTicketsComponent {

  isMyTicketLoading = false;
  myTickets: Ticket[] = [];
  myPaginator: Pagination = {
    totalItems: 10,
    itemsPerPage: 5,
    currentPage: 0,
    itemsPerPageOptions: [5, 10, 20, 30, 50]
  }

  isTicketToAssignLoading = false;
  ticketToAssign: Ticket[] = [];
  assignPaginator: Pagination = {
    totalItems: 10,
    itemsPerPage: 5,
    currentPage: 0,
    itemsPerPageOptions: [5, 10, 20, 30, 50]
  }


  constructor() {
    this.isMyTicketLoading = true;

    setTimeout(() => {
      for (let i = 0; i < 25; i++) {

        let test: Ticket = {
          id: i,
          status: TicketStatus.OPEN,
          statusSubtext: null,
          admin: null,
          reporter: {
            id: i, lastName: 'Doe', firstName: 'John', avatar: ''
          },
          type: TicketType.RESERVATION,
          createdAt: new Date("2026-10-20T13:20"),
          updatedAt: new Date("2026-10-20T16:20")
        }
        this.myTickets.push(test)
      }
      this.isMyTicketLoading = false;

    }, 5000);


  }

  protected onAssignedFilterChanged($event: TicketFilterState) {
    console.log(`Assigned filter changed`);
    console.log(`Types: ${$event.types}`);
    console.log(`Statuses: ${$event.statuses}`);
  }

  protected onTicketFilterChanged($event: TicketFilterState) {
    console.log(`Ticket filter changed`);
    console.log(`Types: ${$event.types}`);
    console.log(`Statuses: ${$event.statuses}`);
  }
}
