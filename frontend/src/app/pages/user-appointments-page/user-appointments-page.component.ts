import {Component, OnInit} from '@angular/core';
import {SearchAndSortBarComponent, SortBy} from "../../components/search-bar/search-and-sort-bar.component";
import {PaginatorComponent} from "../../components/paginator/paginator.component";
import {DatePipe, NgClass, NgForOf, NgIf} from "@angular/common";
import {DropDownListItem} from "../../components/drop-down-list/drop-down-list.component";
import {
    AddOpinionModalComponent,
    OpinionModalInput
} from "../../components/modals/add-opinion-modal/add-opinion-modal.component";
import {ReservationService} from "../../service/reservation.service";
import {Pagination, UserReservationSearch} from "../../model/search/search.model";
import {UserContextService} from "../../service/user-context.service";
import {concatMap} from "rxjs";
import {DoubleSpinnerComponent} from "../../components/double-spinner/double-spinner.component";
import {UserReservationResponse} from "../../model/http/reservation.model";
import {RouterLink} from "@angular/router";


export interface Status{
  status: string;
  image: string;
}

@Component({
  selector: 'app-user-appointments-page',
    imports: [
        SearchAndSortBarComponent,
        PaginatorComponent,
        NgForOf,
        NgClass,
        NgIf,
        AddOpinionModalComponent,
        DatePipe,
        DoubleSpinnerComponent,
        RouterLink
    ],
  templateUrl: './user-appointments-page.component.html',
  styleUrl: './user-appointments-page.component.css'
})
export class UserAppointmentsPageComponent implements OnInit {

  statusList:Status [] = [
      {status: 'PENDING', image: 'icons/pending_icon.svg'},
      {status: 'ACCEPTED', image: 'icons/accepted_icon.svg'},
      {status: 'COMPLETED', image: 'icons/realized_icon.svg'},
      {status: 'CANCELLED', image: 'icons/canceled_icon.svg'},
      {status: 'REJECTED', image: 'icons/reject_icon.svg'},
  ]

  filterByItems: DropDownListItem[] = [
      { content: 'Pending', image:'icons/pending_icon.svg' },
      { content: 'Completed', image:'icons/realized_icon.svg' },
      { content: 'Rejected', image:'icons/reject_icon.svg' },
      { content: 'Cancelled', image:'icons/canceled_icon.svg' },
      { content: 'Accepted', image:'icons/accepted_icon.svg' },
  ]

  sortByItems: DropDownListItem[] = [
      {content: 'Reservation date', image: 'icons/sort_number_asc_icon.svg', option: 'reservationDate,asc'},
      {content: 'Reservation date', image: 'icons/sort_number_desc_icon.svg', option: 'reservationDate,desc'},
  ]

  pagination: Pagination = {
      totalItems: 0,
      itemsPerPage: 5,
      currentPage: 0,
      itemsPerPageOptions: [5, 10, 15, 25, 30]
  }

  reservationSearch: UserReservationSearch = {
      user_id: 0,
      sort: 'None',
      status: 'None',
      offerName: '',
  }

  isReservationLoading = false;
  isAddModalOpen = false;

  reservationList: UserReservationResponse[] = []

  opinionModalInput: OpinionModalInput | null = null;

  constructor(private reservationService: ReservationService, private userContextService: UserContextService) {
  }

  ngOnInit(): void {
    this.isReservationLoading = true;
    this.userContextService.getUserData().pipe(
        concatMap(res  => {
            this.reservationSearch.user_id = res.id;
            return this.reservationService.readUserReservations(this.pagination, this.reservationSearch);
        })
    ).subscribe(response => {
        if(response.status == 200 && response.body){
            this.reservationList = response.body.content
            this.pagination.currentPage = response.body.page.number;
            this.pagination.totalItems = response.body.page.totalElements;
        }
        this.isReservationLoading = false;
    })
  }

  protected readReservationsFromApi(): void {
      this.reservationList = []
      this.isReservationLoading = true;
      this.reservationService.readUserReservations(this.pagination, this.reservationSearch)
            .subscribe(response => {
                    if(response.status == 200 && response.body){
                        this.reservationList = response.body.content
                        this.pagination.currentPage = response.body.page.number;
                        this.pagination.totalItems = response.body.page.totalElements;
                    }
                    this.isReservationLoading = false;
            })
  }


  getImage(status: string): string {
      return this.statusList.find(s => s.status === status)?.image || '';
  }

  cancelReservation(item: UserReservationResponse) {
        this.reservationService.cancelUserReservation(item.id).subscribe(response => {
            if(response.status == 200 && response.body) {
                this.reservationList = this.reservationList.map(item => {
                    return item.id === response.body?.id ? response.body : item;
                })
            }
        })
  }

  onFilterChange($event: string) {
    this.reservationSearch.status = $event;
    this.readReservationsFromApi();
  }

  onSortChange($event: SortBy) {
    this.reservationSearch.sort = $event.sorting;
    this.readReservationsFromApi();
  }

  onSearchChanged($event: string) {
      this.reservationSearch.offerName = $event;
      this.readReservationsFromApi();
  }

  openAddOpinionModal(item: UserReservationResponse) {
      if(!item.hasUserRatedOffer && item.status.toLowerCase() === 'completed'){
          this.opinionModalInput = {
              offerToReview: item.offer,
              companyNameToReview: item.companyName,
              companyLogo: item.companyLogo,
          }
          this.isAddModalOpen = true;
      }
  }

  onAddOpinionModalClose($event: boolean) {
    console.log($event);

    if($event) {
        this.readReservationsFromApi();
    }

    this.isAddModalOpen = false;
    this.opinionModalInput = null;
  }

  protected onPaginatorChanged() {
      this.readReservationsFromApi();
  }

  protected isCancelAvailable(status: string) {
      return !(status.toLowerCase() == 'cancelled' || status.toLowerCase() == 'completed' || status.toLowerCase() == 'rejected');
  }
}
