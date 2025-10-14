import {Component} from '@angular/core';
import {CompanyOpinionsComponent} from "../../../components/company-opinions/company-opinions.component";
import {
  CompanyPortfolioListComponent
} from "../../../components/company-portfolio-list/company-portfolio-list.component";
import {MapComponent} from "../../../components/map/map.component";
import {
  CompanyBusinessHoursComponent
} from "../../../components/company-business-hours/company-business-hours.component";
import {
  CompanyDescriptionEditModalComponent
} from "../../../components/modals/company-description-edit-modal/company-description-edit-modal.component";
import {
  CompanyAddressEditModalComponent
} from "../../../components/modals/company-address-edit-modal/company-address-edit-modal.component";
import {Address, CompanyNameAndLogo, PortfolioModel} from "../../../model/gui/gui.model";
import {
  CompanyNameLogoEditModalComponent
} from "../../../components/modals/company-name-logo-edit-modal/company-name-logo-edit-modal.component";
import {
  CompanyBusinessHourEditModalComponent
} from "../../../components/modals/company-bussines-hour-edit-modal/company-business-hour-edit-modal.component";
import {
  CompanyPortfolioAddModalComponent
} from "../../../components/modals/company-portfolio-add-modal/company-portfolio-add-modal.component";

@Component({
  selector: 'app-company-management-home',
  imports: [
    CompanyOpinionsComponent,
    CompanyPortfolioListComponent,
    MapComponent,
    CompanyBusinessHoursComponent,
    CompanyDescriptionEditModalComponent,
    CompanyAddressEditModalComponent,
    CompanyNameLogoEditModalComponent,
    CompanyBusinessHourEditModalComponent,
    CompanyPortfolioAddModalComponent
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


  portfolioList: PortfolioModel[] = [
    {
      id: 0,
      name: 'image_1',
      photo: 'images/default_logo_company.png'
    }
  ];

  companyNameLogo: CompanyNameAndLogo = {
    logo: 'images/default_logo_company.png',
    logoName: 'logo.png',
    companyName: 'Best barber'
  }

  companyAddress: Address = {
    city: 'Warszawa',
    postalCode: '00-901',
    street: 'Defilad',
    buildingNumber: '1'
  };

  openEditDescriptionModal: boolean = false;
  openEditAddressModal: boolean = false;
  openEditHoursModal: boolean = false;
  openEditLogoNameModal: boolean = false;
  openAddPortfolioModal: boolean = false;


  openEditModal(modalName: string) {
    switch (modalName) {
      case 'editDescriptionModal':
        this.openEditDescriptionModal = true;
        break;
      case 'editAddressModal':
        this.openEditAddressModal = true;
        break;
      case 'editHoursModal':
        this.openEditHoursModal = true;
        break;
      case 'editLogoNameModal':
        this.openEditLogoNameModal = true;
        break;
       case 'addPortfolioModal':
         this.openAddPortfolioModal = true;
         break;
    }
  }


  closeEditModal(modalName: string) {
    switch (modalName) {
      case 'editDescriptionModal':
        this.openEditDescriptionModal = false;
        break;
      case 'editAddressModal':
        this.openEditAddressModal = false;
        break;
      case 'editHoursModal':
        this.openEditHoursModal = false;
        break;
      case 'editLogoNameModal':
        this.openEditLogoNameModal = false;
        break;
      case 'addPortfolioModal':
        this.openAddPortfolioModal = false;
        break;
    }
  }

}
