import {Component, EventEmitter, Input, Output} from '@angular/core';
import {NgClass, NgIf} from "@angular/common";
import {AuthRequest} from "../../../model/auth/auth.model";
import {FormsModule} from "@angular/forms";
import {HttpClient} from "@angular/common/http";
import {AuthService} from "../../../service/auth.service";
import {concatMap, throwError} from "rxjs";
import {ToastService} from "../../../service/toast.service";
import {Router} from "@angular/router";
import {UserContextService} from "../../../service/user-context.service";

@Component({
  selector: 'app-auth-modal',
    imports: [
        NgIf,
        NgClass,
        FormsModule
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

    login_model: AuthRequest = {
        email:'',
        password:'',
    }

  @Input() isVisible = false;
  @Output() closeModal = new EventEmitter<void>();

  isCheck: boolean = false;

  passwordFields: { [key: string]: { visible: boolean; icon: string } } = {
    signIn: { visible: false, icon: '/icons/eye_icon.svg' },
    registerPassword: { visible: false, icon: '/icons/eye_icon.svg' },
    registerConfirm: { visible: false, icon: '/icons/eye_icon.svg' }
  };

  constructor(private router: Router,
              private authService: AuthService,
              private userContextService: UserContextService,
              private toast: ToastService) {}

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
      // TODO Validate data

    this.authService.authentication(this.login_model).subscribe(response => {
        if (response.success) {
            this.userContextService.setLoggedUser(response.loggedUser!)
            this.router.navigate(['/', 'app']);
            this.toast.show("Logged in successfully", 'success');
        } else {
            if(response.errorMessage) {
                const messages = Array.isArray(response.errorMessage.message)
                    ? response.errorMessage.message
                    : [response.errorMessage.message];

                messages.forEach(msg => {
                    const [, text] = msg.split(':');
                    this.toast.show(text?.trim() || msg, 'warning');
                });
            }
        }
    });

  }
}
