import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Page, Pagination, SearchCompanyOptions} from "../model/search/search.model";
import {
    CompanyDetailsResponse,
    CompanyHours,
    CompanyOffersResponse,
    CompanyPortfolioResponse,
    CompanySummaryResponse,
    EmployeeSummaryResponse,
    UserCompanyResponse
} from "../model/http/company.model";

@Injectable({
  providedIn: 'root'
})
export class CompanyService {

  constructor(private http: HttpClient) { }

  getCompanyRecommended(pagination: Pagination, searchValueOptions?: SearchCompanyOptions) {
    let httpParams = new HttpParams()
      .set('page', pagination.currentPage)
      .set('size', pagination.itemsPerPage);

      if (searchValueOptions) {
          Object.entries(searchValueOptions).forEach(([key, value]) => {
              if (value != null && value !== '') {
                  if (key === 'companyName') return;
                  httpParams = httpParams.set(key, value);
              }
          });
      }

      return this.http.get<Page<CompanySummaryResponse>>('/api/public/companies/recommended', {params: httpParams, observe: 'response'});
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

    getCompanyEmployees(companyId: number) {
        const url = `/api/v1/companies/${companyId}/employees`;
        return this.http.get<EmployeeSummaryResponse[]>(url, {observe: 'response'});
    }

    getCompanyBusinessHours(companyId: number) {
      const url = `/api/v1/companies/${companyId}/hours`;
      return this.http.get<CompanyHours[]>(url, {observe: 'response'});
    }

  getUserCompanies(){
      const request_url = `/api/v1/users/me/companies`;
      return this.http.get<UserCompanyResponse[]>(request_url, {observe: 'response'});
  }
}
