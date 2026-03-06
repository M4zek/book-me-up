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


        return this.userContextService.authState().pipe(
            map(state => {
                // If not logged in, redirect to the guest page
                if (!state) return this.router.createUrlTree(['', 'guest']);

                // Check the required roles to access the subpage
                const req_role = route.data['roles'];
                const hasAccess = state.userContext?.roles.some(r => req_role.includes(r));

                // If user does not have access, redirect to the guest page
                if(!hasAccess) {
                    return this.router.createUrlTree(['', 'guest']);
                }

                // User logged in and have all necessary access
                return true;
            })
        )
    }
}
