import { Component } from '@angular/core';
import {RouterOutlet} from "@angular/router";
import {NavbarComponent} from "./navbar/navbar.component";

@Component({
  selector: 'app-navbar-layout',
    imports: [
        RouterOutlet,
        NavbarComponent
    ],
  templateUrl: './navbar-layout.component.html',
  styleUrl: './navbar-layout.component.css'
})
export class NavbarLayoutComponent {

}
