import {ApplicationRef, ComponentRef, createComponent, Injectable} from '@angular/core';
import {ConfirmModalComponent} from "../components/modals/confirm-modal/confirm-modal.component";

@Injectable({
  providedIn: 'root'
})
export class ConfirmService {

    constructor(private appRef: ApplicationRef) {}

    open(message: string): Promise<boolean> {
        return new Promise<boolean>(resolve => {

            const componentRef = createComponent(ConfirmModalComponent, {
                environmentInjector: this.appRef.injector
            });

            componentRef.instance.message = message;

            componentRef.instance.result.subscribe((value: boolean) => {
                resolve(value);
                this.close(componentRef);
            });

            this.appRef.attachView(componentRef.hostView);
            document.body.appendChild(componentRef.location.nativeElement);
        });
    }

    private close(componentRef: ComponentRef<any>) {
        this.appRef.detachView(componentRef.hostView);
        componentRef.destroy();
    }
}
