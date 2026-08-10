import {Component, ViewChild} from '@angular/core';
import {FormsModule, NgForm} from "@angular/forms";
import {NgIf} from "@angular/common";
import {CategoryService} from "../../service/category.service";
import {DoubleSpinnerComponent} from "../double-spinner/double-spinner.component";
import {debounceTime, Subject, Subscription, tap} from "rxjs";


export interface CategorySubject{
    category: string;
    categoryConfirm: string;
}

@Component({
  selector: 'app-new-category-form',
    imports: [
        FormsModule,
        NgIf,
        DoubleSpinnerComponent
    ],
  templateUrl: './new-category-form.component.html',
  styleUrl: './new-category-form.component.css'
})
export class NewCategoryFormComponent {

    @ViewChild('categoryForm') cat_form!: NgForm;

    categorySubject: Subject<CategorySubject> = new Subject<CategorySubject>();
    private sub: Subscription;

    categories: CategorySubject = {category: "", categoryConfirm: ""};

    isLoading: boolean = false;
    requestStatus: number = 0

    countdown = 0;
    private intervalId: any;

    constructor(categoryService: CategoryService) {
        this.sub = this.categorySubject
            .pipe(tap(() => {
                clearInterval(this.intervalId);
                this.countdown = 0;
            }), debounceTime(2000))
            .pipe(tap(() => {
                clearInterval(this.intervalId);

                this.countdown = 30;

                this.intervalId = setInterval(() => {
                    this.countdown--;
                        if (this.countdown <= 0) {
                            clearInterval(this.intervalId);
                        }
                    }, 1000);
                }), debounceTime(30000))
            .subscribe(() => {
                this.cat_form.reset();
            });
    }


    createCategory(categoryForm: any) {
        if (categoryForm.invalid) {
            categoryForm.control.markAsTouched();
            return;
        }
        this.isLoading = true;

        setTimeout(() => {
            this.requestStatus = 200;
            this.isLoading = false;
        }, 3000)
    }

    onOkClick() {
        this.isLoading = true;

        setTimeout(() => {
            this.isLoading = false;
            this.requestStatus = 300;
        },3000)

    }

    onResetClick() {
        this.isLoading = true;

        setTimeout(() => {
            this.isLoading = false;
            this.requestStatus = 0;
        }, 3000)
    }

    onCategoryChanged(category: string){
        this.categories.category = category;

        if(this.categories.category === null) return
        this.categorySubject.next(this.categories);
    }

    onCategoryConfirm(category: string){
        this.categories.categoryConfirm = category;

        if(this.categories.categoryConfirm === null) return
        this.categorySubject.next(this.categories);
    }
}
