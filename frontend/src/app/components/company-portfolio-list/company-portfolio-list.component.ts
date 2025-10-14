import {Component, Input} from '@angular/core';
import {NgForOf} from "@angular/common";
import {PortfolioModel} from "../../model/gui/gui.model";

@Component({
  selector: 'app-company-portfolio-list',
  imports: [
    NgForOf
  ],
  templateUrl: './company-portfolio-list.component.html',
  styleUrl: './company-portfolio-list.component.css'
})
export class CompanyPortfolioListComponent {

  @Input() images: PortfolioModel[] = [];

  removeImage(index: number) {
    this.images.splice(index, 1);
  }
}
