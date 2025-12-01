import {UserResponse} from "./user.model";
import {AddressResponse, CompanyOffersResponse} from "./company.model";


export interface ReservationRequest{
    company_offer_id: number,
    user_id: number,
    preferred_employee_id: number | undefined,
    reservation_date: Date | undefined,
}



// RESPONSES

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
    offer: CompanyOffersResponse;
    hasUserRatedOffer: boolean;
    companyLogo: string;
}