import {Component, HostListener, OnInit} from '@angular/core';
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {Router, RouterLink} from "@angular/router";
import {UserContextService} from "../../../service/user-context.service";


export interface MainNavigation{
  name: string;
  icon?: string;
  path: string;
}

@Component({
  selector: 'app-navigation-user',
  imports: [
    NgIf,
    NgClass,
    NgForOf,
    RouterLink
  ],
  templateUrl: './navigation-user.component.html',
  styleUrl: './navigation-user.component.css'
})
export class NavigationUserComponent implements OnInit {
  user_avatar: string = 'images/user_default_avatar.png';
  user_name: string = 'John Doe';
  drop_down_icon: string = 'icons/drop_down_arrow.png';
  confirm_icon: string = 'icons/confirm_icon.svg';
  reject_icon: string = 'icons/reject_icon.svg';

  menuOpen: boolean = false;
  isActive = false;

  constructor(public router: Router, private userContextService: UserContextService) {}

    ngOnInit(): void {
        this.userContextService.getUserData().subscribe(user => {
            this.user_name = `${user.firstName} ${user.lastName}`;
            if (user.avatar) {
                this.user_avatar = `data:image/jpeg;base64,${user.avatar}`
            }
        })
    }


  BAR_NAVIGATION: MainNavigation[] = [
    {
      name: 'Home',
      icon: 'icons/home_icon.svg',
      path: '/app/home/welcome',
    }
  ]

  NAVIGATION_USER: MainNavigation[] = [
      {
        name: 'My appointments',
        icon: 'icons/calendar_stroke_icon.svg',
        path: '/app/appointments',
      },
      {
        name: 'My companies',
        icon: 'icons/company_icon.svg',
        path: '/app/company-management',
      },
      {
        name: 'Chat',
        icon: 'icons/message_icon.svg',
        path: '/app/messages',
      },
      {
        name: 'Settings',
        icon: 'icons/settings_icon.svg',
        path: '/app/settings',
      },
      {
        name: 'Logout',
        icon: 'icons/logout_icon.svg',
        path: '/guest',
      }
  ]

  toggleSearch() {
    this.isActive = !this.isActive;
  }

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

  protected logout(): void {
      this.userContextService.deleteUserFromStorage();
  }
}
