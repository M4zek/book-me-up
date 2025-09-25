import {Component} from '@angular/core';

@Component({
  selector: 'app-company-list-item',
  imports: [],
  templateUrl: './company-list-item.component.html',
  styleUrl: './company-list-item.component.css'
})
export class CompanyListItemComponent {
  company_logo_url: string = '/images/default_logo_company.png'
  star_icon: string = '/icons/star_icon.svg'
  company_name: string = 'Company Name';
  company_address: string = 'Company Address';
  rating: number = 0.0;
  number_of_opinions: number = 0;
}
