import {Component, EventEmitter, Input, Output} from '@angular/core';
import {NgForOf} from "@angular/common";
import {Router, RouterLink} from "@angular/router";


export interface AdminPanelNavigation{
    path: string,
    icon: string,
    name: string,
}


@Component({
  selector: 'app-admin-panel-navigation',
    imports: [
        NgForOf,
        RouterLink
    ],
  templateUrl: './admin-panel-navigation.component.html',
  styleUrl: './admin-panel-navigation.component.css'
})
export class AdminPanelNavigationComponent {

    @Input() collapsed = false;
    @Output() toggle = new EventEmitter<void>();

    NAVIGATION: AdminPanelNavigation[] = [
        {
            path:'/admin/panel/dashboard',
            icon:'icons/home_icon.svg',
            name:'Dashboard'
        },
        {
            path:'/admin/panel/users',
            icon:'icons/persons_icon.svg',
            name:'Users'
        },
        {
            path:'/admin/panel/companies',
            icon:'icons/company_icon.svg',
            name:'Companies'
        },
        {
            path:'/admin/panel/categories',
            icon:'icons/clipboard_icon.svg',
            name:'Categories'
        },
        {
            path:'/admin/panel/tickets',
            icon:'icons/ticket_icon.svg',
            name:'Tickets / Reports'
        },
    ]

    constructor(protected router: Router) {}

}
