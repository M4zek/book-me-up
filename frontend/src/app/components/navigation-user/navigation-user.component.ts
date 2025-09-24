import {Component, HostListener} from '@angular/core';
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-navigation-user',
  imports: [
    NgIf
  ],
  templateUrl: './navigation-user.component.html',
  styleUrl: './navigation-user.component.css'
})
export class NavigationUserComponent {
  user_avatar: string = 'icons/user_icon.png';
  user_name: string = 'John Doe';
  drop_down_icon: string = 'icons/drop_down_arrow.png';
  search_icon: string = 'icons/search_icon.svg';
  localization_icon: string = 'icons/localization_icon.svg';
  settings_icon: string = 'icons/settings_icon.svg';
  messages_icon: string = 'icons/message_icon.svg';
  company_icon: string = 'icons/company_icon.svg';
  calendar_icon: string = 'icons/calendar_stroke_icon.svg';
  home_icon: string = 'icons/home_icon.svg';

  menuOpen: boolean = false;

  toggleMenu(): void {
    this.menuOpen = !this.menuOpen;
  }

  @HostListener('document:click', ['$event'])
  clickOutside(event: Event): void {
    const target = event.target as HTMLElement;
    if (!target.closest('.drop-down-icon')) {
      this.menuOpen = false;
    }
  }
}
