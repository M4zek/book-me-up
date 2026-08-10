import {Component, computed, effect, input, output, signal} from '@angular/core';
import {debounceTime, Subject, Subscription} from "rxjs";
import {FilterOption} from "../admin-user-toolbar/admin-user-toolbar.component";
import {ClickOutsideDirective} from "../../click-outside.directive";


export interface TicketFilterState {
  searchQuery: string;
  statuses: string[];
  types: string[];
}

@Component({
  selector: 'app-admin-ticket-toolbar',
  imports: [
    ClickOutsideDirective
  ],
  templateUrl: './admin-ticket-toolbar.component.html',
  styleUrl: './admin-ticket-toolbar.component.css'
})
export class AdminTicketToolbarComponent {
  filtersChange = output<TicketFilterState>();

  initialStatuses = input <string[]>([])
  isStatusOpen = signal<boolean>(false);
  selectedStatuses = signal<string[]>([]);

  initialTypes = signal<string[]>([]);
  isTypesOpen = signal<boolean>(false);
  selectedTypes = signal<string[]>([]);

  searchQuery = signal<string>('');

  statusOptions: FilterOption[] = [
    { label: '🔵 Open', value: 'open' },
    { label: '🟠 In Progress', value: 'in_progress' },
    { label: '🔴 Canceled', value: 'canceled' },
    { label: '🟢 Resolved', value: 'resolved' },
  ];

  typesOptions: FilterOption[] = [
    {label: 'Company', value: 'company'},
    {label: 'Reservation', value: 'reservation'},
    {label: 'Review', value: 'review'},
    {label: 'Service', value: 'service'},
    {label: 'Chat Message', value: 'chat_message'},
  ]

  private searchSubject = new Subject<string>();
  private sub: Subscription;

  constructor() {
    this.sub = this.searchSubject.pipe(
        debounceTime(500)
    ).subscribe(query => {
      this.searchQuery.set(query);
      this.emitFilters();
    });

    effect(() => {
      this.selectedStatuses.set(this.initialStatuses());
      this.selectedTypes.set(this.initialTypes());
    });

  }

  statusLabel = computed(() => {
    const selected = this.selectedStatuses();

    if (selected.length === 0) {
      return 'Statuses: All';
    }

    const labels = selected.map(value => {
      const item = this.statusOptions.find(s => s.value === value);
      return item?.label || value;
    });

    return `Statuses: ${labels.join('\t')}`;
  });

  typesLabel = computed(() => {
    const selected = this.selectedTypes();

    if (selected.length === 0) {
      return 'Types: All';
    }

    const labels = selected.map(value => {
      const item = this.typesOptions.find(s => s.value === value);
      return item?.label || value;
    });
    return `Types: ${labels.join('\t')}`;
  })

  onSearchInput(text: string) {
    this.searchSubject.next(text);
  }

  onStatusChange(value: string, event: Event) {
    const checked = (event.target as HTMLInputElement).checked;
    this.selectedStatuses.update(current =>
        checked ? [...current, value] : current.filter(s => s !== value)
    );
    this.emitFilters();
  }

  onTypeChange(value: string, event: Event) {
    const checked = (event.target as HTMLInputElement).checked;
    this.selectedTypes.update(current =>
      checked ? [...current, value] : current.filter(s => s !== value)
    );
    this.emitFilters();
  }

  private emitFilters() {
    this.filtersChange.emit({
      searchQuery: this.searchQuery(),
      statuses: this.selectedStatuses(),
      types: this.selectedTypes()
    });
  }

  toggleStatusDropdown() {
    this.isStatusOpen.update(val => !val);
    this.isTypesOpen.set(false)
  }

  toggleTypeDropdown() {
    this.isTypesOpen.update(val => !val);
    this.isStatusOpen.set(false)
  }

  closeAllDropdowns() {
    this.isStatusOpen.set(false);
    this.isTypesOpen.set(false);
  }

  ngOnDestroy() {
    this.sub.unsubscribe();
  }
}
