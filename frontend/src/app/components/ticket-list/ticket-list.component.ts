import {Component, input, model} from '@angular/core';
import {Ticket} from "../../model/http/ticket.model";
import {DatePipe} from "@angular/common";


@Component({
  selector: 'app-ticket-list',
  imports: [
    DatePipe
  ],
  templateUrl: './ticket-list.component.html',
  styleUrl: './ticket-list.component.css'
})
export class TicketListComponent {
  protected readonly Date = Date;

  isLoading = input<boolean>(false);
  tickets = model<Ticket[]>([]);

  skeletonRows = Array(5).fill(0);

  getStatusLabel(status: string): string {
    switch (status.toLowerCase()) {
      case 'open': return '🔵 Open';
      case 'in_progress': return '🟠 In Progress';
      case 'canceled': return '🔴 Canceled';
      case 'Resolved': return '🟢 Resolved';
      default: return status;
    }
  }

  getTypeLabel(type: string): string {
    return `${type.substring(0, 1)}${type.substring(1).toLowerCase()}`;
  }

  getInitials(name: string): string {
    if (!name) return 'U';
    const parts = name.trim().split(' ');
    return parts.length >= 2
        ? `${parts[0][0]}${parts[1][0]}`.toUpperCase()
        : parts[0].substring(0, 2).toUpperCase();
  }


}
