import {Component} from '@angular/core';
import {AuthModalComponent} from "../modals/auth-modal/auth-modal.component";

@Component({
  selector: 'app-top-bar',
  imports: [
    AuthModalComponent
  ],
  templateUrl: './top-bar.component.html',
  styleUrl: './top-bar.component.css'
})
export class TopBarComponent {
  sign_in_or_create_account: string = 'Sign in / Create an account';


  isModalVisible = false;

  isActive = false;

  toggleSearch() {
    this.isActive = !this.isActive;
  }

  showAuthModal() {
    this.isModalVisible = true;
    console.log("Opening Modal");
  }

  hideAuthModal() {
    this.isModalVisible = false;
    console.log("Closed Modal");
  }
}
