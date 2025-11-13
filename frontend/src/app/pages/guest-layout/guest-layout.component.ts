import {Component} from '@angular/core';
import {RouterOutlet} from "@angular/router";
import {TopBarComponent} from "./top-bar/top-bar.component";

@Component({
  selector: 'app-guest-layout',
    imports: [
        RouterOutlet,
        TopBarComponent
    ],
  templateUrl: './guest-layout.component.html',
  styleUrl: './guest-layout.component.css'
})
export class GuestLayoutComponent {

}
