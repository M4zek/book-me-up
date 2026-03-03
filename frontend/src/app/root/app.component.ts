import {Component} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {ToastComponent} from "../components/toast/toast.component";
import {WebsocketService} from "../service/websocket.service";

@Component({
  selector: 'app-root',
    imports: [RouterOutlet, ToastComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'Book Me Up';

  constructor(socket: WebsocketService) {}

}
