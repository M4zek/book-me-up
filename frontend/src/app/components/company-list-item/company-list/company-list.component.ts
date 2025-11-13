import {Component, ElementRef, HostListener, Input, ViewChild} from '@angular/core';
import {CompanyListItemComponent} from "../company-list-item.component";
import {NgForOf, NgIf} from "@angular/common";
import {DoubleSpinnerComponent} from "../../double-spinner/double-spinner.component";
import {Router} from "@angular/router";
import {CompanySummaryResponse} from "../../../model/response/company-response.model";
import {UserContextService} from "../../../service/user-context.service";

@Component({
  selector: 'app-company-list',
    imports: [
        NgForOf,
        CompanyListItemComponent,
        DoubleSpinnerComponent,
        NgIf
    ],
  templateUrl: './company-list.component.html',
  styleUrl: './company-list.component.css'
})
export class CompanyListComponent {
    @Input() companies: CompanySummaryResponse[] = [];
    @ViewChild('track') trackRef!: ElementRef<HTMLDivElement>;

    private isDown = false;
    private startX = 0;
    private scrollLeft = 0;
    private moved = false;

    ngOnInit(): void {}

    constructor(private router: Router, private userContextService: UserContextService) {}

    scrollByDirection(direction: 'left' | 'right') {
        const el = this.trackRef.nativeElement;
        const amount = Math.round(el.clientWidth * 0.7);
        el.scrollBy({ left: direction === 'left' ? -amount : amount, behavior: 'smooth' });
    }

    onPointerDown(event: PointerEvent) {
        const el = this.trackRef.nativeElement;
        this.isDown = true;
        this.moved = false;
        el.classList.add('dragging');
        this.startX = event.clientX - el.offsetLeft;
        this.scrollLeft = el.scrollLeft;
        (event.target as Element).setPointerCapture(event.pointerId);
    }


    onPointerMove(event: PointerEvent) {
        if (!this.isDown) return;
        event.preventDefault();
        const el = this.trackRef.nativeElement;
        const x = event.clientX - el.offsetLeft;
        const walk = (x - this.startX);

        if (Math.abs(walk) > 2) this.moved = true;

        el.scrollLeft = this.scrollLeft - walk;
    }


    onPointerUp(event: PointerEvent, company?: CompanySummaryResponse) {
        this.isDown = false;
        const el = this.trackRef.nativeElement;
        el.classList.remove('dragging');
        try { (event.target as Element).releasePointerCapture(event.pointerId); } catch (e) {}

        if (!this.moved && company) {
            this.onItemCLick(company);
        }

    }

    @HostListener('keydown', ['$event'])
    onKeydown(event: KeyboardEvent) {
        if (event.key === 'ArrowLeft') { this.scrollByDirection('left'); event.preventDefault(); }
        if (event.key === 'ArrowRight') { this.scrollByDirection('right'); event.preventDefault(); }
    }


    onItemCLick(company: CompanySummaryResponse) {
        this.userContextService.isLoggedIn().subscribe(isLoggedIn => {
            if (isLoggedIn) {
                this.router.navigate(['app/company', company.id])
                    .then(r => console.log("Redirect to APP/company: ",r));
            } else {
                this.router.navigate(['guest/company', company.id])
                    .then(r => console.log("Redirect to GUEST/company/: ",r));
            }
        })
    }


}
