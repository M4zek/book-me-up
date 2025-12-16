import {Component, OnInit} from '@angular/core';
import {SearchAndSortBarComponent, SortBy} from "../../../components/search-bar/search-and-sort-bar.component";
import {PaginatorComponent} from "../../../components/paginator/paginator.component";
import {
    CompanyManagementAppointmentsItemComponent
} from "../../../components/company-management-appointments-item/company-management-appointments-item.component";
import {DropDownListItem} from "../../../components/drop-down-list/drop-down-list.component";
import {CompanyContextService} from "../../../service/company-context.service";
import {CompanyService} from "../../../service/company.service";
import {EmployeeDropDownItem} from "../../../model/gui/gui.model";
import {NgForOf, NgIf} from "@angular/common";
import {ReservationResponse} from "../../../model/http/reservation.model";
import {CompanyReservationsSearch, Pagination} from "../../../model/search/search.model";
import {ReservationService} from "../../../service/reservation.service";
import {concatMap, throwError} from "rxjs";
import {UserContextService} from "../../../service/user-context.service";
import {DoubleSpinnerComponent} from "../../../components/double-spinner/double-spinner.component";

@Component({
  selector: 'app-company-management-appointments',
    imports: [
        SearchAndSortBarComponent,
        PaginatorComponent,
        CompanyManagementAppointmentsItemComponent,
        NgForOf,
        NgIf,
        DoubleSpinnerComponent
    ],
  templateUrl: './company-management-appointments.component.html',
  styleUrl: './company-management-appointments.component.css'
})
export class CompanyManagementAppointmentsComponent implements OnInit {

  sortByList: DropDownListItem[] = [
      {content: 'Date', image: 'icons/sort_number_asc_icon.svg', option: 'reservationDate,asc'},
      {content: 'Date', image: 'icons/sort_number_desc_icon.svg', option: 'reservationDate,desc'},
      {content: 'Reservation number', image: 'icons/sort_number_asc_icon.svg', option: 'reservationNumber,asc'},
      {content: 'Reservation number', image: 'icons/sort_number_desc_icon.svg', option: 'reservationNumber,desc'},
      {content: 'Status', image: 'icons/sort_number_asc_icon.svg', option: 'status,asc'},
      {content: 'Status', image: 'icons/sort_number_desc_icon.svg', option: 'status,desc'},

  ]

  filterByList: DropDownListItem[] = [
      { content: 'Pending', image:'icons/pending_icon.svg', option: 'pending' },
      { content: 'Completed', image:'icons/realized_icon.svg', option: 'completed' },
      { content: 'Rejected', image:'icons/reject_icon.svg', option: 'rejected' },
      { content: 'Cancelled', image:'icons/canceled_icon.svg', option: 'canceled' },
      { content: 'Accepted', image:'icons/accepted_icon.svg', option: 'accepted' },
  ]

  pagination: Pagination = {
      totalItems: 0,
      itemsPerPage: 5,
      currentPage: 0,
      itemsPerPageOptions: [5, 10, 15,25, 50, 100]
  }

  employeeDropdownList: EmployeeDropDownItem[] = []
  reservationsList: ReservationResponse[] = []
  search: CompanyReservationsSearch = { company_id: 0 }
  isReservationLoading: boolean = false;
  loggedUserId: number = -1;

  constructor(private ctx: CompanyContextService,
              private companyService: CompanyService,
              private reservationService: ReservationService,
              private userContextService: UserContextService) {
  }

  ngOnInit() {
      this.isReservationLoading = true;
      this.userContextService.getUserContext().subscribe(user => {
          if (user) {
              this.loggedUserId = user.id;
          } else {
              this.loggedUserId = -1;
          }
      })

      this.ctx.currentCompany$.subscribe(company => {
          if(company){
              this.search.company_id = company.id;
              this.companyService.getCompanyEmployees(this.search.company_id).pipe(
                  concatMap(response => {
                  if(response.status == 200 && response.body){
                      let tmp_employee_dropdown_item: EmployeeDropDownItem[] = []
                      let tmp_employee: DropDownListItem[] = []

                      for(let employee of response.body){
                          tmp_employee_dropdown_item.push({
                              id: employee.id,
                              firstName: employee.firstName,
                              lastName: employee.lastName,
                              avatar: employee.avatar,
                          })

                          tmp_employee.push({
                              content: employee.firstName + " " + employee.lastName,
                              image: employee.avatar,
                              option: employee.id.toString()
                          })
                      }

                      this.employeeDropdownList = tmp_employee_dropdown_item;
                      this.filterByList = this.filterByList.concat(tmp_employee);
                      return this.reservationService.getCompanyReservations(this.search, this.pagination);
                  }
                  return throwError("Error during loading employees");
              })).subscribe(response => {
                    if(response.status == 200 && response.body){
                        this.reservationsList = response.body.content;
                        this.pagination.totalItems = response.body.page.totalElements;
                        this.pagination.currentPage = response.body.page.number;
                    }
              })
          }
          this.isReservationLoading = false;
      })
  }

  protected onSearchChange($event: string) {
    this.search.name = $event;
    this.loadingReservationsFromServer();
  }

  protected onSortChange($event: SortBy) {
      this.search.sort = $event.sorting;
      this.loadingReservationsFromServer();
  }

  protected onFilterChanged($event: DropDownListItem) {
      if(!isNaN(Number($event.option))){
          this.search.userId = Number($event.option);
          this.search.status = undefined;
      } else {
          this.search.status = $event.option;
          this.search.userId = undefined;
      }
      this.loadingReservationsFromServer();
  }

  protected isEditable(reservation: ReservationResponse): boolean {
    return this.ctx.hasAnyRole("COMPANY_OWNER", "COMPANY_MANAGER") || reservation.preferredEmployee?.id === this.loggedUserId;
  }

  protected onPaginationChanged() {
      this.loadingReservationsFromServer();
  }

  protected loadingReservationsFromServer() {
      this.isReservationLoading = true;
      this.reservationService.getCompanyReservations(this.search, this.pagination).subscribe(response => {
          if(response.status == 200 && response.body){
              this.reservationsList = response.body.content;
              this.pagination.totalItems = response.body.page.totalElements;
              this.pagination.currentPage = response.body.page.number;
          }
          this.isReservationLoading = false;
      })
  }


}
