import {Component, Input, input} from '@angular/core';
import {DatePipe, NgClass} from "@angular/common";
import {AccountListItem} from "../../model/gui/admin.gui.model";
import {Page} from "../../model/search/search.model";
import {ROLE_OPTIONS} from "../../model/constants/role-options";
import {Role, UserStatus} from "../../model/http/auth.model";
import {USER_STATUS_OPTIONS} from "../../model/constants/user-status-options";
import {idToColor} from "../../utils.functions";


@Component({
  selector: 'app-user-list',
  imports: [
    NgClass,
    DatePipe
  ],
  templateUrl: './user-list.component.html',
  styleUrl: './user-list.component.css'
})
export class UserListComponent{
  protected readonly ROLE_OPTIONS = ROLE_OPTIONS;

  isLoading = input<boolean>(true);

  @Input() users: Page<AccountListItem> | null = null;

  skeletonRows = Array(5).fill(0);





  getStatusLabel(status: string): string {
    switch (status) {
      case 'active': return 'Active';
      case 'not_active': return 'Not active';
      case 'suspended': return 'Suspended';
      case 'block': return 'Block';
      default: return status;
    }
  }

  getTest(status: UserStatus): string {
    return USER_STATUS_OPTIONS.find(option => option.value === status)?.label ?? status;
  }

  getRoleLabel(value: Role): string {
    return ROLE_OPTIONS.find(option => option.value === value)?.label ?? value;
  }

  protected getInitials(name: string): string {
    if (!name) return 'U';
    const parts = name.trim().split(' ');
    return parts.length >= 2
        ? `${parts[0][0]}${parts[1][0]}`.toUpperCase()
        : parts[0].substring(0, 2).toUpperCase();
  }


  protected readonly idToColor = idToColor;
}
