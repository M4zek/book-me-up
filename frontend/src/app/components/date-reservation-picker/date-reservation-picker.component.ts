import {Component, EventEmitter, Input, Output} from '@angular/core';
import {NgClass, NgForOf} from "@angular/common";

export interface DateModel{
  day: number,
  weekDay: string,
  status: string,
  date: Date
}

export interface BookedSlots {
  start: Date,
  end: Date,
}

@Component({
  selector: 'app-date-reservation-picker',
  imports: [
    NgClass,
    NgForOf
  ],
  templateUrl: './date-reservation-picker.component.html',
  styleUrl: './date-reservation-picker.component.css'
})
export class DateReservationPickerComponent {
  // DAY PICK VARIABLES
  animationDateCarouselDirection: string = '';
  currentMonth!: string;

  currentYear!: number;
  days: DateModel[] = [];
  selectedDay!: number;
  private weekDays = ['Sun', 'Mon', 'Tu', 'Wed', 'Thu', 'Fri', 'Sat'];

  @Input() closeDays: string[] = ['Saturday', 'Sunday'];
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
  @Input() startHour = 8;
  @Input() endHour = 20;

  @Input() bookedSlots: BookedSlots[] = [];

  ngOnInit() {
    this.activeDate = new Date(this.today);
    this.updateMonthYear();
    this.generateWeek();
    this.selectToday();

    this.generateSlots();
    this.updateVisible();
  }

  private selectToday() {
    const todayModel = this.days.find(d =>
        d.date.toDateString() === this.today.toDateString()
    );

    if (todayModel && !this.isClosed(todayModel.date)) {
      this.selectedDay = todayModel.day;
      this.isToday(todayModel.date);
    }
  }

  updateMonthYear() {
    this.currentYear = this.activeDate.getFullYear();
    this.currentMonth = this.activeDate.toLocaleString('en-US', { month: 'long' });
  }

  generateWeek() {
    this.days = [];
    const startOfWeek = new Date(this.activeDate);

    startOfWeek.setDate(this.activeDate.getDate() - this.activeDate.getDay() + 1);

    for (let i = 0; i < 7; i++) {
      const d = new Date(startOfWeek);
      d.setDate(startOfWeek.getDate() + i);

      this.days.push({
        day: d.getDate(),
        weekDay: this.weekDays[d.getDay()],
        status: this.getRandomStatus(),
        date: d
      });
    }
    this.updateMonthYear();
  }

  // TEST METHOD - GREEN 75% FREE HOUR, YELLOW 50% FREE HOUR, RED - NO FREE HOUR: it will be returned from backend
  getRandomStatus(): string {
    const statuses = ['green', 'yellow', 'red'];
    return statuses[Math.floor(Math.random() * statuses.length)];
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
    setTimeout(() => {
      this.animationDateCarouselDirection = '';
    }, 300);
  }

  nextMonth() {
    this.activeDate.setMonth(this.activeDate.getMonth() + 1);
    this.generateWeek();
  }

  prevMonth() {
    const newDate = new Date(this.activeDate);
    newDate.setMonth(this.activeDate.getMonth() - 1);

    if (newDate < this.today) return;
    this.activeDate = newDate;
    this.generateWeek();
  }

  isDisabled(d: Date): boolean {
    return d < new Date(this.today.getFullYear(), this.today.getMonth(), this.today.getDate());
  }

  isClosed(d: Date): boolean {
    if(this.closeDays){
      const dayOfWeek = d.toLocaleDateString('en-US', {weekday: 'long'})
      for (let day of this.closeDays){
        if(day == dayOfWeek){
          return true;
        }
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

    const base = new Date(`${this.currentMonth} ${this.selectedDay}, ${this.currentYear}`);

    let baseStartHour = this.startHour;
    if (this.isTodayDate){
      baseStartHour = new Date().getHours() + 1;
    }

    base.setHours(baseStartHour, 0, 0, 0);

    const end = new Date(base);
    end.setHours(this.endHour, 0, 0, 0);

    let current = new Date(base);

    while (current < end) {
      const slotStart = new Date(current);
      const slotEnd = new Date(current);
      slotEnd.setMinutes(slotStart.getMinutes() + this.duration);

      if(slotEnd.getHours() == this.endHour && slotEnd.getMinutes() > 0) { return }

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

  isOverlapping(start: Date, end: Date): boolean {
    if(this.bookedSlots){
      return this.bookedSlots.some(b =>
          (start < b.end && end > b.start)
      );
    }
    return false
  }
}
