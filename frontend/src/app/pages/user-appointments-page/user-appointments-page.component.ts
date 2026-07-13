import {Component, OnInit} from '@angular/core';
import {SearchAndSortBarComponent, SortBy} from "../../components/search-bar/search-and-sort-bar.component";
import {PaginatorComponent} from "../../components/paginator/paginator.component";
import {AsyncPipe, DatePipe, NgClass, NgForOf, NgIf} from "@angular/common";
import {DropDownListItem} from "../../components/drop-down-list/drop-down-list.component";
import {
    AddOpinionModalComponent,
    OpinionModalInput
} from "../../components/modals/add-opinion-modal/add-opinion-modal.component";
import {ReservationService} from "../../service/reservation.service";
import {Pagination, UserReservationSearch} from "../../model/search/search.model";
import {UserContextService} from "../../service/user-context.service";
import {concatMap, interval, map, startWith} from "rxjs";
import {DoubleSpinnerComponent} from "../../components/double-spinner/double-spinner.component";
import {UserReservationResponse} from "../../model/http/reservation.model";
import {RouterLink} from "@angular/router";
import {MyImgComponent} from "../../components/my-img/my-img.component";
import {AddressResponse, FileType} from "../../model/http/company.model";
import {ToastService} from "../../service/toast.service";
import {ConfirmService} from "../../service/confirm.service";


export interface Status{
  status: string;
  image: string;
}

export interface StatusText{
    status: string;
    text: string;
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
        DoubleSpinnerComponent,
        RouterLink,
        MyImgComponent,
        DatePipe,
        AsyncPipe
    ],
  templateUrl: './user-appointments-page.component.html',
  styleUrl: './user-appointments-page.component.css'
})
export class UserAppointmentsPageComponent implements OnInit {
    protected readonly FileType = FileType;
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
      itemsPerPage: 15,
      currentPage: 0,
      itemsPerPageOptions: [5, 10, 15, 25, 30, 50]
  }

  reservationSearch: UserReservationSearch = {
      user_id: 0,
      sort: 'None',
      status: 'None',
      offerName: '',
  }

  statusText: StatusText[] = [
      {status: "PENDING", text: 'The reservation is awaiting confirmation. We will notify you once its status changes.'},
      {status: "CANCELLED", text: 'The reservation has been cancelled. If you have any questions, please contact us.'},
      {status: "ACCEPTED", text: 'The reservation has been accepted. Everything is ready — we look forward to seeing you at the scheduled time.'},
      {status: "REJECTED", text: 'The reservation has been rejected. If needed, you can try again or contact us for more details.'},
      {status: "COMPLETED", text: 'The reservation has been completed. Thank you for using our service — we hope to see you again.'}
  ]

  isReservationLoading = false;
  isAddModalOpen = false;

  reservationList: UserReservationResponse[] = []

  opinionModalInput: OpinionModalInput | null = null;


  reservationSelected: UserReservationResponse | null = null;

  constructor(private reservationService: ReservationService,
              private userContextService: UserContextService,
              private confirmService: ConfirmService,
              private toast: ToastService) {
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


  async cancelReservation(item: UserReservationResponse) {
       let result = await this.confirmService.open(`Are you sure to cancel the reservation [${item.reservationNumber}]?`);

       if(!result){ return; }

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
      } else {
        this.toast.show(`You already have review assigned to this offer: ${item.offer.name}`, "warning");
      }
  }

  onAddOpinionModalClose($event: boolean) {
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

  protected getAddressString(address: AddressResponse): string{
      return `${address.postalCode} ${address.city}, ${address.street} ${address.buildingNumber}`
  }


  protected onReservationSelected(item: UserReservationResponse) {
      if(this.reservationSelected === item) {
          this.reservationSelected = null;
      } else {
          this.reservationSelected = item;
      }
  }

  getStatusText(status: string) {
      return this.statusText.find(s => s.status === status)?.text;
  }


  countDownS = interval(1000).pipe(
      startWith('Calculating...'),
      map(() => this.calculateRemainingTime())
  )

  calculateRemainingTime() {

      if(!this.reservationSelected) return;

      const date = new Date(this.reservationSelected.reservationDate);

      let diff = date.getTime() - Date.now();

      if (diff <= 0) return "Time's up";

      let time_txt = '';

      const d = Math.floor(diff / (1000 * 60 * 60 * 24));
      diff %= (1000 * 60 * 60 * 24);
      if(d != 0) time_txt += `${d}d `

      const h = Math.floor(diff / (1000 * 60 * 60));
      diff %= (1000 * 60 * 60);
      if(h != 0) time_txt += `${h}h `

      const m = Math.floor(diff / (1000 * 60));
      diff %= (1000 * 60);
      if(m != 0) time_txt += `${m}m `

      const s = Math.floor(diff / 1000);
      time_txt += `${s}s`

      return time_txt;
  }

}
