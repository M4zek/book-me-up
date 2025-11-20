import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {concatMap, throwError} from 'rxjs';
import {CompanyService} from "../../service/company.service";
import {ReservationService} from "../../service/reservation.service";
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {CompanyHours} from "../../model/response/company-response.model";
import {ReservationAvailabilityResponse} from "../../model/response/reservation-response.model";


interface DateModel {
    day: number;
    weekDay: string;
    status: string;
    date: Date;
}


@Component({
    selector: 'app-date-reservation-picker',
    imports: [
        NgClass,
        NgForOf,
        NgIf
    ],
    templateUrl: './date-reservation-picker.component.html',
    styleUrl: './date-reservation-picker.component.css'
})
export class DateReservationPickerComponent implements OnInit {
    animationDateCarouselDirection: string = '';

    currentMonth!: string;
    currentYear!: number;
    days: DateModel[] = [];
    selectedDay!: number;

    @Input() businessHours: CompanyHours[] = []
    @Input() companyId: number = 0;
    bookedInformation: ReservationAvailabilityResponse[] = []


    @Output() changeDate = new EventEmitter();

    today = new Date();
    activeDate!: Date;

    // TIME HOUR VARIABLES
    animationTimeCarouselDirection: string = '';
    slots: { label: string, start: Date, end: Date, disabled: boolean }[] = [];
    visibleSlots: { label: string, start: Date, end: Date, disabled: boolean }[] = [];
    selectedSlot?: { label: string, start: Date, end: Date };
    private index = 0;

    visibleCount = 4;
    isTodayDate: boolean = false;
    @Input() duration = 30;
    @Input() startHour = 8; // default fallback
    @Input() endHour = 20;  // default fallback


    constructor(private companyService: CompanyService,
                private reservationService: ReservationService) {}


    ngOnInit() {
        this.activeDate = new Date(this.today);

        // Download business hours once
        this.companyService.getCompanyBusinessHours(this.companyId)
            .pipe( concatMap(response => {
                if (response.status === 200 && response.body) {
                    this.businessHours = response.body;

                    // generate initial week and load availability for it
                    this.generateWeek();

                    // fetch availability for the active week
                    return this.fetchAvailabilityForActiveWeek();

                } else {
                    return throwError(() => new Error('Error during download company business hours'));
                }
            })).subscribe(availabilityResponse => {
            if (availabilityResponse.status === 200 && availabilityResponse.body) {
                this.bookedInformation = availabilityResponse.body;

                // after we have availability, update day statuses and select today
                this.updateDaysStatusFromBookings();
                this.selectToday();

                // now generate slots for selected day
                this.generateSlots();
                this.updateVisible();
            }
        }, error => {
            console.error('Error loading availability', error);
        })
    }


    private selectToday() {
        const todayModel = this.days.find(d =>
            d.date.toDateString() === this.today.toDateString()
        );

        if (todayModel && !this.isClosed(todayModel.date)) {
            this.selectedDay = todayModel.day;
            this.isToday(todayModel.date);
        } else {
            // if today is closed or not in days, select first open day
            const firstOpen = this.days.find(d => !this.isClosed(d.date));
            if (firstOpen) {
                this.selectedDay = firstOpen.day;
                this.isToday(firstOpen.date);
            }
        }
    }

    updateMonthYear() {
        this.currentYear = this.activeDate.getFullYear();
        this.currentMonth = this.activeDate.toLocaleString('en-US', { month: 'long' });
    }

    generateWeek() {
        this.days = [];
        const startOfWeek = new Date(this.activeDate);

        // normalize: start week to Monday
        startOfWeek.setDate(this.activeDate.getDate() - this.activeDate.getDay() + 1);

        for (let i = 0; i < 7; i++) {
            const d = new Date(startOfWeek);
            d.setDate(startOfWeek.getDate() + i);

            this.days.push({
                day: d.getDate(),
                weekDay: d.toLocaleDateString('en-US', { weekday: 'short' }),
                status: 'red',
                date: d
            });
        }
        this.updateMonthYear();
    }



    selectDay(value: DateModel) {
        this.selectedDay = value.day;
        this.isToday(value.date);
        this.selectedSlot = undefined;
        this.generateSlots();
        this.updateVisible();
    }

    nextWeek() {
        this.animationDateCarouselDirection = 'slide-left';
        this.activeDate.setDate(this.activeDate.getDate() + 7);
        this.generateWeek();

        // after changing active week, fetch availability for that week
        this.fetchAvailabilityForActiveWeek().subscribe(resp => {
            if (resp.status === 200 && resp.body) {
                this.bookedInformation = resp.body;
                this.updateDaysStatusFromBookings();

                // if the currently selected day is outside new week or closed, reset selection
                const selectedDate = new Date(this.currentYear, this.activeDate.getMonth(), this.selectedDay);
                if (!this.days.some(d => d.date.toDateString() === selectedDate.toDateString() && !this.isClosed(d.date))) {
                    this.selectToday();
                }
                this.generateSlots();
                this.updateVisible();
            }
        }, err => console.error(err));

        setTimeout(() => {
            this.animationDateCarouselDirection = '';
        }, 300);
    }

    prevWeek() {
        const newDate = new Date(this.activeDate);
        newDate.setDate(this.activeDate.getDate() - 7);

        if (newDate < this.today) return;

        this.animationDateCarouselDirection = 'slide-right';
        this.activeDate = newDate;
        this.generateWeek();

        this.fetchAvailabilityForActiveWeek().subscribe(resp => {
            if (resp.status === 200 && resp.body) {
                this.bookedInformation = resp.body;
                this.updateDaysStatusFromBookings();
                this.generateSlots();
                this.updateVisible();
            }
        }, err => console.error(err));

        setTimeout(() => {
            this.animationDateCarouselDirection = '';
        }, 300);
    }

    nextMonth() {
        this.activeDate.setMonth(this.activeDate.getMonth() + 1);
        this.generateWeek();

        this.fetchAvailabilityForActiveWeek().subscribe(resp => {
            if (resp.status === 200 && resp.body) {
                this.bookedInformation = resp.body;
                this.updateDaysStatusFromBookings();
                this.generateSlots();
                this.updateVisible();
            }
        }, err => console.error(err));
    }

    prevMonth() {
        const newDate = new Date(this.activeDate);
        newDate.setMonth(this.activeDate.getMonth() - 1);

        if (newDate < this.today) return;
        this.activeDate = newDate;
        this.generateWeek();

        this.fetchAvailabilityForActiveWeek().subscribe(resp => {
            if (resp.status === 200 && resp.body) {
                this.bookedInformation = resp.body;
                this.updateDaysStatusFromBookings();
                this.generateSlots();
                this.updateVisible();
            }
        }, err => console.error(err));
    }

    isDisabled(d: Date): boolean {
        return d < new Date(this.today.getFullYear(), this.today.getMonth(), this.today.getDate());
    }

    isClosed(d: Date): boolean {
        const ch = this.getCompanyHoursForDate(d);

        if (!ch) return true;

        if (!ch.open) return true;

        const now = new Date();
        const isToday =
            d.getDate() === now.getDate() &&
            d.getMonth() === now.getMonth() &&
            d.getFullYear() === now.getFullYear();

        if (isToday) {
            const [closeH, closeM] = ch.closeTime.split(':').map(Number);
            const closeDate = new Date(now);
            closeDate.setHours(closeH, closeM, 0, 0);

            if (now > closeDate) {
                return true;
            }
        }

        return false;
    }

    private isToday(date:Date){
        const today = new Date();
        this.isTodayDate = date.getDate() === today.getDate() &&
            date.getMonth() === today.getMonth() &&
            date.getFullYear() === today.getFullYear();
    }


    // TIME HOUR METHODS
    generateSlots() {
        this.slots = [];

        const base = new Date(this.activeDate.getFullYear(), this.activeDate.getMonth(), this.selectedDay);

        // get company hours for selected day (if available) or fallback to inputs
        const companyHours = this.getCompanyHoursForDate(base);
        let dayStart = this.startHour;
        let dayEnd = this.endHour;

        if (companyHours) {
            // parse "HH:mm" or "H:mm" strings
            const [openH, openM] = companyHours.openTime.split(':').map(s => Number(s));
            const [closeH, closeM] = companyHours.closeTime.split(':').map(s => Number(s));
            dayStart = openH + (openM > 0 ? openM/60 : 0);
            dayEnd = closeH + (closeM > 0 ? closeM/60 : 0);
        }

        let baseStartHour = Math.floor(dayStart);
        let baseStartMinute = Math.round((dayStart - baseStartHour) * 60);

        if (this.isTodayDate){
            const now = new Date();
            // start from next full slot
            const nextSlot = new Date(now.getTime());
            nextSlot.setMinutes(Math.ceil(now.getMinutes() / this.duration) * this.duration, 0, 0);
            baseStartHour = nextSlot.getHours();
            baseStartMinute = nextSlot.getMinutes();

            // if nextSlot is before business open, use business open
            if (companyHours) {
                const openDate = new Date(base);
                openDate.setHours(Number(companyHours.openTime.split(':')[0]), Number(companyHours.openTime.split(':')[1]));
                if (nextSlot < openDate) {
                    baseStartHour = Number(companyHours.openTime.split(':')[0]);
                    baseStartMinute = Number(companyHours.openTime.split(':')[1]);
                }
            }
        }

        base.setHours(baseStartHour, baseStartMinute, 0, 0);

        const end = new Date(this.activeDate.getFullYear(), this.activeDate.getMonth(), this.selectedDay);
        end.setHours(Math.floor(dayEnd), Math.round((dayEnd - Math.floor(dayEnd)) * 60), 0, 0);

        let current = new Date(base);

        while (current < end) {
            const slotStart = new Date(current);
            const slotEnd = new Date(current);
            slotEnd.setMinutes(slotStart.getMinutes() + this.duration);

            if(slotEnd > end) { break }

            const disabled = this.isOverlapping(slotStart, slotEnd);

            this.slots.push({
                label: `${this.formatTime(slotStart)} - ${this.formatTime(slotEnd)}`,
                start: slotStart,
                end: slotEnd,
                disabled
            });

            current.setMinutes(current.getMinutes() + this.duration);
        }
    }

    formatTime(date: Date): string {
        return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    }

    updateVisible() {
        this.visibleSlots = this.slots.slice(this.index, this.index + this.visibleCount);
    }

    next() {
        if (this.index + this.visibleCount < this.slots.length) {
            this.index += this.visibleCount;
            this.updateVisible();
            this.animationTimeCarouselDirection = 'slide-left';
            setTimeout(() => {
                this.animationTimeCarouselDirection = ''
            }, 300)
        }
    }

    prev() {
        if (this.index - this.visibleCount >= 0) {
            this.index -= this.visibleCount;
            this.updateVisible();
            this.animationTimeCarouselDirection = 'slide-right';
            setTimeout(() => {
                this.animationTimeCarouselDirection = ''
            }, 300)
        }
    }

    selectSlot(slot: { label: string, start: Date, end: Date, disabled: boolean }) {
        if (!slot.disabled) {
            this.selectedSlot = slot;
            this.changeDate.emit(this.selectedSlot);
        }
    }

    // checks against currently loaded bookedInformation for the selected day
    isOverlapping(start: Date, end: Date): boolean {
        // prefer bookedInformation for the selected day
        const selDate = new Date(this.activeDate.getFullYear(), this.activeDate.getMonth(), this.selectedDay).toDateString();
        const bookedDay = this.bookedInformation.find(b => new Date(b.dateOfBooked).toDateString() === selDate);

        if (bookedDay && bookedDay.bookedCompanyHours) {
            return bookedDay.bookedCompanyHours.some(bh => {
                const [sh, sm] = bh.startTimeBooked.split(':').map(s => Number(s));
                const [eh, em] = bh.endTimeBooked.split(':').map(s => Number(s));
                const s = new Date(start);
                s.setHours(sh, sm, 0, 0);
                const e = new Date(end);
                e.setHours(eh, em, 0, 0);
                return (start < e && end > s);
            });
        }
        return false
    }

    // --- new helper: request availability for the currently active week ---
    private fetchAvailabilityForActiveWeek() {
        const fromDate = new Date(this.activeDate);
        fromDate.setDate(this.activeDate.getDate() - this.activeDate.getDay() + 1);

        const toDate = new Date(fromDate);
        toDate.setDate(toDate.getDate() + 6); // inclusive 7 days

        const params = {
            companyId: this.companyId,
            fromDate: fromDate,
            toDate: toDate,
        };

        return this.reservationService.getAvailabilityCalendar(params);
    }

    // update day status (green/yellow/red) based on bookedInformation.freeTimePercentage
    private updateDaysStatusFromBookings() {
        for (let d of this.days) {
            const booked = this.bookedInformation.find(b => new Date(b.dateOfBooked).toDateString() === d.date.toDateString());
            if (booked) {
                const pct = booked.freeTimePercentage ?? 0;
                if (pct >= 75) d.status = 'green';
                else if (pct >= 50) d.status = 'yellow';
                else d.status = 'red';
            } else {
                d.status = 'green';
            }
        }
    }

    private getCompanyHoursForDate(date: Date): CompanyHours | undefined {
        const dayName = date.toLocaleDateString('en-US', { weekday: 'long' });
        return this.businessHours.find(b => b.dayOfWeek === dayName);
    }
}
