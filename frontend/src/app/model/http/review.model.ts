import {UserResponse} from "./user.model";
import {CompanyOffersResponse} from "./company.model";


export interface ReviewOfferRequest{
    author_id: number,
    company_offer_id: number,
    rating: number,
    comment: string,
}




// Responses
export interface ReviewUserDetailsResponse {
    id: number,
    comment: string,
    rating: number,
    created_at: Date,
    updated_at: Date,
    author: UserResponse,
    offer: CompanyOffersResponse
}