import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {ReservationResponse, ReservationUpdateModel} from "../../model/http/reservation.model";
import {getUserAvatar} from "../../utils.functions";
import {DropDownListComponent, DropDownListItem} from "../drop-down-list/drop-down-list.component";
import {EmployeeSummaryResponse} from "../../model/http/company.model";
import {ReservationService} from "../../service/reservation.service";
import {NgIf} from "@angular/common";
import {DoubleSpinnerComponent} from "../double-spinner/double-spinner.component";
import {ToastService} from "../../service/toast.service";
import {ConfirmService} from "../../service/confirm.service";

@Component({
  selector: 'app-reservation-details',
    imports: [
        DropDownListComponent,
        NgIf,
        DoubleSpinnerComponent
    ],
  templateUrl: './reservation-details.component.html',
  styleUrl: './reservation-details.component.css'
})
export class ReservationDetailsComponent implements OnChanges {
    protected readonly getUserAvatar = getUserAvatar;

    @Output() close:EventEmitter<void> = new EventEmitter();

    @Input() reservation: ReservationResponse | null = null;
    @Input() company_id: number | null = null;

    @Input() employees: EmployeeSummaryResponse[] = [];

    statusDropDownList: DropDownListItem[] = [
        {id: 1,content: 'Pending', image: 'icons/pending_icon.svg'},
        {id: 2,content: 'Accepted', image: 'icons/accepted_icon.svg'},
        {id: 3,content: 'Rejected', image: 'icons/reject_icon.svg'},
        {id: 4,content: 'Completed', image: 'icons/realized_icon.svg'},
        {id: 5,content: 'Cancelled', image: 'icons/canceled_icon.svg'},
    ]

    employeesDropDownList: DropDownListItem[] = []

    reservationUpdate: ReservationUpdateModel = {
        company_id: -1, reservation_id: -1,
        request: {
            status: '',
            preferred_employee_id: -1
        }
    }

    isReservationUpdating: boolean = false;

    constructor(private reservationService: ReservationService,
                private toast: ToastService,
                private confirm: ConfirmService) {
    }

    ngOnChanges(changes: SimpleChanges): void {
        if(changes['employees']) {
            this.employeesDropDownList = this.convertEmployeeToDropDownListItem(changes['employees'].currentValue)
            this.reservationUpdate.request.preferred_employee_id = this.findCurrentPrefEmployee().id;
        }
        if(changes['company_id']) {
            this.reservationUpdate.company_id = changes['company_id'].currentValue;
        }
        if(changes['reservation']) {
            this.reservationUpdate.reservation_id = changes['reservation'].currentValue.id
            this.reservationUpdate.request.status = this.findCurrentStatus().content;
        }
    }

    protected return() {
        this.close.emit();
    }

    protected convertEmployeeToDropDownListItem(employees: EmployeeSummaryResponse[]): DropDownListItem[]{
        return employees.map(employee => {
            return {
                id: employee.id,
                image: employee.avatar,
                content: `${employee.firstName} ${employee.lastName}`,
            }
        })
    }


    protected findCurrentStatus(): DropDownListItem{
        let current_status =  this.statusDropDownList.find(status => status.content.toUpperCase() === this.reservation?.status) ;
        return current_status ? current_status : {id: -1, content: ''};
    }

    protected findCurrentPrefEmployee(): DropDownListItem{
        let current_PrefEmployee = this.employeesDropDownList.find(employee => employee.id === this.reservation?.preferredEmployee.id);
        return current_PrefEmployee ? current_PrefEmployee : {id: -1, content: ''}
    }

    protected onPrefEmployeeChanged($event: DropDownListItem) {
        if($event.content != 'None'){
            this.reservationUpdate.request.preferred_employee_id = $event.id;
        }
        console.log(this.reservationUpdate);
    }

    protected onStatusChanged($event: DropDownListItem) {
       this.reservationUpdate.request.status = $event.content.toUpperCase();
    }

    protected isReservationChanged(){
        return (
            this.reservation?.status.toUpperCase() === this.reservationUpdate.request.status?.toUpperCase() &&
            this.reservation?.preferredEmployee.id === this.reservationUpdate.request.preferred_employee_id
        )
    }


    protected async confirmChanges(){
        if(!this.isReservationChanged()){
            const result = await this.confirm.open("Confirm to continue...")

            if(result){
                this.isReservationUpdating = true;

                if(this.reservation?.status.toUpperCase() === this.reservationUpdate.request.status?.toUpperCase()){
                    this.reservationUpdate.request.status = '';
                }

                if(this.reservation?.preferredEmployee.id === this.reservationUpdate.request.preferred_employee_id){
                    this.reservationUpdate.request.preferred_employee_id = undefined;
                }

                this.reservationService.patchReservation(this.reservationUpdate).subscribe({
                    next: (response)=> {
                        if(response.status === 200 && response.body){
                            this.reservation!.status=response.body.status;
                            this.reservation!.preferredEmployee = response.body.preferredEmployee;
                        }
                    }, error: (err) => {
                        console.error(err);
                        this.isReservationUpdating = false;
                        this.toast.show("Ups... Something went wrong!", "error");
                    }, complete: () => {
                        this.isReservationUpdating = false;
                    }
                })
            }
        }
    }
}
