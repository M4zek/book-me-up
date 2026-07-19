import {ActivatedRouteSnapshot, CanActivate, CanActivateChild, Router, RouterStateSnapshot} from '@angular/router';
import {Injectable} from "@angular/core";
import {CompanyContextService} from "./service/company-context.service";

@Injectable({
    providedIn: 'root'
})
export class CompanyRoleGuard implements CanActivate, CanActivateChild {


    constructor(private router: Router, private ctx: CompanyContextService) {
    }


    canActivate(
        route: ActivatedRouteSnapshot,
        state: RouterStateSnapshot
    ): boolean {

        const requiredRoles = route.data?.['companyRoles'] as string[] | undefined;

        if (!requiredRoles || requiredRoles.length === 0) {
            return true;
        }

        const company = this.ctx.getCompany();

        if (!company) {
            this.redirectToDefault();
            return false;
        }

        const userRoles: string[] = company.role ?? [];

        const hasAccess = requiredRoles.some(role =>
            userRoles.includes(role)
        );

        if (!hasAccess) {
            this.redirectToDefault();
            return false;
        }

        return true;
    }


    canActivateChild(
        route: ActivatedRouteSnapshot,
        state: RouterStateSnapshot
    ): boolean {
        return this.canActivate(route, state);
    }

    private redirectToDefault(): void {
        this.router.navigate(['/app/company-management/forbidden']);
    }
}