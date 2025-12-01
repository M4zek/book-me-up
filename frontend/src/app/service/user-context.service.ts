import {Injectable} from '@angular/core';
import {BehaviorSubject, filter, map, Observable} from "rxjs";
import {UserContextModel, UserContextWrapper} from "../model/http/auth.model";
import {Router} from "@angular/router";
import {UserResponse} from "../model/http/user.model";

@Injectable({
  providedIn: 'root'
})
export class UserContextService {

    protected readonly userKey: string = 'userSession'
    protected readonly userDataKey: string = 'userData'

    private currentUser$: BehaviorSubject<UserContextWrapper> =
        new BehaviorSubject<UserContextWrapper>({loggedIn: false});


    private userData$: BehaviorSubject<UserResponse> = new BehaviorSubject<UserResponse>({
        id: 0,
        firstName: "",
        lastName: "",
        phoneNumber: "",
        email: "",
        birthdate: "",
        avatar: ""
    })

    constructor(private router: Router) {
        const userSession: string | null =  localStorage.getItem(this.userKey);
        const userData: string | null = localStorage.getItem(this.userDataKey);
        if(userSession != null){
            const parsedData: UserContextWrapper = JSON.parse(userSession);
            this.currentUser$ = new BehaviorSubject<UserContextWrapper>(parsedData);
        }
        if(userData != null){
            const parsedData: UserResponse = JSON.parse(userData);
            this.userData$ = new BehaviorSubject<UserResponse>(parsedData);
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


    public setLoggedUserData(userData: UserResponse): void {
        this.userData$.next(userData);
        localStorage.setItem(this.userDataKey, JSON.stringify(this.userData$.value));
    }

    public getUserData(): Observable<UserResponse> {
        return this.userData$.pipe();
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
