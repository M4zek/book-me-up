import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'app-confirm-modal',
    imports: [],
  templateUrl: './confirm-modal.component.html',
  styleUrl: './confirm-modal.component.css'
})
export class ConfirmModalComponent {
    @Input() message: string = 'Are you sure?';
    @Output() result = new EventEmitter<boolean>();

    confirm() {
        this.result.emit(true);
    }

    cancel() {
        this.result.emit(false);
    }
}
