import {Injectable} from '@angular/core';
import {BehaviorSubject, filter, map, Observable} from "rxjs";
import {UserContextModel, UserContextWrapper} from "../model/auth/auth.model";
import {Router} from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class UserContextService {

    protected readonly userKey: string = 'userSession'

    private currentUser$: BehaviorSubject<UserContextWrapper> =
        new BehaviorSubject<UserContextWrapper>({loggedIn: false});


    constructor(private router: Router) {
        const userSession: string | null =  localStorage.getItem(this.userKey);
        if(userSession != null){
            const parsedData: UserContextWrapper = JSON.parse(userSession);
            this.currentUser$ = new BehaviorSubject<UserContextWrapper>(parsedData);
        }
    }

    public setLoggedUser(userLogged: UserContextModel): void {
        this.currentUser$.next({
            loggedIn: true,
            userContext: {
                id: userLogged.id,
                roles: userLogged.roles,
                token: userLogged.token,
                refreshToken: userLogged.refreshToken
            }
        });
        localStorage.setItem(this.userKey, JSON.stringify(this.currentUser$.value));
    }

    public updateTokens(accessToken: string, refreshToken: string){
        const data = localStorage.getItem(this.userKey);
        if(data){
            const user = JSON.parse(data);
            this.currentUser$.next({
                loggedIn: true,
                userContext: {
                    id: user.userContext.id,
                    roles: user.userContext.roles,
                    token: accessToken,
                    refreshToken: refreshToken
                }
            });
            localStorage.setItem(this.userKey, JSON.stringify(this.currentUser$.value));
        }
    }

    public deleteUserFromStorage(): void {
        localStorage.clear();
        this.currentUser$.next({loggedIn: false, userContext: undefined});
        this.router.navigate(['/', 'guest'])
    }

    public isLoggedIn(): Observable<boolean> {
        return this.currentUser$.pipe(
            map((res) => res.loggedIn)
        );
    }

    public getUserContext(): Observable<UserContextModel> {
        return this.currentUser$.pipe(
            filter((wrapper) => wrapper.loggedIn),
            map((wrapper) => wrapper.userContext!)
        );
    }

    public getUserRefreshToken(): Observable<string>{
        return this.currentUser$.pipe(
            map((wrapper) => wrapper.loggedIn ? wrapper.userContext!.refreshToken : '')
        );
    }

    public getUserToken(): Observable<string> {
        return this.currentUser$.pipe(
            map((wrapper) => wrapper.loggedIn ? wrapper.userContext!.token : '')
        );
    }

}
