import { Injectable } from '@angular/core';
import {BehaviorSubject} from "rxjs";

export interface ToastMessage {
  text: string;
  type?: 'success' | 'error' | 'info' | 'warning';
  id: number;
  closing?: boolean;
  show?: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class ToastService {
  private toastsSubject = new BehaviorSubject<ToastMessage[]>([]);
  toasts$ = this.toastsSubject.asObservable();

  private counter = 0;

  show(text: string, type: 'success' | 'error' | 'info' | 'warning' = 'info') {
    const id = this.counter++;
    const toast: ToastMessage = { text, type, id };

    const current = this.toastsSubject.value;
    this.toastsSubject.next([...current, toast]);

    setTimeout(() => {
      const toasts = this.toastsSubject.value.map(t =>
          t.id === id ? { ...t, show: true } : t
      );
      this.toastsSubject.next(toasts);
    }, 100);

    setTimeout(() => {
      this.startRemove(id);
    }, 5000);
  }

  startRemove(id: number) {
    const toasts = this.toastsSubject.value.map(t =>
        t.id === id ? { ...t, closing: true } : t
    );
    this.toastsSubject.next(toasts);

    setTimeout(() => this.remove(id), 300);
  }

  remove(id: number) {
    this.toastsSubject.next(this.toastsSubject.value.filter(t => t.id !== id));
  }
}
