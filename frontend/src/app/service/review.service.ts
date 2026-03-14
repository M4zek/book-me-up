import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {ReviewOfferRequest, ReviewUserDetailsResponse} from "../model/http/review.model";
import {Page, Pagination} from "../model/search/search.model";

@Injectable({
  providedIn: 'root'
})
export class ReviewService {

    constructor(private http: HttpClient) {}


    // Create new review to completed user reservation
    createReview(body: ReviewOfferRequest){
        const apiURL = "/api/v1/companies/reviews";
        return this.http.post<ReviewUserDetailsResponse>(apiURL, body, {observe: 'response'});
    }

    // Read company reviews details
    readCompanyReviews(companyId: number, rating: number | null, pagination: Pagination){
        const apiURL = `/api/public/companies/${companyId}/reviews`;

        let httpParams = new HttpParams()
            .set('page', pagination.currentPage)
            .set('size', pagination.itemsPerPage);

        if(rating != null){
            httpParams = httpParams.set('rating', rating);
        }

        return this.http.get<Page<ReviewUserDetailsResponse>>(apiURL, {params: httpParams, observe: 'response'});
    }

}
