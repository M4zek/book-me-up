import {Component} from '@angular/core';
import {RatingStarsComponent} from "./rating-stars/rating-stars.component";
import {RatingBarComponent} from "./rating-bar/rating-bar.component";
import {NgForOf} from "@angular/common";
import {UserOpinionsListComponent} from "./user-opinions-list/user-opinions-list.component";

@Component({
  selector: 'app-company-opinions',
    imports: [
        RatingStarsComponent,
        RatingBarComponent,
        NgForOf,
        UserOpinionsListComponent
    ],
  templateUrl: './company-opinions.component.html',
  styleUrl: './company-opinions.component.css'
})
export class CompanyOpinionsComponent {

  showUserOpinionsList = false;

  opinionsDetails=
    {
      overallOpinions: 521,
      overallRating: 4.7,
      details: [
        [5, 321],
        [4, 222],
        [3, 100],
        [2, 10],
        [1, 2]
      ]
    }


    onShowUserOpinionsListChange($event: Event) {
        this.showUserOpinionsList = ($event.target as HTMLInputElement).checked;

        // TODO MAKE A REQUEST TO DOWNLOAD OPINION LIST
        // THEN SEND THE LIST TO UserOpinionListContainer

    }
}
