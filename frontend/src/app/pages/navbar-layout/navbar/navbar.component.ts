import {Component} from '@angular/core';
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {NavigationEnd, Router, RouterLink} from "@angular/router";
import {UserContextService} from "../../../service/user-context.service";
import {Role} from "../../../model/http/auth.model";
import {AuthModalComponent} from "../../../components/modals/auth-modal/auth-modal.component";
import {RoleCheckerDirective} from "../../../role-checker.directive";
import {MyImgComponent} from "../../../components/my-img/my-img.component";
import {FileType} from "../../../model/http/company.model";
import {WebsocketService} from "../../../service/websocket.service";
import {NotificationType, RoomType} from "../../../model/http/chat.model";
import {ToastService} from "../../../service/toast.service";


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
            name: 'Home', icon: 'icons/home_icon.svg', path: '/app/home', role: [Role.ROLE_USER]
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

    messageReceivedCount: number = 0;

    constructor(public router: Router,
                private userContextService: UserContextService,
                private toastService: ToastService,
                private webSocket: WebsocketService) {
        this.userContextService.authState().subscribe(state => {
            this.user.isLoggedIn = state.isLoggedIn;
            this.user.firstName = state.userData?.firstName;
            this.user.lastName = state.userData?.lastName;
            this.user.avatar = state.userData?.avatar_url;
        })

        this.router.events.subscribe(event => {
            if(event instanceof NavigationEnd && event.url.includes('/app/messages')){
                this.messageReceivedCount = 0;
            }
        })

        this.webSocket.notificationSubject$.subscribe(notification => {
            switch (notification?.type){
                case NotificationType.CHAT:
                    let msg = 'New chat message';

                    // Show info message arrive if user is not on
                    // the message page and increase message number
                    // Otherwise reset received message count
                    if(!this.router.url.includes("/app/messages")){
                        msg = notification.room.roomType == RoomType.PRIVATE ?
                            `New message <strong>${notification.room.lastMessage.sender.firstName}</strong>` :
                            `New message in <strong>${notification.room.name}</strong> from 
                            <strong>${notification.room.lastMessage.sender.firstName}</strong>`;
                        this.toastService.show(msg, "info");
                        this.messageReceivedCount++;
                    } else {
                        this.messageReceivedCount = 0;
                    }
                    break;

                default:
                    break;
            }
        })
    }

    protected logout() {
        this.userContextService.deleteUserFromStorage();
    }

    protected toggleAuthModalVisible(){
        this.authModalVisible = !this.authModalVisible;
    }

}
