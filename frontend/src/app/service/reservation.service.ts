import {Injectable} from '@angular/core';
import {HttpClient, HttpParams, HttpResponse} from "@angular/common/http";
import {
    ReservationAvailabilityResponse,
    ReservationResponse,
    UserReservationResponse
} from "../model/response/reservation-response.model";
import {Observable} from "rxjs";
import {ReservationAvailability} from "../model/gui/gui.model";
import {ReservationRequest} from "../model/request/reservation-request.model";
import {Page, Pagination, UserReservationSearch} from "../model/search/search.model";

@Injectable({
  providedIn: 'root'
})
export class ReservationService {

  constructor(private http: HttpClient) { }


  getAvailabilityCalendar(availability: ReservationAvailability): Observable<HttpResponse<ReservationAvailabilityResponse[]>> {
      let url = `/api/v1/companies/${availability.companyId}/reservations`;

      let httpParams = new HttpParams()
          .set('fromDate', availability.fromDate.toLocaleDateString('en-US', {year: 'numeric', month: '2-digit', day: '2-digit'}).replace(/\//g, '-'))
          .set('toDate', availability.toDate.toLocaleDateString('en-US', {year: 'numeric', month: '2-digit', day: '2-digit'}).replace(/\//g, '-'))

      return this.http.get<ReservationAvailabilityResponse[]>(url, {params: httpParams, observe: 'response'});
  }


  makeAnReservation(reservationRequest: ReservationRequest): Observable<HttpResponse<ReservationResponse>> {
      const url = '/api/v1/companies/reservations'
      return this.http.post<ReservationResponse>(url, reservationRequest, {observe: 'response'});
  }

  readUserReservations(pagination: Pagination, search: UserReservationSearch){
      let url = `/api/v1/reservations/${search.user_id}`;

      let httpParams = new HttpParams()
          .set('page', pagination.currentPage)
          .set('size', pagination.itemsPerPage);

      if(search.status && search.status != 'None'){
          httpParams = httpParams.set("status", search.status.toUpperCase());
      }

      if(search.sort && search.sort != 'None'){
          httpParams = httpParams.set("sort", search.sort);
      }

      if(search.offerName && search.offerName != ''){
          httpParams = httpParams.set("name", search.offerName);
      }

      return this.http.get<Page<UserReservationResponse>>(url, {params: httpParams, observe: 'response'});
  }

  cancelUserReservation(reservation_id: number){
      let url = `/api/v1/reservations/${reservation_id}/cancel`;
      return this.http.patch<UserReservationResponse>(url,{}, {observe: 'response'});
  }

}
