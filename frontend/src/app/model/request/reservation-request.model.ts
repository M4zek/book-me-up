

export interface ReservationRequest{
    company_offer_id: number,
    user_id: number,
    preferred_employee_id: number | undefined,
    reservation_date: Date | undefined,
}

