import { Component } from '@angular/core';

export interface TicketSummary {
  todayTickets: number;
  openTickets: number;
  solvedTickets: number;
}

export interface TicketReport {
  rank: number;
  user: string;
  type: string;
  date: string;
}

@Component({
  selector: 'app-ticket-summary',
  imports: [],
  templateUrl: './ticket-summary.component.html',
  styleUrl: './ticket-summary.component.css'
})
export class TicketSummaryComponent {
  isLoading = true;

  summary: TicketSummary | null = null;
  tickets: TicketReport[] = [];

  skeletonRows = Array(3).fill(0);

  ngOnInit(): void {
    setTimeout(() => {
      this.summary = {
        todayTickets: 24,
        openTickets: 24,
        solvedTickets: 24
      };

      this.tickets = [
        { rank: 1, user: 'John Doe', type: 'Bug', date: '1 min ago' },
        { rank: 2, user: 'Mark Walkberg', type: 'Report', date: '12 min ago' },
        { rank: 3, user: 'Jane Smith', type: 'Feature', date: '25 min ago' }
      ];

      this.isLoading = false;
    }, 1500);
  }
}
