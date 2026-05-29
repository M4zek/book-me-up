import {Component} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {ToastComponent} from "../components/toast/toast.component";
import {WebsocketService} from "../service/websocket.service";
import {ProjectInfoModalComponent} from "../components/modals/project-info-modal/project-info-modal.component";

@Component({
  selector: 'app-root',
    imports: [RouterOutlet, ToastComponent, ProjectInfoModalComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'Book Me Up';

  constructor(socket: WebsocketService) {}

}
