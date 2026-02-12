import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {CompanyOfferSearch, Page, Pagination, SearchCompanyOptions} from "../model/search/search.model";
import {
    CompanyDetailsResponse,
    CompanyEmployeeDetailsResponse,
    CompanyHours,
    CompanyOfferRequest,
    CompanyOffersResponse,
    CompanyPortfolioResponse,
    CompanySummaryResponse,
    EmployeeDetailsResponse,
    EmployeeSummaryResponse,
    UserCompanyResponse
} from "../model/http/company.model";
import {LogoName} from "../components/modals/company-name-logo-edit-modal/company-name-logo-edit-modal.component";

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

    // Method to send request to backend for change employee role in company based on employe id and company id.
    updateEmployeeRole(companyId: number, employeeId: number, role: string) {
      const url = `/api/v1/companies/${companyId}/employees/${employeeId}/role`;
      const body = {role: role};
      return this.http.patch<CompanyEmployeeDetailsResponse>(url, body, {observe: 'response'});
    }

    fireEmployee(company_id: number, employee_id: number) {
      const url = `/api/v1/companies/${company_id}/employee/${employee_id}/dismiss`;
      return this.http.delete(url, {observe: 'response'});
    }

    getCompanyBusinessHours(companyId: number) {
      const url = `/api/v1/companies/${companyId}/hours`;
      return this.http.get<CompanyHours[]>(url, {observe: 'response'});
    }

  getUserCompanies(){
      const request_url = `/api/v1/users/me/companies`;
      return this.http.get<UserCompanyResponse[]>(request_url, {observe: 'response'});
  }

    getCompanyEmployeesDetails(companyId: number, pagination: Pagination) {
        const request_url = `/api/v1/companies/${companyId}/employees/details`;

        let httpParams = new HttpParams()
            .set('page', pagination.currentPage)
            .set('size', pagination.itemsPerPage)

        return this.http.get<Page<CompanyEmployeeDetailsResponse>>(request_url, {params: httpParams, observe: 'response'});
    }



  // Company Offer Methods
  searchCompanyOffers(search: CompanyOfferSearch, pagination: Pagination) {
      let url = `/api/v1/company/${search.company_id}/offers`;

      let httpParams = new HttpParams()
          .set('page', pagination.currentPage)
          .set('size', pagination.itemsPerPage)

      Object.entries(search).forEach(([key, value]) => {
          if (key === 'company_id') return;

          if (value != null && value !== '') {
              httpParams = httpParams.set(key, value);
          }
      });

      return this.http.get<Page<CompanyOffersResponse>>(url, {params: httpParams, observe: 'response'});
  }

  createNewCompanyOffer(body: CompanyOfferRequest, company_id: number){
      let req_url = `/api/v1/company/${company_id}/offers`;
      return this.http.post<CompanyOffersResponse>(req_url, body, {observe: 'response'});
  }

  updateCompanyOffer(body: CompanyOfferRequest, company_id: number, offer_id: number){
      let req_url = `/api/v1/company/${company_id}/offers/${offer_id}`;

      return this.http.patch<CompanyOffersResponse>(req_url, body, {observe: 'response'});
  }


  // Method to send request to the backend to hire employee in the company base od user ids and company id.
  hireEmployeesToCompany(company_id: number, employeeIds: number[]){
      let url = `/api/v1/companies/${company_id}/employees/hire`
      let body = {employeeIds: employeeIds};
      return this.http.post<EmployeeDetailsResponse[]>(url, body, {observe: 'response'});
  }

  // Method to update logo or company name
  updateLogoOrNameInCompany(company_id: number, data: LogoName) {
      let  url: string = `/api/v1/companies/${company_id}/profile`;

      // Convert name data to json
      let json_data = JSON.stringify({name: data.name});
      const formData = new FormData();

      // Assigned data to request (name or logo or both)
      if(data.file) formData.append("logo", data.file)
      if(data.name) formData.append("data", new Blob([json_data], {type: 'application/json'}));

      return this.http.patch<CompanyDetailsResponse>(url, formData, {observe: 'response'});
  }


  // Method to update company description
  updateCompanyDescription(company_id: number, desc: string){
    let  url = `/api/v1/companies/${company_id}/desc`;
    let body_json_data = {description: desc};

    return this.http.patch<CompanyDetailsResponse>(url, body_json_data, {observe: 'response'});
  }
}
