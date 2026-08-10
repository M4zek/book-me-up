import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {UserHireDetails, UserResponse} from "../model/http/user.model";
import {Page, Pagination} from "../model/search/search.model";
import {Member} from "../model/http/chat.model";
import {AccountListItem, MonthCountData, UserActivityItem} from "../model/gui/admin.gui.model";
import {UserFilterState} from "../components/admin-user-toolbar/admin-user-toolbar.component";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http: HttpClient) { }

  readLoggedInUserData(){
    const url = "/api/v1/users/me";
    return this.http.get<UserResponse>(url, {observe: 'response'});
  }


  searchUsersByNameAndSurname(body: {firstName: string, lastName: string}, pagination: Pagination ) {
      let firstName = body.firstName;
      let lastName = body.lastName;

      let httpParams = new HttpParams()
          .set('page', pagination.currentPage)
          .set('size', pagination.itemsPerPage);

      if(firstName){
          httpParams = httpParams.set("firstName", firstName);
      }

      if(lastName){
          httpParams = httpParams.set("lastName", lastName);
      }

      let url = "/api/v1/users/hire/search";

      return this.http.get<Page<UserHireDetails>>(url, {params: httpParams, observe: 'response'});
  }


  searchUsersByFirstNameAndLastNameToChat(body: {firstName: string, lastName: string}, pagination: Pagination ) {
      let firstName = body.firstName;
      let lastName = body.lastName;

      let httpParams = new HttpParams()
          .set('page', pagination.currentPage)
          .set('size', pagination.itemsPerPage);

      if(firstName){
          httpParams = httpParams.set("firstName", firstName);
      }

      if(lastName){
          httpParams = httpParams.set("lastName", lastName);
      }

      let url = "/api/v1/users/search";

      return this.http.get<Page<Member>>(url, {params: httpParams, observe: 'response'});
  }



  getUsersGrowthTrend(){
      const users_url = '/api/v1/admin/users/growth';
      return this.http.get<MonthCountData[]>(users_url, {observe: 'response'});
  }

  getUsersLoginTrend(){
      const users_url = '/api/v1/admin/users/login-trend';
      return this.http.get<MonthCountData[]>(users_url, {observe: 'response'});
  }


  getNewAccountHistory(page: Pagination | null = null){
      const url = "/api/v1/admin/users/new-accounts";

      let httpParams = new HttpParams();
      httpParams = this.setPaginationToParams(httpParams, page);

      return this.http.get<Page<UserActivityItem>>(url, {params: httpParams, observe: 'response'});
  }

    getLoggedInHistory(page: Pagination | null = null){
        const url = "/api/v1/admin/users/last-loggedin";

        let httpParams = new HttpParams();
        httpParams = this.setPaginationToParams(httpParams, page);

        return this.http.get<Page<UserActivityItem>>(url, {params: httpParams, observe: 'response'});
    }

    getAdminActivityHistory(page: Pagination | null = null){
        const url = "/api/v1/admin/users/last-admin-loggedin";

        let httpParams = new HttpParams();
        httpParams = this.setPaginationToParams(httpParams, page);

        return this.http.get<Page<UserActivityItem>>(url, {params: httpParams, observe: 'response'});
    }

    searchUsersBasedOnFilters(page: Pagination | null = null, filters: UserFilterState | null = null){
      const url = "/api/v1/admin/users/search";

        let httpParams = new HttpParams();
        httpParams = this.setPaginationToParams(httpParams, page);
        httpParams = this.buildUserFilterParams(httpParams, filters);

        return this.http.get<Page<AccountListItem>>(url, {params: httpParams, observe: 'response'});
    }


    private setPaginationToParams(params: HttpParams, page: Pagination | null): HttpParams {
        return params
            .set('page', page?.currentPage ?? 0)
            .set('size', page?.itemsPerPage ?? 5);
    }

    private buildUserFilterParams(params: HttpParams, filters: UserFilterState | null = null): HttpParams {
        if (!filters) return params;

        if (filters.searchQuery?.trim()) {
            params = params.set('query', filters.searchQuery.trim());
        }

        if (filters.statuses?.length) {
            params = params.set('statuses', filters.statuses.join(','));
        }

        if (filters.roles?.length) {
            params = params.set('roles', filters.roles.join(','));
        }

        return params;
    }


}
