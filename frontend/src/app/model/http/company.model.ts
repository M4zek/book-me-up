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

export interface CompanyHours{
    dayOfWeek: string,
    openTime: string,
    closeTime: string,
    open: boolean,
}

export interface CompanyReviewStatistics{
    rating: number,
    totalReviews: number,
    ratingCounts: {[key: number]: number},
}

export interface EmployeeSummaryResponse{
    id: number,
    firstName: string,
    lastName: string,
    avatar: string
}

export interface EmployeeDetailsResponse{
    id: number,
    firstName: string,
    lastName: string,
    phone: string,
    email: string,
    role_in_company: string,
    avatar: string | null,
}

export interface CompanyDetailsResponse {
    id: number,
    name: string,
    description: string,
    address: AddressResponse,
    reviewStatistics: CompanyReviewStatistics,
    companyHours: CompanyHours[],
    owner: EmployeeSummaryResponse,
    employees: EmployeeSummaryResponse[],
    logo: string,
}


export interface CompanyOffersResponse{
    id: number,
    name: string,
    description: string,
    price: number,
    duration: number
}

export interface CompanyOfferRequest{
    name?: string,
    description?: string,
    price?: number,
    duration?: number
}

export interface CompanyPortfolioResponse{
    id: number,
    filename: string,
    downloadUrl: string,
    image: string,
}

export interface UserCompanyResponse{
    id: number,
    name: string,
    logo: string,
    role: string[],
}

export interface CompanyEmployeeDetailsResponse{
    id: number,
    firstName: string,
    lastName: string,
    email: string,
    phone: string,
    role_in_company: string,
    avatar: string,
}