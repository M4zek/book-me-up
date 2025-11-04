import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Page, Pagination} from "../model/search/search.model";
import {CompanySummaryResponse} from "../model/response.model";

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

}
