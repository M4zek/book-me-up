import {Injectable} from '@angular/core';
import {HttpClient, HttpParams, HttpResponse} from "@angular/common/http";
import {ReservationAvailabilityResponse, ReservationResponse} from "../model/response/reservation-response.model";
import {Observable} from "rxjs";
import {ReservationAvailability} from "../model/gui/gui.model";
import {ReservationRequest} from "../model/request/reservation-request.model";

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
}
