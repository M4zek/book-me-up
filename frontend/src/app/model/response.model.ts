export interface CategoryResponse{
    id: number,
    name: string,
    numberOfCompanies: number,
}

export interface AddressResponse{
    id: number,
    city: string,
    postalCode: string,
    street: string,
    buildingNumber: string,
}

export interface CompanySummaryResponse{
    id: number,
    name: string,
    address: AddressResponse,
    rating: number,
    numberOfReviews: number,
    logo: string,
}