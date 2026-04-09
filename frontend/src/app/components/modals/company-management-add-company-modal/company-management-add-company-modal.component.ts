import {AfterViewInit, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {DecimalPipe, NgForOf, NgIf} from "@angular/common";
import {FormsModule, NgForm, ReactiveFormsModule} from "@angular/forms";
import {Address} from "../../../model/gui/gui.model";
import {DropDownListComponent, DropDownListItem} from "../../drop-down-list/drop-down-list.component";
import {
    CompanyDetailsResponse,
    CompanyHours,
    CompanyRequest,
    EmployeeSummaryResponse
} from "../../../model/http/company.model";
import {MapComponent} from "../../map/map.component";
import {DoubleSpinnerComponent} from "../../double-spinner/double-spinner.component";
import {ToastService} from "../../../service/toast.service";
import {ConfirmService} from "../../../service/confirm.service";
import {getCompanyLogo, getUserAvatar} from "../../../utils.functions";
import {CompanyService} from "../../../service/company.service";
import {CategoryService} from "../../../service/category.service";
import {UserContextService} from "../../../service/user-context.service";
import {HttpResponse} from "@angular/common/http";

export interface Details {
  avatar: string;
  name: string;
  description: string;
  category: {name: string, isCorrect: boolean, isTouched: boolean};
}

export interface Step{
    name: string;
    model?: Address | CompanyHours[] | Details;
    isCorrect?: boolean;
}

@Component({
  selector: 'app-company-management-add-company-modal',
    imports: [
        NgIf,
        ReactiveFormsModule,
        FormsModule,
        NgForOf,
        DropDownListComponent,
        MapComponent,
        DoubleSpinnerComponent,
        DecimalPipe
    ],
  templateUrl: './company-management-add-company-modal.component.html',
  styleUrl: './company-management-add-company-modal.component.css'
})
export class CompanyManagementAddCompanyModalComponent implements OnInit, AfterViewInit {
    protected readonly getCompanyLogo = getCompanyLogo;
    protected readonly getUserAvatar = getUserAvatar;


    @Input() isVisible: boolean = false;
    @Output() closeModal = new EventEmitter<void>();
    @Output() showNewCompany = new EventEmitter<Number>();
    @Output() companyCreated = new EventEmitter<CompanyDetailsResponse>();

    details: Details = {
        avatar: '', description: '', name: '', category: {
            name: '',
            isCorrect: false,
            isTouched: false,
        },
    };

    owner!: EmployeeSummaryResponse;

    address: Address = {
        city: '', postalCode: '', street: '', buildingNumber: ''
    }

    hours: CompanyHours[] = [
        {dayOfWeek: "Monday", openTime: '08:00', closeTime: '16:00', open: false},
        {dayOfWeek: "Tuesday", openTime: '08:00', closeTime: '16:00', open: false},
        {dayOfWeek: "Wednesday", openTime: '08:00', closeTime: '16:00', open: false},
        {dayOfWeek: "Thursday", openTime: '08:00', closeTime: '16:00', open: false},
        {dayOfWeek: "Friday", openTime: '08:00', closeTime: '16:00', open: false},
        {dayOfWeek: "Saturday", openTime: '08:00', closeTime: '16:00', open: false},
        {dayOfWeek: "Sunday", openTime: '08:00', closeTime: '16:00', open: false},
    ]

    steps: Step[] = [
        { name: 'Details', model: this.details},
        { name: 'Address', model: this.address},
        { name: 'Opening hours', model: this.hours},
        { name: 'Summary'},
        { name: 'Result'}
    ]
    currentStep = 0;

    categories: DropDownListItem[] = [];

    isCompanyCreating: boolean = false;
    companyResponse: HttpResponse<CompanyDetailsResponse> | null = null;

    constructor(private toast: ToastService,
                private userContextService: UserContextService,
                private confirmService: ConfirmService,
                private companyService: CompanyService,
                private categoryService: CategoryService,
                ) {
    }


    ngOnInit() {
        this.userContextService.getUserData().subscribe({
            next: data => {
                this.owner = {
                    id: data.id,
                    firstName: data.firstName,
                    lastName: data.lastName,
                    avatar: data.avatar ? data.avatar : '',
                }
            }
        })
    }

    ngAfterViewInit() {
        this.categoryService.getCategories().subscribe({
            next: data => {
                if(data.status === 200 && data.body){
                    data.body.content.forEach(category => {
                        this.categories.push({
                            id: category.id,
                            content: category.name,
                        })
                    })
                }
            }, error: err => {
                console.log(err);
            }, complete: () => {

            }
        })
    }

    nextStep(form: NgForm | null) {
        if(this.currentStep > 1 && this.steps.length - 1) {
            this.currentStep++;
            return;
        }

        if(!form) return;

        if (this.currentStep < this.steps.length - 1) {
            switch (this.currentStep) {
                case 0:
                      if(!form.form.invalid && this.details.category.isCorrect) {
                          this.currentStep++;
                      } else{
                          form.form.markAllAsTouched();
                          this.details.category.isTouched = true;
                      }
                    break;

                case 1:
                    if(!form.form.invalid){
                        this.currentStep++;
                    } else {
                        form.form.markAllAsTouched();
                    }
                    break;
            }
        }
    }

    prevStep() {
        if (this.currentStep > 0) {
            this.currentStep--;
        }
    }


    close(){
      this.closeModal.emit();
    }

    protected onCategoryChanged($event: DropDownListItem) {
      this.details.category.isTouched = true;
      if($event.content.length > 0){
          this.details.category.name = $event.content;
          this.details.category.isCorrect = true;
      } else {
          this.details.category.isCorrect = false;
      }
    }

    protected adjustTime(companyHours: CompanyHours, field: 'openTime' | 'closeTime', part: 'hour' | 'minute', delta: number) {
        const [hourStr, minuteStr] = companyHours[field].split(':');
        let hour = Number(hourStr);
        let minute = Number(minuteStr);

        if (part === 'hour') {
            hour += delta;
            if (hour > 23) hour = 0;
            if (hour < 0) hour = 23;
        } else {
            minute += delta;
            if (minute >= 60) minute = 0;
            if (minute < 0) minute = 50;
        }

        companyHours[field] =
            `${hour.toString().padStart(2,'0')}:${minute.toString().padStart(2,'0')}`;
    }


    protected onFileSelected(event: Event) {
        const input = event.target as HTMLInputElement;
        const files = input.files;

        if (!files || files.length === 0) return;

        const file = files[0];
        const maxSize = 2 * 1024 * 1024; // 2 MB

        if (files.length > 1) {
            this.toast.show('You can upload only one photo', 'error');
            return;
        }

        if (!file.type.startsWith('image/')) {
            this.toast.show(`File "${file.name}" must be an image.`, 'error');
            return;
        }

        if (file.size > maxSize) {
            this.toast.show(`"${file.name}" can be up to 2 MB in size.`, 'error');
            return;
        }

        const reader = new FileReader();
        reader.onload = () => {
            this.details.avatar = reader.result as string;
        };

        reader.readAsDataURL(file);

        input.value = '';
    }



    protected async confirm(retry: boolean = false) {
        const msg_confirm = retry ? 'Do you want to try again?' : 'Are you sure the data is correct?';
        const result = await this.confirmService.open(msg_confirm);

        if(result){
            const companyRequest: CompanyRequest = {
                owner_id: this.owner.id,
                name: this.details.name,
                description: this.details.description,
                category: {
                    name: this.details.category.name,
                },
                logo: this.details.avatar ? this.details.avatar : null,
                address: this.address,
                openingHours: this.hours
            }

            this.isCompanyCreating = true;
            this.companyService.createCompany(companyRequest).subscribe({
                next: result => {
                    if(result.status === 201 && result.body as CompanyDetailsResponse) {
                        this.companyResponse = result
                        this.companyCreated.emit(this.companyResponse.body as CompanyDetailsResponse);
                    }
                }, error: err => {
                    console.log(err);
                }, complete: () => {
                    this.isCompanyCreating = false;
                }
            })
            if(this.currentStep < this.steps.length - 1) {
                this.currentStep++;
            }
        }
    }

    protected continue() {
        if(this.companyResponse?.status === 201){
            this.showNewCompany.emit(this.companyResponse.body?.id);
        }
        this.closeModal.emit();
    }
}
