import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Page, Pagination} from "../model/search/search.model";
import {CategoryResponse} from "../model/response.model";

@Injectable({
  providedIn: 'root'
})
export class CategoryService {

    constructor(private http: HttpClient) {}


    getCategories(pagination: Pagination){
        const httpParams = new HttpParams()
            .set('page', pagination.currentPage)
            .set('size', pagination.itemsPerPage)


        return this.http.get<Page<CategoryResponse>>('/api/public/categories', {params: httpParams});
    }

}
