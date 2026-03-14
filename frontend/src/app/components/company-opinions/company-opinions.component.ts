import {Component, ElementRef, Input, ViewChild} from '@angular/core';
import {RatingStarsComponent} from "./rating-stars/rating-stars.component";
import {DecimalPipe, KeyValuePipe, NgForOf, NgIf} from "@angular/common";
import {UserOpinionsListComponent} from "./user-opinions-list/user-opinions-list.component";
import {RatingBarComponent} from "./rating-bar/rating-bar.component";
import {CompanyReviewStatistics} from "../../model/http/company.model";
import {ReviewService} from "../../service/review.service";
import {Pagination} from "../../model/search/search.model";
import {FormsModule} from "@angular/forms";
import {ReviewUserDetailsResponse} from "../../model/http/review.model";
import {DoubleSpinnerComponent} from "../double-spinner/double-spinner.component";

@Component({
  selector: 'app-company-opinions',
    imports: [
        RatingStarsComponent,
        UserOpinionsListComponent,
        DecimalPipe,
        NgForOf,
        KeyValuePipe,
        RatingBarComponent,
        FormsModule,
        NgIf,
        DoubleSpinnerComponent
    ],
  templateUrl: './company-opinions.component.html',
  styleUrl: './company-opinions.component.css'
})
export class CompanyOpinionsComponent {

  showUserOpinionsList = false;
  isUserOpinionsLoading = false;

  @ViewChild('scrollContainer') scrollContainer!: ElementRef;

  @Input() companyId: number | null = null;
  protected selected_rating: number | null = null;

  @Input() opinionsDetails: CompanyReviewStatistics = {
      rating: 0,
      totalReviews: 0,
      ratingCounts: { 1:0 }
  };

  pagination: Pagination = {
      totalItems: -1,
      itemsPerPage: 10,
      currentPage: 0,
  }

  opinions: ReviewUserDetailsResponse[] = [];

  constructor(private reviewService: ReviewService) {}

  onShowUserOpinionsListChange($event: Event) {
      this.showUserOpinionsList = ($event.target as HTMLInputElement).checked;

      if(this.showUserOpinionsList && this.companyId) {
          this.readOpinions();
      } else {
          this.selected_rating = null;
          this.opinions = [];
      }
  }

  protected readOpinions(){

      if(!this.companyId) return;

      const container = this.scrollContainer.nativeElement;
      const previousScrollTop = container.scrollTop;

      this.isUserOpinionsLoading = true;

      this.reviewService.readCompanyReviews(this.companyId, this.selected_rating, this.pagination)
          .subscribe({
              next: response => {
                  if (response.status === 200 && response.body) {

                      const newOpinions = response.body.content;
                      let index = 0;
                      const interval = setInterval(() => {
                          if (index >= newOpinions.length) {
                              clearInterval(interval);
                              return;
                          }

                          this.opinions = [...this.opinions, newOpinions[index]];
                          index++;

                          container.scrollTop = previousScrollTop;

                      }, 50);

                      this.pagination.currentPage = response.body.page.number;
                      this.pagination.totalItems = response.body.page.totalElements;
                  }
              },
              error: error => {
                  console.log(error)
              },
              complete: () => {
                  this.isUserOpinionsLoading = false;
              }
          })
  }


  protected selectRating(rating_str: string) {
      if(this.selected_rating != Number(rating_str)) {
          this.selected_rating = Number(rating_str);
          this.showUserOpinionsList = true;
      } else {
          this.selected_rating = null;
      }
      this.opinions = [];
      this.readOpinions();
  }

  protected showMore() {
     this.pagination.currentPage++;
     let total_read = this.opinions.length;

     if(total_read != this.pagination.totalItems) {
         this.readOpinions();
     }
  }
}
