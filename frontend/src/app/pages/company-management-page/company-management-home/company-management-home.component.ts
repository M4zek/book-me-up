import {Component} from '@angular/core';
import {CompanyOpinionsComponent} from "../../../components/company-opinions/company-opinions.component";
import {
  CompanyPortfolioListComponent
} from "../../../components/company-portfolio-list/company-portfolio-list.component";
import {MapComponent} from "../../../components/map/map.component";
import {
  CompanyBusinessHoursComponent
} from "../../../components/company-business-hours/company-business-hours.component";

@Component({
  selector: 'app-company-management-home',
  imports: [
    CompanyOpinionsComponent,
    CompanyPortfolioListComponent,
    MapComponent,
    CompanyBusinessHoursComponent
  ],
  templateUrl: './company-management-home.component.html',
  styleUrl: './company-management-home.component.css'
})
export class CompanyManagementHomeComponent {
  description: string =
      'Lorem Ipsum is simply dummy text of the printing and typesetting industry. ' +
      'Lorem Ipsum has been the industry\'s standard dummy text ever since the 1500s, ' +
      'when an unknown printer took a galley of type and scrambled it to make a type specimen book. ' +
      'It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. ' +
      'It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, ' +
      'and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.' +
      'Lorem Ipsum is simply dummy text of the printing and typesetting industry. ' +
      'Lorem Ipsum has been the industry\'s standard dummy text ever since the 1500s, ' +
      'when an unknown printer took a galley of type and scrambled it to make a type specimen book. ' +
      'It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. ' +
      'It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, ' +
      'and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.' +
      'Lorem Ipsum is simply dummy text of the printing and typesetting industry. ' +
      'Lorem Ipsum has been the industry\'s standard dummy text ever since the 1500s, ' +
      'when an unknown printer took a galley of type and scrambled it to make a type specimen book. ' +
      'It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. ' +
      'It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, ' +
      'and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.' +
      'Lorem Ipsum is simply dummy text of the printing and typesetting industry. ' +
      'Lorem Ipsum has been the industry\'s standard dummy text ever since the 1500s, ' +
      'when an unknown printer took a galley of type and scrambled it to make a type specimen book. ' +
      'It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. ' +
      'It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, ' +
      'and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.';
  companyLogo: string = 'images/default_logo_company.png';
  companyName: string = 'Best barber';

  editCompanyLogoOrName() {

  }

  editDescription() {

  }

  editAddress() {

  }

  editHours() {

  }
}
