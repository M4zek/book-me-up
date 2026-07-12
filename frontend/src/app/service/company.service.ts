import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {CompanyOfferSearch, Page, Pagination, SearchCompanyOptions} from "../model/search/search.model";
import {
    AddressResponse,
    CompanyDetailsResponse,
    CompanyEmployeeDetailsResponse,
    CompanyHours,
    CompanyOfferRequest,
    CompanyOffersResponse,
    CompanyPortfolioResponse, CompanyRequest,
    CompanySummaryResponse,
    EmployeeDetailsResponse,
    EmployeeSummaryResponse,
    UserCompanyResponse
} from "../model/http/company.model";
import {LogoName} from "../components/modals/company-name-logo-edit-modal/company-name-logo-edit-modal.component";
import {Address} from "../model/gui/gui.model";
import {
    EmployeeSearchModel
} from "../pages/company-management-page/company-management-employee/company-management-employee.component";

@Injectable({
  providedIn: 'root'
})
export class CompanyService {

  constructor(private http: HttpClient) { }


    createCompany(companyRequest: CompanyRequest, logo: File | null) {
        const url = '/api/v1/companies';

        let formDate = new FormData();

        formDate.append('companyRequest',
            new Blob([JSON.stringify(companyRequest)], {
                type: 'application/json'
            })
        );

        if(logo)
            formDate.append('file', logo);

        return this.http.post<CompanyDetailsResponse>(url, formDate, {observe: 'response'});
    }

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

    getCompanyEmployeesDetails(companyId: number, pagination: Pagination, search: EmployeeSearchModel | null = null) {
        const request_url = `/api/v1/companies/${companyId}/employees/details`;

        let httpParams = new HttpParams()
            .set('page', pagination.currentPage)
            .set('size', pagination.itemsPerPage)

        if(search?.firstName && search.firstName !== '') {
            httpParams = httpParams.set('firstName', search.firstName);
            if(search?.lastName && search.lastName !== '') {
                httpParams = httpParams.set('lastName', search.lastName);
            }
        }

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

      const formData = new FormData();

      console.log(data.name)

      // Assigned data to request (name or logo or both)
      if(data.file) formData.append("logo", data.file)

      // Convert name data to json
      let json_data = JSON.stringify({name: data.name});

      formData.append("data", new Blob([json_data], {type: 'application/json'}));

      return this.http.patch<CompanyDetailsResponse>(url, formData, {observe: 'response'});
  }


  // Method to update company description
  updateCompanyDescription(company_id: number, desc: string){
    let  url = `/api/v1/companies/${company_id}/desc`;
    let body_json_data = {description: desc};

    return this.http.patch<CompanyDetailsResponse>(url, body_json_data, {observe: 'response'});
  }


  // Method to update company opening hours
  updateCompanyOpeningHours(company_id: number, days: CompanyHours[]){
      const url = `/api/v1/companies/${company_id}/opening-hours`;

      const body = days.map(day => ({
          dayOfWeek: day.dayOfWeek,
          openTime: day.openTime,
          closeTime: day.closeTime,
          open: day.open
      }));

      return this.http.patch<CompanyHours[]>(url, body, {observe: 'response'});
  }

  // Method to update company address
  updateCompanyAddress(company_id: number, address: Address){
    let url = `/api/v1/companies/${company_id}/address`;
    return this.http.patch<AddressResponse>(url, address, {observe: 'response'});
  }

  // Method to delete photo from portfolio
  deleteImageFromCompanyPortfolio(company_id: number, photo_id: number){
      const url = `/api/v1/companies/${company_id}/portfolio-images/${photo_id}`;
      return this.http.delete(url, {observe: 'response'});
  }

  // Method to add new images to company portfolio
  uploadNewImagesToCompanyPortfolio(company_id: number, files: File[]){
      let url = `/api/v1/companies/${company_id}/portfolio-images`;

      const formData = new FormData();
      files.forEach((file) => {
          formData.append("images", file);
      });

      return this.http.post<CompanyPortfolioResponse[]>(url, formData, {observe: 'response'});
  }
}
