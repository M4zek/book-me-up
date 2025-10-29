import {Component, EventEmitter, Input, Output} from '@angular/core';
import {NgClass, NgIf} from "@angular/common";

@Component({
  selector: 'app-auth-modal',
  imports: [
    NgIf,
    NgClass
  ],
  templateUrl: './auth-modal.component.html',
  styleUrl: './auth-modal.component.css'
})
export class AuthModalComponent {
  email_icon: string = '/icons/envelope_icon.svg'
  lock_icon: string = '/icons/lock_icon.svg'
  user_icon: string = '/icons/user_icon.svg'
  calendar_icon: string = '/icons/calendar_stroke_icon.svg'
  phone_icon: string = '/icons/phone_icon.svg'

  @Input() isVisible = false;
  @Output() closeModal = new EventEmitter<void>();

  isCheck: boolean = false;

  passwordFields: { [key: string]: { visible: boolean; icon: string } } = {
    signIn: { visible: false, icon: '/icons/eye_icon.svg' },
    registerPassword: { visible: false, icon: '/icons/eye_icon.svg' },
    registerConfirm: { visible: false, icon: '/icons/eye_icon.svg' }
  };

  close() {
    this.closeModal.emit();
  }

  togglePasswordVisibility(field: string) {
    const current = this.passwordFields[field];
    current.visible = !current.visible;
    current.icon = current.visible
        ? '/icons/eye_slash_icon.svg'
        : '/icons/eye_icon.svg';
  }

  register() {

  }

  sign_in() {

  }
}
