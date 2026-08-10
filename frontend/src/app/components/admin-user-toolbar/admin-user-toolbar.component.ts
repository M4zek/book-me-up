import {Component, computed, output, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {ClickOutsideDirective} from "../../click-outside.directive";
import {debounceTime, Subject, Subscription} from "rxjs";
import {USER_STATUS_OPTIONS} from "../../model/constants/user-status-options";
import {ROLE_OPTIONS} from "../../model/constants/role-options";

export interface FilterOption {
  label: string;
  value: string;
}

export interface UserFilterState {
  searchQuery: string;
  statuses: string[];
  roles: string[];
}

@Component({
  selector: 'app-admin-user-toolbar',
  standalone: true,
  imports: [CommonModule, FormsModule, ClickOutsideDirective],
  templateUrl: './admin-user-toolbar.component.html',
  styleUrls: ['./admin-user-toolbar.component.css']
})
export class AdminUserToolbarComponent {

  filtersChange = output<UserFilterState>();

  searchQuery = signal<string>('');
  isStatusOpen = signal<boolean>(false);
  isRolesOpen = signal<boolean>(false);
  selectedStatuses = signal<string[]>([]);
  selectedRoles = signal<string[]>([]);

  // statusOptions: FilterOption[] = [
  //   { label: '🟢 Active', value: 'active' },
  //   { label: '🟡 Not active', value: 'not_active' },
  //   { label: '🔴 Suspended', value: 'suspended' },
  //   { label: '🔴 Block', value: 'block' },
  // ];

  statusOptions = USER_STATUS_OPTIONS;
  roleOptions = ROLE_OPTIONS;

  private searchSubject = new Subject<string>();
  private sub: Subscription;

  constructor() {
    this.sub = this.searchSubject.pipe(
        debounceTime(500)
    ).subscribe(query => {
      this.searchQuery.set(query);
      this.emitFilters();
    });
  }

  onSearchInput(text: string) {
    this.searchSubject.next(text);
  }

  statusLabel = computed(() => {
    const selected = this.selectedStatuses();
    if (selected.length === 0) return 'Statuses: All';

    if (selected.length === 1) {
      const item = this.statusOptions.find(s => s.value === selected[0]);
      return `Status: ${item?.label || selected[0]}`;
    }

    return `Statuses: ${selected.length} selected`;
  });

  rolesLabel = computed(() => {
    const selected = this.selectedRoles();
    if (selected.length === 0) return 'Roles: All';
    if (selected.length === 1) {
      const item = this.roleOptions.find(r => r.value === selected[0]);
      return `Role: ${item?.label || selected[0]}`;
    }
    return `Roles: ${selected.length} selected`;
  });

  onStatusChange(value: string, event: Event) {
    const checked = (event.target as HTMLInputElement).checked;
    this.selectedStatuses.update(current =>
        checked ? [...current, value] : current.filter(s => s !== value)
    );
    this.emitFilters();
  }

  onRoleChange(value: string, event: Event) {
    const checked = (event.target as HTMLInputElement).checked;
    this.selectedRoles.update(current =>
        checked ? [...current, value] : current.filter(r => r !== value)
    );
    this.emitFilters();
  }

  private emitFilters() {
    this.filtersChange.emit({
      searchQuery: this.searchQuery(),
      statuses: this.selectedStatuses(),
      roles: this.selectedRoles()
    });
  }

  toggleStatusDropdown() {
    this.isRolesOpen.set(false);
    this.isStatusOpen.update(val => !val);
  }

  toggleRolesDropdown() {
    this.isStatusOpen.set(false);
    this.isRolesOpen.update(val => !val);
  }

  closeAllDropdowns() {
    this.isStatusOpen.set(false);
    this.isRolesOpen.set(false);
  }

  ngOnDestroy() {
    this.sub.unsubscribe();
  }
}