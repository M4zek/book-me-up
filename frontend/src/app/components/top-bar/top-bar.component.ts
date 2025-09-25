import {Component} from '@angular/core';

@Component({
  selector: 'app-top-bar',
  imports: [],
  templateUrl: './top-bar.component.html',
  styleUrl: './top-bar.component.css'
})
export class TopBarComponent {
  sign_in_or_create_account: string = 'Sign in / Create an account';
  search_icon: string = 'icons/search_icon.svg';
  localization_icon: string = 'icons/localization_icon.svg';
  confirm_icon: string = 'icons/confirm_icon.svg';
  reject_icon: string = 'icons/reject_icon.svg';
}
