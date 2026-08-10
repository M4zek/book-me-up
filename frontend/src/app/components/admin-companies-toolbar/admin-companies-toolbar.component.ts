import {Component, computed, input, output, signal} from '@angular/core';
import {debounceTime, Subject, Subscription} from "rxjs";
import {FilterOption} from "../admin-user-toolbar/admin-user-toolbar.component";
import {ClickOutsideDirective} from "../../click-outside.directive";

export interface CompanyFilterState {
  searchQuery: string;
  statuses: string[];
  categories: string[];
}

@Component({
  selector: 'app-admin-companies-toolbar',
  imports: [
    ClickOutsideDirective
  ],
  templateUrl: './admin-companies-toolbar.component.html',
  styleUrl: './admin-companies-toolbar.component.css'
})
export class AdminCompaniesToolbarComponent {
  filtersChange = output<CompanyFilterState>();

  searchQuery = signal<string>('');
  isStatusOpen = signal<boolean>(false);
  selectedStatuses = signal<string[]>([]);
  statusOptions: FilterOption[] = [
    { label: '🟢 Active', value: 'active' },
    { label: '🟡 Not active', value: 'not_active' }
  ];

  selectedCategories = signal<string[]>([]);
  categoriesOptions = input<string[]>([])
  isCategoriesLoading = signal<boolean>(true)
  isCategoriesOpen = signal<boolean>(false)

  private searchSubject = new Subject<string>();
  private sub: Subscription;

  constructor() {
    this.sub = this.searchSubject.pipe(
        debounceTime(500)
    ).subscribe(query => {
      this.searchQuery.set(query);
      this.emitFilters();
    });

    setTimeout(()=> {
      this.isCategoriesLoading.set(false);

      this.categoriesOptions().push('Test')

    }, 3000)

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

  categoryLabel = computed(() => {
    const selected = this.selectedCategories();
    if (selected.length === 0) return 'Categories: All';

    if (selected.length === 1) {
      const item = this.categoriesOptions().find(s => s === selected[0]);
      return `Category: ${item || selected[0]}`;
    }

    return `Categories: ${selected.length} selected`;
  })

  onStatusChange(value: string, event: Event) {
    const checked = (event.target as HTMLInputElement).checked;
    this.selectedStatuses.update(current =>
        checked ? [...current, value] : current.filter(s => s !== value)
    );
    this.emitFilters();
  }


  private emitFilters() {
    this.filtersChange.emit({
      searchQuery: this.searchQuery(),
      statuses: this.selectedStatuses(),
      categories: this.selectedCategories(),
    });
  }

  toggleStatusDropdown() {
    this.isStatusOpen.update(val => !val);
    this.isCategoriesOpen.set(false);
  }

  toggleCategoriesDropdown() {
    this.isCategoriesOpen.update(val => !val);
    this.isStatusOpen.set(false);
  }

  closeAllDropdowns() {
    this.isStatusOpen.set(false);
    this.isCategoriesOpen.set(false);
  }

  ngOnDestroy() {
    this.sub.unsubscribe();
  }

  protected onCategoriesChanged(value: string, event: Event) {
    const checked = (event.target as HTMLInputElement).checked;
    this.selectedCategories.update(current =>
        checked ? [...current, value] : current.filter(s => s !== value)
    );
    this.emitFilters();
  }
}
