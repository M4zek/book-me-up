import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {catchError, Observable, switchMap, throwError} from "rxjs";
import {Injectable} from "@angular/core";
import {RefreshTokenResponse} from "./model/http/auth.model";
import {UserContextService} from "./service/user-context.service";
import {AuthService} from "./service/auth.service";
import {ToastService} from "./service/toast.service";
import {Router} from "@angular/router";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

    constructor(private userContextService: UserContextService,
                private authService: AuthService,
                private router: Router,
                private toast: ToastService) {}

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        if(request.url.includes('/api/v1/')) {
            let token: string = '';
            this.userContextService.getUserToken().subscribe(JWT => token = "Bearer " + JWT);
            if (token.length > 7) {
                request = request.clone({
                    setHeaders: {Authorization: token}
                });
            }
        }

        return next.handle(request)
            .pipe(
                catchError(err => {
                    if(err instanceof HttpErrorResponse){
                        switch (err.status) {
                            case 401:
                                // console.log(`401 :::: ${err.error.message}`);
                                if(String(err.error.message).includes("JWT has expired at")){
                                    return this.refreshTokenAndRetryRequest(request, next);
                                }
                                if(String(err.error.message).includes("Wrong token format")){
                                    this.userContextService.deleteUserFromStorage();
                                    this.toast.show("Ups somethings went wrong. Pleas login again...", "warning")
                                }
                                break;

                            case 403:
                                console.log(`403 :::: ${err.error.message}`);
                                if(
                                    String(err.error.message).includes('Session has expired, please log in again!')
                                    ||
                                    (String(err.error.message).includes('Invalid refresh token'))){
                                    this.userContextService.deleteUserFromStorage();
                                    this.toast.show("Your session has expired, please log in again...", "warning");
                                }
                                break;

                            case 500:
                                this.router.navigate(['/server-error']);
                                this.toast.show("Internal server error", "error");
                                break;
                        }
                    }
                    return throwError(err);
                })
            )
    }


    private refreshTokenAndRetryRequest(request: HttpRequest<any>, next: HttpHandler): Observable<any>{
        return this.authService.refreshToken().pipe(
            switchMap((response: RefreshTokenResponse) => {
                const newToken = response.accessToken;
                const refreshToken = response.refreshToken;

                this.userContextService.updateTokens(newToken, refreshToken);

                const repliedRequest = request.clone({
                    setHeaders: {
                        Authorization: `Bearer ${newToken}`
                    }
                });

                return next.handle(repliedRequest);
            }),
            catchError((err) => {
                // console.error(err);
                return throwError(err);
            })
        )
    }
}
