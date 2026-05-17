import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {NgIf} from "@angular/common";
import {RatingStarsComponent} from "../../company-opinions/rating-stars/rating-stars.component";
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {ReviewUserDetailsResponse} from "../../../model/http/review.model";
import {ReviewService} from "../../../service/review.service";
import {ConfirmService} from "../../../service/confirm.service";
import {ToastService} from "../../../service/toast.service";

export interface ReviewEditData {
    rating: number | null;
    comment: string | null;
}

@Component({
    selector: 'app-edit-opinion-modal',
    imports: [
        NgIf,
        RatingStarsComponent,
        ReactiveFormsModule
    ],
    templateUrl: './edit-opinion-modal.component.html',
    styleUrl: './edit-opinion-modal.component.css'
})
export class EditOpinionModalComponent implements OnChanges {
    @Input() isVisible: boolean = false;

    @Input() opinion!: ReviewUserDetailsResponse | undefined;
    @Output() onClose: EventEmitter<void> = new EventEmitter();

    protected MAX_COMMENT_LENGTH = 1024;

    editOpinionForm = new FormGroup({
        rating: new FormControl<number | null>(null, [Validators.required, Validators.min(1), Validators.max(5)]),
        comment: new FormControl<string | null>(null, [Validators.required, Validators.minLength(10), Validators.maxLength(this.MAX_COMMENT_LENGTH)]),
    })

    cpyEditData: ReviewEditData = {
        rating: null,
        comment: null,
    }


    constructor(private reviewService: ReviewService,
                private confirm: ConfirmService,
                private toast: ToastService,
    ) {}


    ngOnChanges(changes: SimpleChanges): void {
        if(changes['opinion']) {
            this.editOpinionForm.patchValue({
                rating: this.opinion ? this.opinion.rating : null,
                comment: this.opinion ? this.opinion.comment : null,
            })

            this.cpyEditData = {
                rating: this.editOpinionForm.controls.rating.value,
                comment: this.editOpinionForm.controls.comment.value
            }
        }
    }

    close() {
        this.isVisible = false;
        this.opinion = undefined;
        this.editOpinionForm.reset();
        this.onClose.emit();
    }

    onRatingChanged($event: number) {
        this.editOpinionForm.controls['rating'].setValue($event);
    }

    async onConfirm(){
        if(this.editOpinionForm.valid && this.isDataChanged()){
            let result = await this.confirm.open("Are you sure to edit this opinion?");
            if(result){
                this.updateReview();
            }
        } else {
            this.editOpinionForm.markAllAsTouched()
        }
    }


    protected onCancel() {
        this.close();
    }



    private updateReview(){
        if(!this.opinion) return;

        let data = {
            rating: this.editOpinionForm.controls.rating.value,
            comment: this.editOpinionForm.controls.comment.value,
        }

        this.reviewService.updateReview(data, this.opinion.id).subscribe({
            next: data => {
                if(data.status == 200 && data.body) {
                    this.opinion = data.body;
                    this.cpyEditData.comment = this.opinion.comment;
                    this.cpyEditData.rating = this.opinion.rating;
                    this.toast.show("Successfully updated review!", "success");
                }
            }, error: error => {
                this.resetDataToDefault();

                switch (error.status) {
                    case 400:
                        this.toast.show(error.error.message, "error");
                        this.close();
                        break;
                }
            }, complete: () => {
                this.close();
            }
        })
    }

    protected isDataChanged(): boolean{
        return this.editOpinionForm.controls.rating.value != this.cpyEditData.rating
            ||
            this.editOpinionForm.controls.comment.value != this.cpyEditData.comment;
    }

    protected resetDataToDefault(): void {
        this.editOpinionForm.controls['rating'].setValue(this.cpyEditData.rating);
        this.editOpinionForm.controls['comment'].setValue(this.cpyEditData.comment);
    }

}
