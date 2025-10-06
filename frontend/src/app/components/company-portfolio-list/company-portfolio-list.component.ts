import { Component } from '@angular/core';
import {NgForOf} from "@angular/common";

@Component({
  selector: 'app-company-portfolio-list',
  imports: [
    NgForOf
  ],
  templateUrl: './company-portfolio-list.component.html',
  styleUrl: './company-portfolio-list.component.css'
})
export class CompanyPortfolioListComponent {

  images: string[] = [
    'images/default_logo_company.png',
    'images/default_logo_company.png',
    'images/default_logo_company.png',
    'images/default_logo_company.png',
    'images/default_logo_company.png',
    'images/default_logo_company.png',
  ];

  removeImage(index: number) {
    this.images.splice(index, 1);
  }

  addImage() {

  }
}
