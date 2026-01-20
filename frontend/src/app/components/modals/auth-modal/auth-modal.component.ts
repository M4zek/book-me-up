import {Component, EventEmitter, Input, Output} from '@angular/core';
import {NgClass, NgIf} from "@angular/common";
import {AuthRequest, UserAccountRequest} from "../../../model/http/auth.model";
import {FormsModule} from "@angular/forms";
import {AuthService} from "../../../service/auth.service";
import {ToastService} from "../../../service/toast.service";
import {Router} from "@angular/router";
import {UserContextService} from "../../../service/user-context.service";
import {concatMap, throwError} from "rxjs";
import {UserService} from "../../../service/user.service";

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


  registerData: UserAccountRequest = {
      addressEmail: '',
      password: '',
      userDataRequest: {
          firstName: '',
          lastName: '',
          dateOfBirth: '',
          phoneNumber: '',
          photo: null
      }
  }
  confirmPassword: string = '';


  @Input() isVisible = false;
  @Output() closeModal = new EventEmitter<void>();

  isCheck: boolean = false;

  passwordFields: { [key: string]: { visible: boolean; icon: string } } = {
    signIn: { visible: false, icon: '/icons/eye_icon.svg' },
    registerPassword: { visible: false, icon: '/icons/eye_icon.svg' },
    registerConfirm: { visible: false, icon: '/icons/eye_icon.svg' }
  };

  birthDateVisible: boolean = false;

  constructor(private router: Router,
              private authService: AuthService,
              private userContextService: UserContextService,
              private userService: UserService,
              private toast: ToastService) {}

  close() {
      this.resetForm();
      this.closeModal.emit();
  }

  togglePasswordVisibility(field: string) {
    const current = this.passwordFields[field];
    current.visible = !current.visible;
    current.icon = current.visible
        ? '/icons/eye_slash_icon.svg'
        : '/icons/eye_icon.svg';
  }

  register(form: any) {
    this.onSubmit(form);
    if(form.invalid) {
        form.markAsTouched();
        return;
    }

    if(this.isUserAdult(this.registerData.userDataRequest.dateOfBirth) && this.registerData.password === this.confirmPassword) {
        this.authService.register(this.registerData).subscribe({
            next: (response) => {
                if(response.status === 200 && response.body) {
                    this.toast.show("Account has been registered successfully.", "success");
                    this.resetForm();
                    form.clearErrors();
                }
            }
            ,error: (err) =>{
                console.error(err);
                let message = err.error.message;
                if(message instanceof Array) {
                    for (const key in message) {
                        let msg: string = message[key];
                        this.toast.show(msg.split(":")[1], 'error');
                    }
                } else {
                    this.toast.show(message, "error");
                }
                console.log(message);
            }
        })
    }
  }

  sign_in(form: any) {

      if(form.invalid) {
          return;
      }

      this.authService.authentication(this.login_model).pipe(
          concatMap(response => {
              if(response.success){
                  this.userContextService.setLoggedUser(response.loggedUser!)

                  return this.userService.readLoggedInUserData();
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
                  return throwError(response.errorMessage);
              }
          })).subscribe(response => {
              if (response.status === 200 && response.body){
                this.userContextService.setLoggedUserData(response.body);
                let currentUrl = this.router.url.replace("guest", "app");
                this.router.navigateByUrl(currentUrl);
                this.toast.show("Logged in successfully", 'success');
              }
      });

  }


  onSubmit(form: any) {
      if (form.invalid) {
          form.control.markAllAsTouched();
          return;
      }
  }

  protected resetForm(){
      this.registerData = {
          addressEmail: '',
          password: '',
          userDataRequest: {
              firstName: '',
              lastName: '',
              dateOfBirth: '',
              phoneNumber: '',
              photo: null
          }
      }
      this.confirmPassword = '';
      this.login_model = {
          email:'',
          password:'',
      }
  }

    protected isUserAdult(date: Date | string): boolean {
        if (!date) {
            return false;
        }

        const birthDate = date instanceof Date ? date : new Date(date);

        if (isNaN(birthDate.getTime())) {
            return false;
        }

        const today = new Date();

        const adultDate = new Date(
            birthDate.getFullYear() + 18,
            birthDate.getMonth(),
            birthDate.getDate()
        );

        return today >= adultDate;
    }


    protected clearRegisterForm(registerForm: any) {
        this.registerData = {
            addressEmail: '',
            password: '',
            userDataRequest: {
                firstName: '',
                lastName: '',
                dateOfBirth: '',
                phoneNumber: '',
                photo: null
            }
        }
        this.confirmPassword = '';
        registerForm.reset();
    }

    protected onLoginSubmit(loginForm: any) {
        if (loginForm.invalid) {
            loginForm.control.markAllAsTouched();
            return;
        }
    }

    protected isPasswordEquals(){
      if(!(this.registerData.password.length > 0) || !(this.confirmPassword.length > 0)){
          return false;
      } else if(this.confirmPassword !== this.registerData.password){
          return false;
      }
      return true;
    }
}
