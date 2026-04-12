import {Component, EventEmitter, Input, Output} from '@angular/core';
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {AvailableSlot, DayAvailability} from "../../model/http/reservation.model";
import {DoubleSpinnerComponent} from "../double-spinner/double-spinner.component";


type DatePart = 'dayName' | 'dayNumber' | 'monthName' | 'year' | 'full';

export enum ChangeDateEventType {
    SET_SLOT = 'SET_SLOT',
    SET_DAY = 'SET_DAY',
    NEXT_WEEK = 'NEXT_WEEK', PREV_WEEK = 'PREV_WEEK',
    NEXT_MONTH = 'NEXT_MONTH', PREV_MONTH = 'PREV_MONTH'
}

export type ChangeDateEvent =
    | { type: ChangeDateEventType.SET_SLOT; payload: { slot: AvailableSlot } }
    | { type: ChangeDateEventType.SET_DAY; payload: { date: Date } }
    | { type: ChangeDateEventType.NEXT_WEEK }
    | { type: ChangeDateEventType.PREV_WEEK }
    | { type: ChangeDateEventType.NEXT_MONTH }
    | { type: ChangeDateEventType.PREV_MONTH }

@Component({
    selector: 'app-date-reservation-picker',
    imports: [
        NgClass,
        NgIf,
        NgForOf,
        DoubleSpinnerComponent
    ],
    templateUrl: './date-reservation-picker.component.html',
    styleUrl: './date-reservation-picker.component.css'
})
export class DateReservationPickerComponent {
    animationDateCarouselDirection: string = '';
    animationTimeCarouselDirection: string = '';

    @Output() changeDate: EventEmitter<ChangeDateEvent> = new EventEmitter();
    @Input() days: DayAvailability[] = [];

    @Input() isSlotsLoadingFromServer = false;

    selectedDay: DayAvailability | null = null;
    selectedSlot: AvailableSlot | null = null;

    availableSlots: AvailableSlot[] | null = [];
    availableStartIndex: number = 0;
    availableVisibleMax: number = 5;

    constructor() {}

    protected getDatePart(dateInput: Date | string, part: DatePart): string | number {
        const date = new Date(dateInput);

        switch (part) {
            case 'dayName':
                return date.toLocaleDateString('en-EN', { weekday: 'long' });

            case 'dayNumber':
                return date.getDate();

            case 'monthName':
                return date.toLocaleDateString('en-EN', { month: 'long' });

            case 'year':
                return date.toLocaleDateString('en-EN', {year: 'numeric' });

            case 'full':
                return date.toLocaleDateString('en-EN', {
                    weekday: 'long',
                    day: 'numeric',
                    month: 'long'
                });

            default:
                return '';
        }
    }



    protected getStatus(percentage: number | null | undefined) {
        if (!percentage) { return "red"}

        if (percentage >= 75) return  'green';
        else if (percentage >= 50) return 'yellow';
        else return  'red';
    }

    protected selectDay(day: DayAvailability) {
        if(this.selectedDay !== day){
            this.selectedDay = day;
            this.selectedSlot = null;
            this.availableSlots = this.selectedDay.slots;
            this.changeDate.emit({
                type: ChangeDateEventType.SET_DAY,
                payload: this.selectedDay
            });
        }
    }


    // Slots time
    selectSlot(slot: AvailableSlot){
        if(slot !== this.selectedSlot){
            this.selectedSlot = slot;

            this.changeDate.emit({
                type: ChangeDateEventType.SET_SLOT,
                payload: {slot: slot}
            })

        }
    }

    get visibleSlots(): AvailableSlot[] {
        if(this.availableSlots == null){ return []}

        return this.availableSlots.slice(
            this.availableStartIndex,
            this.availableStartIndex + this.availableVisibleMax
        );
    }

    nextSlots() {
        if(this.availableSlots == null){ return }
        if (this.availableStartIndex + this.availableVisibleMax < this.availableSlots.length) {
            this.availableStartIndex += this.availableVisibleMax;
            this.animationTimeCarouselDirection = 'slide-left';
            setTimeout(() => {
                this.animationTimeCarouselDirection = ''
            }, 300)
        }
    }

    prevSlots() {
        if (this.availableStartIndex - this.availableVisibleMax >= 0) {
            this.availableStartIndex -= this.availableVisibleMax;
            this.animationTimeCarouselDirection = 'slide-right';
            setTimeout(() => {
                this.animationTimeCarouselDirection = ''
            }, 300)
        }
    }


    protected getMonthNameAndYearFromFirstDay(days: DayAvailability[]) {
        if(days !== null && days !== undefined) {
            let firstDay: DayAvailability = days[0];
            return `${this.getDatePart(firstDay.date, 'monthName')} (${this.getDatePart(firstDay.date, 'year')})`;
        }
        return '';
    }

    protected isDayBeforeToday(givenDay: DayAvailability) {
        let today: Date = new Date();
        today.setHours(0, 0, 0, 0);

        let day = new Date(givenDay.date);
        day.setHours(0, 0, 0, 0);
        return day < today;
    }

    protected isCurrentMonth(date: Date): boolean {
        date = new Date(date);
        const today = new Date();

        return (
            date.getMonth() === today.getMonth() &&
            date.getFullYear() === today.getFullYear()
        );
    }

    // Gui button handling
    protected nextMonth() {
        this.changeDate.emit({
            type: ChangeDateEventType.NEXT_MONTH,
        })
    }

    protected prevMonth() {
        if(this.isCurrentMonth(new Date(this.days[0].date))){
            return;
        }
        this.changeDate.emit({
            type: ChangeDateEventType.PREV_MONTH,
        })
    }

    protected prevWeek() {
        this.changeDate.emit({
            type: ChangeDateEventType.PREV_WEEK,
        })
    }

    protected nextWeek() {
        this.changeDate.emit({
            type: ChangeDateEventType.NEXT_WEEK,
        })
    }
}
