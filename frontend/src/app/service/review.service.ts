import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ReviewOfferRequest, ReviewUserDetailsResponse} from "../model/http/review.model";

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


}
