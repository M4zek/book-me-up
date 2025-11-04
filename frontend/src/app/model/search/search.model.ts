export interface SearchCompanyResult {
    name?: string | null;
    place?: string | null;
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