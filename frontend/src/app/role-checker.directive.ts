import {Directive, ElementRef, Input, OnInit, TemplateRef, ViewContainerRef} from '@angular/core';
import {Role} from "./model/http/auth.model";
import {distinctUntilChanged, map, Subject, takeUntil} from "rxjs";
import {UserContextService} from "./service/user-context.service";
import {arraysEqual} from "./utils.functions";

@Directive({
  selector: '[appRoleChecker]',
    standalone: true,
})
export class RoleCheckerDirective implements OnInit {

    @Input()
    public appRoleChecker: Array<Role> | undefined;

    private ngDestroy$ = new Subject<void>();

    constructor(private elementRef: ElementRef,
                private templateRef: TemplateRef<any>,
                private viewContainer: ViewContainerRef,
                private userContextService: UserContextService) { }


    ngOnInit() {
        this.userContextService.getUserContext()
            .pipe(
                takeUntil(this.ngDestroy$),
                map(userContext => userContext.roles),
                distinctUntilChanged((first, second) => arraysEqual(first, second)),
            )
            .subscribe((userRoles: Role[]) => {
                this.elementRef.nativeElement.disabled = true;
                this.viewContainer.clear();

                if (!this.appRoleChecker) {
                    this.viewContainer.createEmbeddedView(this.templateRef);
                } else {
                    let hasPermission = false;
                    for(const item of this.appRoleChecker){
                        if(userRoles.includes(item)){
                            hasPermission = true;
                        }
                    }
                    if (hasPermission) {
                        this.viewContainer.createEmbeddedView(this.templateRef);
                    }
                }
            });
    }
}
