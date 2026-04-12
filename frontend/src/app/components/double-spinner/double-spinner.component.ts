import {Component, Input} from '@angular/core';
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-double-spinner',
    imports: [
        NgIf
    ],
  templateUrl: './double-spinner.component.html',
  styleUrl: './double-spinner.component.css'
})
export class DoubleSpinnerComponent {
    @Input() text: string =''
}
