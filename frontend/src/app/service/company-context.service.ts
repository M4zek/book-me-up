import {Injectable} from '@angular/core';
import {UserCompanyResponse} from "../model/http/company.model";
import {BehaviorSubject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class CompanyContextService {

    private currentCompanySubject = new BehaviorSubject<UserCompanyResponse | null>(null);
    currentCompany$ = this.currentCompanySubject.asObservable();

    constructor() { }

    setCompany(company: UserCompanyResponse | null): void {
        this.currentCompanySubject.next(company);
    }

    get roles(): string[] {
        return this.currentCompanySubject.value?.role ?? [];
    }

    hasRole(role: string): boolean {
        return this.roles.includes(role);
    }

    hasAnyRole(...roles: string[]): boolean {
        return roles.some((r:string) => this.getCompany()?.role.includes(r));
    }

    getCompany(){
        return this.currentCompanySubject.value;
    }

    isOwnerLoggedIn(){
        return this.roles.includes("COMPANY_OWNER");
    }
}
