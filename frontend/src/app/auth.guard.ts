import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree} from '@angular/router';
import {UserContextService} from "./service/user-context.service";
import {map, Observable} from "rxjs";
import {Injectable} from "@angular/core";

@Injectable({
    providedIn: 'root'
})
export class AuthGuard implements CanActivate {

    constructor(private userContextService: UserContextService, private router: Router) {}

    canActivate(
        route: ActivatedRouteSnapshot,
        state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
        return this.userContextService.isLoggedIn().pipe(
            map(loggedIn => {
                if (!loggedIn) {
                    return this.router.createUrlTree(['', 'guest']);
                }
                return true;
            })
        );
    }
}
