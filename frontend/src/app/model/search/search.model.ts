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