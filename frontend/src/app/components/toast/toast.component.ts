import { Component } from '@angular/core';
import {AsyncPipe, NgClass, NgForOf} from "@angular/common";
import {ToastService} from "../../service/toast.service";

@Component({
  selector: 'app-toast',
  imports: [
    NgForOf,
    AsyncPipe,
    NgClass
  ],
  templateUrl: './toast.component.html',
  styleUrl: './toast.component.css'
})
export class ToastComponent {
  constructor(public toastService: ToastService) {}


  getIcon(type: 'success' | 'error' | 'info' | 'warning' = 'info'): string {
    switch(type) {
      case 'success': return 'toast/success_toast_icon.svg';
      case 'error': return 'toast/error_toast_icon.svg';
      case 'info': return 'toast/info_toast_icon.svg';
      case 'warning': return 'toast/warn_toast_icon.svg';
      default: return '';
    }
  }

  trackById(index: number, item: any) {
    return item.id;
  }
}
