import {UserResponse} from "./user-response.model";
import {CompanyOffersResponse} from "./company-response.model";

export interface ReservationAvailabilityResponse {
    dateOfBooked: Date;
    bookedCompanyHours: BookedCompanyHoursResponse[];
    freeTimePercentage: number;
}

export interface BookedCompanyHoursResponse  {
    startTimeBooked: string;
    endTimeBooked: string;
}


export interface ReservationResponse {
    id: number;
    reservationDate: string;
    reservationNumber: string;
    status: string;
    customer: UserResponse,
    preferredEmployee: UserResponse | null,
    companyOffer: CompanyOffersResponse
}