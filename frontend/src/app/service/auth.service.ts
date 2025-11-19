import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {UserContextService} from "./user-context.service";
import {AuthRequest, LoginWrapper, RefreshTokenResponse, UserContextModel} from "../model/auth/auth.model";
import {catchError, map, Observable, of} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient, private userContextService: UserContextService) { }

    public authentication(source: AuthRequest): Observable<LoginWrapper>{
        const url: string = '/api/auth/authenticate';
        return this.http.post<UserContextModel>(url, source)
            .pipe(
                map((result: UserContextModel) => {
                    return {success: true, loggedUser: result}
                }),
                catchError(err => {
                    const errorMessage = err.error;
                    return of({success:false, errorMessage: errorMessage});
                })
            )
    }

    refreshToken(): Observable<RefreshTokenResponse> {
        let refreshToken: string | undefined;
        this.userContextService.getUserRefreshToken().subscribe(token => refreshToken = token);
        return this.http.post<RefreshTokenResponse>(`/api/auth/refreshToken/${refreshToken}`, null);
    }


}
