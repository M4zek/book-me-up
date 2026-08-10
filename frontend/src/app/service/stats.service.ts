import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {DashboardStatistics, UsersStatistics} from "../model/gui/admin.gui.model";

@Injectable({
  providedIn: 'root'
})
export class StatsService {

  url: string = `/api/v1/admin/stats`

  constructor(private http: HttpClient) {}


  getDashboardStats() {
    const dashboard_url = this.url + '/dashboard';
    return this.http.get<DashboardStatistics>(dashboard_url ,{observe: 'response'});
  }


  getUsersStats(){
    const users_url = this.url + '/users';
    return this.http.get<UsersStatistics>(users_url, {observe: 'response'});
  }
}
