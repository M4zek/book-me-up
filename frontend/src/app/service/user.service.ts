import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {UserHireDetails, UserResponse} from "../model/http/user.model";
import {Page, Pagination} from "../model/search/search.model";

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


}
