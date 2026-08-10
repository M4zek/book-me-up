import {Component, Input} from '@angular/core';
import {getTimeAgo} from "../../utils.functions";
import {UserActivityItem} from "../../model/gui/admin.gui.model";
import {Page} from "../../model/search/search.model";


@Component({
  selector: 'app-activity-card',
  imports: [],
  templateUrl: './activity-card.component.html',
  styleUrl: './activity-card.component.css'
})
export class ActivityCardComponent {

  protected readonly getTimeAgo = getTimeAgo;

  @Input() isLoading = true;

  @Input() users: Page<UserActivityItem> | null = null;



  protected getInitials(name: string): string {
    if (!name) return 'U';
    const parts = name.trim().split(' ');
    return parts.length >= 2
        ? `${parts[0][0]}${parts[1][0]}`.toUpperCase()
        : parts[0].substring(0, 2).toUpperCase();
  }
}
