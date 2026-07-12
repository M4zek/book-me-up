import {Component} from '@angular/core';
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {Router, RouterLink} from "@angular/router";
import {UserContextService} from "../../../service/user-context.service";
import {Role} from "../../../model/http/auth.model";
import {AuthModalComponent} from "../../../components/modals/auth-modal/auth-modal.component";
import {RoleCheckerDirective} from "../../../role-checker.directive";
import {MyImgComponent} from "../../../components/my-img/my-img.component";
import {FileType} from "../../../model/http/company.model";


export interface NAVIGATION{
    name: string;
    icon?: string;
    path: string;
    role?: Role[];
}

export interface UserProfile{
    isLoggedIn: boolean,
    firstName?: string,
    lastName?: string,
    avatar?: string | null,
}

@Component({
  selector: 'app-navbar',
    imports: [
        NgForOf,
        RouterLink,
        NgClass,
        NgIf,
        AuthModalComponent,
        RoleCheckerDirective,
        MyImgComponent
    ],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {

    protected readonly FileType = FileType;
    authModalVisible = false;

    NAV: NAVIGATION [] = [
        {
            name: 'Home', icon: 'icons/home_icon.svg', path: '/app/home/welcome', role: [Role.ROLE_USER]
        }, {
            name: 'Companies', icon: 'icons/company_icon.svg', path: '/app/company-management', role: [Role.ROLE_USER]
        }, {
            name: 'Appointments', icon: 'icons/calendar_stroke_icon.svg', path: '/app/appointments', role: [Role.ROLE_USER]
        }, {
            name: 'Chat', icon: 'icons/message_icon.svg', path: '/app/messages', role: [Role.ROLE_USER]
        }, {
            name: 'Admin', icon: 'icons/user_admin_icon.svg', path: '/admin/panel', role: [Role.ROLE_ADMIN]
        }, {
            name: 'Settings', icon: 'icons/settings_icon.svg', path: '/app/settings', role: [Role.ROLE_USER]
        }
    ]

    protected user: UserProfile = {
        isLoggedIn: false,
    }

    constructor(public router: Router, private userContextService: UserContextService) {
        this.userContextService.authState().subscribe(state => {
            this.user.isLoggedIn = state.isLoggedIn;
            this.user.firstName = state.userData?.firstName;
            this.user.lastName = state.userData?.lastName;
            this.user.avatar = state.userData?.avatar_url;
        })
    }

    protected logout() {
        this.userContextService.deleteUserFromStorage();
    }

    protected toggleAuthModalVisible(){
        this.authModalVisible = !this.authModalVisible;
    }

}
