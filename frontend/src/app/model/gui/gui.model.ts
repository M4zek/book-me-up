import {CompanyHours, CompanyReviewStatistics} from "../http/company.model";

export interface Address{
    city: string;
    street: string;
    buildingNumber: string;
    postalCode: string;
}

export interface CompanyNameAndLogo{
    logo: string;
    logoName: string;
    companyName: string;
}

export interface EmployeeDropDownItem {
    id: number;
    firstName: string;
    lastName: string;
    avatar: string;
}

export interface UserNameAndAvatar {
    id: number;
    name: string;
    avatar: string;
}

export interface HiredEmployeeData {
    id: number;
    name: string;
    role: string;
    email: string;
    phone: string;
    photo: string;
}

export enum HiredEmployeeRole{
    admin = 'Admin',
    owner = 'Owner',
    employee = 'Employee',
}

export interface PortfolioModel {
    id: number;
    name: string;
    photo: string;
}

export interface OfferManagementItem {
    id?: number;
    name: string;
    description: string;
    price: number;
    duration: number;
}

export interface ReservationAvailability {
    companyId: number;
    fromDate: Date;
    toDate: Date;
}


export interface CompanyHomeManagementModel {
    id: number;
    description: string;
    portfolio: PortfolioModel[];
    name_logo: CompanyNameAndLogo;
    address: Address;
    hours: CompanyHours[];
    opinions: CompanyReviewStatistics;
}