import {UserResponse} from "./user.model";
import {AddressResponse, CompanyOffersResponse, EmployeeSummaryResponse} from "./company.model";


export interface ReservationRequest{
    company_offer_id: number,
    user_id: number,
    preferred_employee_id: number | undefined,
    reservation_date: Date | String | undefined,
}


export interface ReservationUpdateModel {
    company_id: number,
    reservation_id: number,
    request: ReservationStatusEmployeeUpdateRequest
}

export interface ReservationStatusEmployeeUpdateRequest{
    preferred_employee_id?: number,
    status?: string
}

// RESPONSES

export interface ReservationResponse {
    id: number;
    reservationDate: string;
    reservationNumber: string;
    status: string;
    customer: UserResponse,
    preferredEmployee: EmployeeSummaryResponse,
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


export interface AvailableSlot {
    start: String,
    end: String,
    availableEmployeeIds: number[],
}

export interface DayAvailability{
    date: Date,
    isOpen: boolean,
    slots: AvailableSlot[] | null,
    freeTimePercentage: number | null,
}

export interface AvailableReservationSlots{
    employees: EmployeeSummaryResponse[],
    dayAvailabilities: DayAvailability[],
}