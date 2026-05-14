import { Component } from '@angular/core';
import {Router} from "@angular/router";

@Component({
  selector: 'app-server-error-page',
  imports: [],
  templateUrl: './server-error-page.component.html',
  styleUrl: './server-error-page.component.css'
})
export class ServerErrorPageComponent {


    constructor(private router: Router) {
    }

    protected goToHomePage() {
        this.router.navigate(['/app']);
    }
}
