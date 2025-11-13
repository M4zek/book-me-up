import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Page, Pagination, SearchCompanyOptions} from "../model/search/search.model";
import {
    CompanyDetailsResponse,
    CompanyOffersResponse, CompanyPortfolioResponse,
    CompanySummaryResponse
} from "../model/response/company-response.model";

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

  getCompanyOffersByCompanyId(company_id: number, pagination: Pagination){
      let httpParams = new HttpParams()
          .set('page', pagination.currentPage)
          .set('size', pagination.itemsPerPage);

      return this.http.get<Page<CompanyOffersResponse>>(`/api/public/company/${company_id}/offers`, {params: httpParams, observe: 'response'});
  }

  getCompanyPortfolioByCompanyId(company_id: number, pagination?: Pagination){
      let httpParams = new HttpParams()
        .set('page', pagination ? pagination.currentPage : 0)
        .set('size', pagination ? pagination.itemsPerPage : 10);

      return this.http.get<Page<CompanyPortfolioResponse>>(`/api/public/companies/${company_id}/portfolio-images`, {params: httpParams, observe: 'response'});

  }

  searchCompanyByCompanyNameOrCityOrCategory(pagination: Pagination, searchOptions: SearchCompanyOptions){
      const url = '/api/public/companies/search';

      let httpParams = new HttpParams()
          .set('page', pagination.currentPage)
          .set('size', pagination.itemsPerPage)

      if (searchOptions) {
          Object.entries(searchOptions).forEach(([key, value]) => {
              if (value != null && value !== '') {
                  httpParams = httpParams.set(key, value);
              }
          });
      }

      return this.http.get<Page<CompanySummaryResponse>>(url, {params: httpParams, observe: 'response'});
  }

}
