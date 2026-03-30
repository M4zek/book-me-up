import {Component, OnInit} from '@angular/core';
import {DatePipe, NgForOf, NgIf} from "@angular/common";
import {getUserAvatar, getWeekdayNumber, idToColor} from "../../../utils.functions";
import {CompanyService} from "../../../service/company.service";
import {CompanyContextService} from "../../../service/company-context.service";
import {filter} from "rxjs";
import {CompanyHours, EmployeeSummaryResponse} from "../../../model/http/company.model";
import {CompanyReservationsSearch, Pagination} from "../../../model/search/search.model";
import {ReservationService} from "../../../service/reservation.service";
import {ReservationResponse} from "../../../model/http/reservation.model";
import {DoubleSpinnerComponent} from "../../../components/double-spinner/double-spinner.component";
import {ReservationDetailsComponent} from "../../../components/reservation-details/reservation-details.component";

interface CalendarEvent{
    id: number;
    day:number
    start:string
    end:string
    title:string
    employee: EmployeeSummaryResponse
}

interface PositionedEvent extends CalendarEvent{
    left:number
    width:number
}

interface DatePicker{
    start: Date,
    end: Date,
}

@Component({
  selector: 'app-company-management-bookings-calendar',
    imports: [
        NgForOf,
        NgIf,
        DatePipe,
        DoubleSpinnerComponent,
        ReservationDetailsComponent
    ],
  templateUrl: './company-management-bookings-calendar.component.html',
  styleUrl: './company-management-bookings-calendar.component.css'
})
export class CompanyManagementBookingsCalendarComponent implements OnInit {
    protected readonly getUserAvatar = getUserAvatar;
    protected readonly idToColor = idToColor;

    isReservationsLoading: boolean = false;
    isEmployeesLoading: boolean = false;
    isHoursLoading: boolean = false;


    startHour= 0
    endHour= 0
    step= 10
    pixelsPerStep= 20


    selectedReservation: ReservationResponse | null = null;

    days = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"];

    times:string[]=[]
    datePicker!: DatePicker;


    company_id!: number;
    events: CalendarEvent[] = [];
    employees: EmployeeSummaryResponse[] = [];
    reservations: ReservationResponse[] = [];
    hours: CompanyHours[] = [];

    constructor(private companyService: CompanyService,
                private ctx: CompanyContextService,
                private reservationService: ReservationService){}

    ngOnInit() {
        this.initDatePickerData();
        this.loadEmployeesAndReservations();
    }



    loadEmployeesAndReservations(){
        this.ctx.currentCompany$
            .pipe(
                filter(company => company !== null)
            )
            .subscribe(company => {
                const company_id = company!.id;
                this.company_id = company_id;
                this.readCompanyOpenHours(company_id);
                this.getCompanyEmployees(company_id);
                this.getCompanyReservations(this.datePicker.start, this.datePicker.end, company_id);
            })

    }

    initDatePickerData() {
        const today = new Date();

        const day = today.getDay();

        const diffToMonday = (day === 0 ? -6 : 1 - day);

        const start = new Date(today);
        start.setDate(today.getDate() + diffToMonday);

        start.setHours(0, 0, 0, 0);

        const end = new Date(start);
        end.setDate(start.getDate() + 6);

        this.datePicker = {
            start: start,
            end: end
        };
    }

    generateTimes(){

        let minutes=this.startHour*60
        const end=this.endHour*60

        this.times = []

        while(minutes<=end){

            const h=Math.floor(minutes/60)
            const m=minutes%60

            this.times.push(
                `${String(h).padStart(2,"0")}:${String(m).padStart(2,"0")}`
            )

            minutes+=this.step
        }
    }

    getTop(time:string){

        const [h,m]=time.split(":").map(Number)

        const minutes=h*60+m
        const start=this.startHour*60

        return (((minutes-start)/this.step)*this.pixelsPerStep) + 25; // Add margin in first row
    }

    getHeight(start:string,end:string){

        const [sh,sm]=start.split(":").map(Number)
        const [eh,em]=end.split(":").map(Number)

        const startMin=sh*60+sm
        const endMin=eh*60+em

        return ((endMin-startMin)/this.step)*this.pixelsPerStep
    }


    getPositionedEvents(): PositionedEvent[] {
        const positioned: PositionedEvent[] = [];
        const eventsByDay: {[key:number]: CalendarEvent[]} = {};

        for (const e of this.events) {
            (eventsByDay[e.day] = eventsByDay[e.day] || []).push(e);
        }

        for (const dayStr in eventsByDay) {
            const day = Number(dayStr);
            const evs = eventsByDay[day];

            evs.sort((a,b)=> this.getTop(a.start) - this.getTop(b.start));

            const clusters: CalendarEvent[][] = [];

            for (const ev of evs) {
                let placed = false;

                for (const cluster of clusters) {
                    if (cluster.some(cEv => this.isOverlap(ev, cEv))) {
                        cluster.push(ev);
                        placed = true;
                        break;
                    }
                }

                if (!placed) clusters.push([ev]);
            }

            for (const cluster of clusters) {
                const n = cluster.length;
                if (n === 1) {
                    positioned.push({
                        ...cluster[0],
                        left: 0,
                        width: 1
                    });
                } else {

                    for (let i=0; i<n; i++) {
                        positioned.push({
                            ...cluster[i],
                            left: i/n,
                            width: 1/n
                        });
                    }
                }
            }
        }

        return positioned;
    }


    isOverlap(a:CalendarEvent,b:CalendarEvent){
        const [ah,am]=a.start.split(":").map(Number)
        const [bh,bm]=b.start.split(":").map(Number)
        const [ae,am2]=a.end.split(":").map(Number)
        const [be,bm2]=b.end.split(":").map(Number)

        const aStart = ah*60+am
        const aEnd = ae*60+am2
        const bStart = bh*60+bm
        const bEnd = be*60+bm2

        return aStart < bEnd && bStart < aEnd
    }

    onEventClick(id:number){
        if(this.selectedReservation && this.selectedReservation.id === id){
            this.selectedReservation = null;
        } else {
            this.selectedReservation = this.reservations.filter(reservation => reservation.id === id)[0];
        }
    }


    protected nextWeek() {
        this.datePicker.start = this.addDays(this.datePicker.start, 7);
        this.datePicker.end = this.addDays(this.datePicker.end, 7);
        this.getCompanyReservations(this.datePicker.start, this.datePicker.end, this.company_id);
    }

    protected prevWeek() {
        this.datePicker.start = this.addDays(this.datePicker.start, -7);
        this.datePicker.end = this.addDays(this.datePicker.end, -7);
        this.getCompanyReservations(this.datePicker.start, this.datePicker.end, this.company_id);
    }

    protected addDays(date: Date, days: number): Date {
        const newDate = new Date(date);
        newDate.setDate(newDate.getDate() + days);
        return newDate;
    }

    protected isToday(cc: string){
        const today = new Date();
        const todayName = new Intl.DateTimeFormat('en-US', { weekday: 'long' }).format(today);
        return todayName.toUpperCase() === cc.toUpperCase();
    }


    protected getCompanyEmployees(company_id: number){
        this.isEmployeesLoading = true;

        this.companyService.getCompanyEmployees(company_id)
            .pipe(
                filter(response => response.status === 200)
            )
            .subscribe({
                next: response => {
                    if(response.body)
                        this.employees = response.body;
                }, error: error => {
                    console.error(error);
                }, complete: () => {
                    this.isEmployeesLoading = false;
                }
        })

    }

    protected getCompanyReservations(fromDate: Date, toDate: Date, company_id: number){
        const http_params: CompanyReservationsSearch = {
            company_id: company_id,
            fromDate: fromDate,
            toDate: toDate,
        }

        const pagination_full: Pagination = {
            totalItems: 0,
            currentPage: 0,
            itemsPerPage: 9999,
        }

        this.isReservationsLoading = true;
        this.events = [];
        this.reservationService.getCompanyReservations(http_params, pagination_full)
            .pipe(
                filter(response => response.status === 200),
            )
            .subscribe({
                next: response => {
                    if(response.body)
                        this.reservations = response.body.content;
                        this.generateEvents(this.reservations);

                }, error: error => {
                    console.error(error);
                }, complete: () => {
                    this.isReservationsLoading = false;
                }
        })
    }

    protected readCompanyOpenHours(company_id: number){
        this.isHoursLoading = true;
        this.companyService.getCompanyBusinessHours(company_id).pipe(
            filter(response => response.status === 200),
        )
        .subscribe({
            next: response => {
                if(response.body) {
                    this.hours = response.body;
                    this.setHoursRange(this.hours);
                    this.generateTimes();
                }
            }, error: error => {
                console.error(error);
            }, complete: () => {
                this.isHoursLoading = false;
            }
        })
    }


    protected setHoursRange(hours: CompanyHours[]){

        if (!hours?.length) {
            this.startHour = 0;
            this.endHour = 0;
            return;
        }

        const parsed = hours.map(h => {
            const [openH, openM] = h.openTime.split(':').map(Number);
            const [closeH, closeM] = h.closeTime.split(':').map(Number);

            return {
                open: openH + openM / 60,
                close: closeH + closeM / 60
            };
        });

        const min = Math.min(...parsed.map(p => p.open));
        const max = Math.max(...parsed.map(p => p.close));

        this.startHour = Math.floor(min);
        this.endHour = Math.ceil(max);
    }

    protected generateEvents(reservations: ReservationResponse[]){
        if(!reservations?.length){
            return ;
        }

        for(let index = 0; index < reservations.length; index++){
            const reservation = reservations[index];
            const event: CalendarEvent = this.convertReservationToEvent(reservation);
            this.events.push(event);
        }
    }



    private convertReservationToEvent(reservation: ReservationResponse): CalendarEvent{

        const startDate = new Date(reservation.reservationDate);
        const endDate = new Date(reservation.reservationDate);
        endDate.setMinutes(endDate.getMinutes() + reservation.companyOffer.duration);

        const [startH, startM] = [
            startDate.getHours().toString().padStart(2, '0'),
            startDate.getMinutes().toString().padStart(2, '0')
        ];

        const [endH, endM] = [
            endDate.getHours().toString().padStart(2, '0'),
            endDate.getMinutes().toString().padStart(2, '0')
        ];

        const startTime = `${startH}:${startM}`;
        const endTime = `${endH}:${endM}`;
        const day_number = getWeekdayNumber(reservation.reservationDate);

        return {
            id: reservation.id,
            start: startTime,
            end: endTime,
            day: day_number,
            title: reservation.companyOffer.name,
            employee: {
                id: reservation.preferredEmployee.id,
                firstName: reservation.preferredEmployee.firstName,
                lastName: reservation.preferredEmployee.lastName,
                avatar: reservation.preferredEmployee.avatar ? reservation.preferredEmployee.avatar : ''
            }
        };
    }


    protected getDayOfMonth(index: number) {
        const date = new Date(this.datePicker.start);
        date.setDate(date.getDate() + index);
        return date.toLocaleDateString('en-US', {day: '2-digit'});
    }

}
