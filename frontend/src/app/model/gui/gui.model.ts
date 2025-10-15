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
    id: number;
    name: string;
    description: string;
    price: number;
    duration: number;
}