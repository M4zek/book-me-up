import {Component, OnInit} from '@angular/core';
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-project-info-modal',
    imports: [
        NgIf
    ],
  templateUrl: './project-info-modal.component.html',
  styleUrl: './project-info-modal.component.css'
})
export class ProjectInfoModalComponent implements OnInit {

    private HAS_VISITED_KEY: string =  "hasVisited"

    isVisible: boolean = false;

    ngOnInit() {
        const hasVisitedStorage = localStorage.getItem(this.HAS_VISITED_KEY);

        if (!Boolean(hasVisitedStorage)) {
            this.isVisible = true;
        }
    }

    close() {
        this.isVisible = false;
        localStorage.setItem(this.HAS_VISITED_KEY, "true");
    }

}
