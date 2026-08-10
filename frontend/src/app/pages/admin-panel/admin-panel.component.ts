import { Component } from '@angular/core';
import {AdminPanelNavigationComponent} from "./admin-panel-navigation/admin-panel-navigation.component";
import {RouterOutlet} from "@angular/router";

@Component({
  selector: 'app-admin-panel',
    imports: [
        AdminPanelNavigationComponent,
        RouterOutlet
    ],
  templateUrl: './admin-panel.component.html',
  styleUrl: './admin-panel.component.css'
})
export class AdminPanelComponent {

    isCollapsed = true;

    toggleSidebar() {
        this.isCollapsed = !this.isCollapsed;
    }
}
