import { Injectable } from '@angular/core';
import {UserCompanyResponse} from "../model/http/company.model";
import { BehaviorSubject } from "rxjs";

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
        return this.currentCompanySubject.value?.roles ?? [];
    }

    hasRole(role: string): boolean {
        return this.roles.includes(role);
    }


    getCompany(){
        return this.currentCompanySubject.value;
    }

}
