import {EmployeeSummaryResponse} from "./company.model";

export enum TicketStatus {
    OPEN = 'OPEN',
    IN_PROGRESS = 'IN_PROGRESS',
    RESOLVED = 'RESOLVED',
    CLOSED = 'CLOSED'
}

export enum TicketType {
    COMPANY = 'COMPANY',
    RESERVATION = 'RESERVATION',
    REVIEW = 'REVIEW',
    SERVICE = 'SERVICE',
    EMPLOYEE = 'EMPLOYEE',
    CHAT_MESSAGE = 'CHAT_MESSAGE'
}

// Temporary ticket (Probably will be changed)
export interface Ticket {
    id: number;

    status: TicketStatus;
    statusSubtext: string | null;
    type: TicketType;
    reporter: EmployeeSummaryResponse ;

    admin: EmployeeSummaryResponse | null;

    createdAt: string | Date;
    updatedAt: string | Date;
}
