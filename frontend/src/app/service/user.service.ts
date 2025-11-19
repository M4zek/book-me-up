import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {UserResponse} from "../model/response/user-response.model";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http: HttpClient) { }

  readLoggedInUserData(){
    const url = "/api/v1/users/me";
    return this.http.get<UserResponse>(url, {observe: 'response'});
  }




}
