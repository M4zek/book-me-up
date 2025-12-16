export interface SearchCompanyOptions {
    companyName?: string | null;
    city?: string | null;
    category?: string | null;
}

export interface Pagination{
    totalItems: number,
    itemsPerPage: number,
    currentPage: number,
    itemsPerPageOptions?: number[]
}

export interface Page<T> {
    content: T[],
    page: {
        number: number;
        page: number;
        totalElements: number;
        totalPages: number;
    };
}

export interface UserReservationSearch {
    user_id: number;
    sort: string | undefined;
    status: string | undefined;
    offerName: string | undefined;
}

export interface CompanyOfferSearch {
    company_id: number;
    name?: string;
    sort?: string;
}

export interface CompanyReservationsSearch {
    company_id: number;
    name?: string;
    status?: string;
    sort?: string;
    userId?: number;
}