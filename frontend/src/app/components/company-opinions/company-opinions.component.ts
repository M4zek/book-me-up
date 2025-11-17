import {Component, Input} from '@angular/core';
import {RatingStarsComponent} from "./rating-stars/rating-stars.component";
import {DecimalPipe, KeyValuePipe, NgForOf} from "@angular/common";
import {UserOpinionsListComponent} from "./user-opinions-list/user-opinions-list.component";
import {RatingBarComponent} from "./rating-bar/rating-bar.component";
import {CompanyReviewStatistics} from "../../model/response/company-response.model";

@Component({
  selector: 'app-company-opinions',
    imports: [
        RatingStarsComponent,
        UserOpinionsListComponent,
        DecimalPipe,
        NgForOf,
        KeyValuePipe,
        RatingBarComponent
    ],
  templateUrl: './company-opinions.component.html',
  styleUrl: './company-opinions.component.css'
})
export class CompanyOpinionsComponent {

  showUserOpinionsList = false;

  @Input() opinionsDetails: CompanyReviewStatistics = {
      rating: 0,
      totalReviews: 0,
      ratingCounts: { 1:0 }
  };


    onShowUserOpinionsListChange($event: Event) {
        this.showUserOpinionsList = ($event.target as HTMLInputElement).checked;

        // TODO MAKE A REQUEST TO DOWNLOAD OPINION LIST
        // THEN SEND THE LIST TO UserOpinionListContainer

    }
}
