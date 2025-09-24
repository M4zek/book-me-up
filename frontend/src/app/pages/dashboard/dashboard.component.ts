import {Component} from '@angular/core';
import {NavigationUserComponent} from "../../components/navigation-user/navigation-user.component";
import {RouterOutlet} from "@angular/router";

@Component({
  selector: 'app-dashboard',
  imports: [
    NavigationUserComponent,
    RouterOutlet
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent {

}
