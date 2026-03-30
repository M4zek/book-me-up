import {Injectable} from '@angular/core';
import {HttpClient, HttpParams, HttpResponse} from "@angular/common/http";

import {Observable} from "rxjs";
import {ReservationAvailability} from "../model/gui/gui.model";
import {
    AvailableReservationSlots,
    ReservationRequest,
    ReservationResponse,
    ReservationUpdateModel,
    UserReservationResponse
} from "../model/http/reservation.model";
import {CompanyReservationsSearch, Page, Pagination, UserReservationSearch} from "../model/search/search.model";

@Injectable({
  providedIn: 'root'
})
export class ReservationService {

  constructor(private http: HttpClient) { }

  getReservationSlots(availability: ReservationAvailability): Observable<HttpResponse<AvailableReservationSlots>> {
      let url = `/api/v1/companies/${availability.companyId}/reservations/free-slots`;

      let httpParams = new HttpParams()
          .set('fromDate', availability.fromDate.toLocaleDateString('en-US', {year: 'numeric', month: '2-digit', day: '2-digit'}).replace(/\//g, '-'))
          .set('toDate', availability.toDate.toLocaleDateString('en-US', {year: 'numeric', month: '2-digit', day: '2-digit'}).replace(/\//g, '-'))
          .set("duration", availability.duration);
      return this.http.get<AvailableReservationSlots>(url, {params: httpParams, observe: 'response'});
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


  // COMPANY MANAGEMENT METHODS

    // Read company reservations from backend to show it on company appointments view
    getCompanyReservations(search: CompanyReservationsSearch, pagination: Pagination){
        let url_request = `/api/v1/companies/${search.company_id}/reservations`;

        let httpParams = new HttpParams()
            .set('page', pagination.currentPage)
            .set('size', pagination.itemsPerPage);

        if(search.status && search.status != 'None'){
            httpParams = httpParams.set("status", search.status.toUpperCase());
        }

        if(search.name && search.name != ''){
            httpParams = httpParams.set("name", search.name);
        }

        if(search.userId){
            httpParams = httpParams.set("userId", search.userId);
        }

        if(search.sort && search.sort != 'None'){
            httpParams = httpParams.set("sort", search.sort);
        }

        if(search.fromDate && search.toDate){
            httpParams = httpParams.set("fromDate", search.fromDate.toLocaleDateString('en-US', {year: 'numeric', month: '2-digit', day: '2-digit'}).replace(/\//g, '-'))
            httpParams = httpParams.set("toDate", search.toDate.toLocaleDateString('en-US', {year: 'numeric', month: '2-digit', day: '2-digit'}).replace(/\//g, '-'))
        }

        return this.http.get<Page<ReservationResponse>>(url_request, {params: httpParams, observe: 'response'});
    }


    // Update reservation by status or preferred employee
    patchReservation(reservationUpdate: ReservationUpdateModel){
      let url_request = `/api/v1/companies/${reservationUpdate.company_id}/reservations/${reservationUpdate.reservation_id}`;
      let requestBody = reservationUpdate.request;

      return this.http.patch<ReservationResponse>(url_request, requestBody, {observe: 'response'});
    }

  // **************************

}
