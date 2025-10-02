import {Component, Input, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {CompanyListComponent} from "../../../components/company-list-item/company-list/company-list.component";
import {CompanyListItemComponent} from "../../../components/company-list-item/company-list-item.component";
import {SearchCompanyResult} from "../../../model/search/search.model";
import {Subject, takeUntil} from "rxjs";

@Component({
  selector: 'app-search-result',
  imports: [
    CompanyListComponent,
    CompanyListItemComponent
  ],
  templateUrl: './search-result.component.html',
  styleUrl: './search-result.component.css'
})
export class SearchResultComponent implements OnInit {
  @Input() searchValue: SearchCompanyResult = {}
  private destroy$ = new Subject<void>();

  recommendedText: string = 'Recommended';
  constructor(private route: ActivatedRoute) {}

  ngOnInit() {
    this.route.queryParamMap
        .pipe(takeUntil(this.destroy$))
        .subscribe(params => {
          this.searchValue.name = params.get('name');
          this.searchValue.place = params.get('place');
          this.searchValue.category = params.get('category');

          this.recommendedText = 'Recommended';
          if (this.searchValue.category) this.recommendedText += ` ${this.searchValue.category}`;
          if (this.searchValue.place) this.recommendedText += ` ${this.searchValue.place}`;

          /*
          TODO MAKE REQUEST TO THE BACKEND TO SEARCH FOR COMPANIES BY NAME,PLACE AND CATEGORY!
          If the category has been added, download the best companies from this category in terms of reviews.
          If not, download the best companies overall to the Recommended section.
           */
        });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
