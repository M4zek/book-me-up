import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Page, Pagination} from "../model/search/search.model";
import {CompanyDetailsResponse, CompanySummaryResponse} from "../model/response.model";

@Injectable({
  providedIn: 'root'
})
export class CompanyService {

  constructor(private http: HttpClient) { }

  getCompanySummary(pagination: Pagination){
    let httpParams = new HttpParams()
      .set('page', pagination.currentPage)
      .set('size', pagination.itemsPerPage);

      return this.http.get<Page<CompanySummaryResponse>>('/api/public/companies/recommended', {params: httpParams});
  }


  getCompanyDetailById(company_id: number){
      return this.http.get<CompanyDetailsResponse>(`/api/public/companies/${company_id}`, {observe: 'response'});
  }

}
