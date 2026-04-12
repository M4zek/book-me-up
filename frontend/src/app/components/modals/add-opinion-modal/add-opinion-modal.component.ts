import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {NgIf} from "@angular/common";
import {RatingStarsComponent} from "../../company-opinions/rating-stars/rating-stars.component";
import {CompanyOffersResponse} from "../../../model/http/company.model";
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {UserContextService} from "../../../service/user-context.service";
import {take} from "rxjs";
import {UserResponse} from "../../../model/http/user.model";
import {ReviewOfferRequest} from "../../../model/http/review.model";
import {ReviewService} from "../../../service/review.service";
import {ToastService} from "../../../service/toast.service";

export interface OpinionModalInput {
    offerToReview: CompanyOffersResponse,
    companyNameToReview: string,
    companyLogo: string
}

@Component({
  selector: 'app-add-opinion-modal',
    imports: [
        NgIf,
        RatingStarsComponent,
        FormsModule,
        ReactiveFormsModule
    ],
  templateUrl: './add-opinion-modal.component.html',
  styleUrl: './add-opinion-modal.component.css'
})
export class AddOpinionModalComponent implements OnChanges {

  protected MAX_COMMENT_LENGTH = 1024;

  @Input() inputData: OpinionModalInput | null = null;

  @Input() isVisible: boolean = false;
  @Output() closeModal = new EventEmitter<boolean>();

  constructor(private userContextService: UserContextService,
              private reviewService: ReviewService,
              private toast: ToastService) {}


  ngOnChanges(changes: SimpleChanges) {
    if (changes['inputData'] && this.inputData?.offerToReview) {
        this.userContextService.getUserData()
            .pipe(take(1))
            .subscribe((user: UserResponse) => {
                this.reviewForm.controls['author_id'].setValue(user.id);
            })

        this.reviewForm.controls['company_offer_id'].setValue(this.inputData?.offerToReview.id);
    } else {
        console.error("Unable to open Opinion Modal");
    }
  }

   reviewForm = new FormGroup({
       author_id: new FormControl<number | null>(null, Validators.required),
       company_offer_id: new FormControl<number | null>(null, Validators.required),
       rating: new FormControl<number | null>(null, [Validators.required, Validators.min(1), Validators.max(5)]),
       comment: new FormControl<string | null>(null, [Validators.required, Validators.minLength(10), Validators.maxLength(this.MAX_COMMENT_LENGTH)]),
   });

  close(value: boolean = false) {
    this.inputData = null;
    this.reviewForm.clearValidators();
    this.closeModal.emit(value);
  }

  onRatingChange($event: number) {
    this.reviewForm.controls['rating'].setValue($event);
  }

  confirm() {
    if (this.reviewForm.valid) {
        let reviewRequest: ReviewOfferRequest = this.reviewForm.value as ReviewOfferRequest;
        this.reviewService.createReview(reviewRequest).subscribe(response => {
            if (response.status === 201 && response.body) {
                this.toast.show("Review created", "success")
                this.closeModal.emit(true);
            } else  {
                this.toast.show("Error during review creation.", "error");
            }
        })
    } else {
        this.reviewForm.markAllAsTouched();
    }

  }
}
