import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Page, Pagination} from "../model/search/search.model";
import {CategoryResponse} from "../model/http/company.model";

@Injectable({
  providedIn: 'root'
})
export class CategoryService {

    constructor(private http: HttpClient) {}


    getCategories(pagination?: Pagination){
        let httpParams = null

        if (pagination) {
            httpParams = new HttpParams()
                .set('page', pagination.currentPage)
                .set('size', pagination.itemsPerPage)
        } else {
            httpParams = new HttpParams()
                .set('page', 0)
                .set('size', 999)
        }

        return this.http.get<Page<CategoryResponse>>('/api/public/categories', {params: httpParams, observe: 'response'}, );
    }

}
