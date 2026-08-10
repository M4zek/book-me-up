import {Component, OnInit, signal} from '@angular/core';
import {StatCardComponent} from "../../../components/stat-card/stat-card.component";
import {ActivityCardComponent} from "../../../components/user-activity-card/activity-card.component";
import {animate, state, style, transition, trigger} from "@angular/animations";
import {
  AdminUserToolbarComponent,
  UserFilterState
} from "../../../components/admin-user-toolbar/admin-user-toolbar.component";
import {UserListComponent} from "../../../components/user-list/user-list.component";
import {PaginatorComponent} from "../../../components/paginator/paginator.component";
import {AccountListItem, UserActivityItem, UsersStatistics} from "../../../model/gui/admin.gui.model";
import {UserService} from "../../../service/user.service";
import {StatsService} from "../../../service/stats.service";
import {ToastService} from "../../../service/toast.service";
import {Page, Pagination} from "../../../model/search/search.model";
import {PAGINATOR} from "../../../model/constants/paginator-options";

@Component({
  selector: 'app-admin-users',
  imports: [
    StatCardComponent,
    ActivityCardComponent,
    AdminUserToolbarComponent,
    UserListComponent,
    PaginatorComponent
  ],
  templateUrl: './admin-users.component.html',
  styleUrl: './admin-users.component.css',
  animations: [
    trigger('collapseContent', [
      state('expanded', style({
        height: '*',
        opacity: 1,
        visibility: 'visible',
        paddingTop: '*',
        paddingBottom: '*'
      })),
      state('collapsed', style({
        height: '0px',
        opacity: 0,
        visibility: 'hidden',
        overflow: 'hidden',
        paddingTop: '0px',
        paddingBottom: '0px'
      })),

      transition('expanded <=> collapsed', [
        animate('300ms ease-in-out')
      ]),
    ]),
  ],

})
export class AdminUsersComponent implements OnInit {

  isExpanded: boolean = true;

  paginator: Pagination = PAGINATOR;
  filters!: UserFilterState;

  users = signal<Page<AccountListItem> | null>(null)
  isUsersLoading = signal<boolean>(false)


  isStatsLoading = signal(true);
  stats = signal<UsersStatistics | null>(null);

  isNewAccountsLoading = signal(true);
  newAccounts = signal<Page<UserActivityItem> | null>(null)


  isLastLoggedInLoading = signal(true);
  lastLoggedIn =signal<Page<UserActivityItem> | null>(null)


  isAdminActiveHistoryLoading = signal(true);
  lastActiveAdmins = signal<Page<UserActivityItem> | null>(null)


  constructor(private userService: UserService,
              private statsService: StatsService,
              private toast: ToastService) {
  }


  ngOnInit() {
    this.loadingUsersStats();
    this.loadingNewAccountHistory();
    this.loadingLastLoggedIn();
    this.loadingAdminActivityHistory();
    this.searchUsers(null, null);
  }

  private loadingUsersStats(){
    this.isStatsLoading.set(true);

    this.statsService.getUsersStats().subscribe({
      next: data => {
        this.stats.set(data.body);
        this.isStatsLoading.set(false);
      },
      error: error => {
        this.isStatsLoading.set(false);
        this.toast.show("An error occurred while loading the users statistics.")
        console.error(error);
      }
    })
  }

  private loadingNewAccountHistory(page: Pagination | null = null){
    this.isNewAccountsLoading.set(true);
    this.userService.getNewAccountHistory(page).subscribe({
      next: data => {
        this.newAccounts.set(data.body)
        this.isNewAccountsLoading.set(false);
      }, error: error => {
        this.isNewAccountsLoading.set(false);
        this.toast.show("An error occurred while loading the last created accounts", 'error')
        console.error(error);
      }
    })
  }

  private loadingLastLoggedIn(page: Pagination | null = null){
    this.isLastLoggedInLoading.set(true);
    this.userService.getLoggedInHistory(page).subscribe({
      next: data => {
        this.lastLoggedIn.set(data.body)
        this.isLastLoggedInLoading.set(false);
      }, error: error => {
        this.isLastLoggedInLoading.set(false);
        this.toast.show("An error occurred while loading the last logged in accounts", 'error')
        console.error(error);
      }
    })
  }

  private loadingAdminActivityHistory(page: Pagination | null = null){
    this.isAdminActiveHistoryLoading.set(true);
    this.userService.getAdminActivityHistory(page).subscribe({
      next: data => {
        this.lastActiveAdmins.set(data.body)
        this.isAdminActiveHistoryLoading.set(false);
      }, error: error => {
        this.isAdminActiveHistoryLoading.set(false);
        this.toast.show("An error occurred while loading the last logged in admins", 'error')
        console.error(error);
      }
    })
  }

  private searchUsers(page: Pagination | null = null, filters: UserFilterState | null = null){
    this.isUsersLoading.set(true);
    this.userService.searchUsersBasedOnFilters(page , filters).subscribe({
      next: data => {
        this.users.set(data.body);
        this.isUsersLoading.set(false);

        this.paginator.currentPage = data.body!.page.number;
        this.paginator.totalItems = data.body!.page.totalElements;

      }, error: error => {
        this.isUsersLoading.set(false);
        this.toast.show("An error occurred while searching users.", 'error')
        console.error(error);
      }
    })
  }


  protected onFilterChanged = (filters: UserFilterState) => {
    this.filters = filters;
    this.searchUsers(null, filters);
  }


  protected toggleExpand(): void {
    this.isExpanded = !this.isExpanded;
  }


  protected onPaginatorChanged($event: void) {
    this.searchUsers(this.paginator, this.filters);
  }
}
