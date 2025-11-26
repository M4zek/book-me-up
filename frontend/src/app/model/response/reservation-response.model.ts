import {UserResponse} from "./user-response.model";
import {AddressResponse, CompanyOffersResponse} from "./company-response.model";

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

export interface UserReservationResponse {
    id: number;
    companyName: string;
    reservationDate: string;
    reservationNumber: string;
    status: string;
    address: AddressResponse;
    offerName: string;
    offerPrice: number;
    companyLogo: string;
}