import {Component, Input} from '@angular/core';
import {DatePipe, NgForOf, NgIf} from "@angular/common";
import {UserContextService} from "../../../service/user-context.service";
import {ReviewUserDetailsResponse} from "../../../model/http/review.model";
import {UserResponse} from "../../../model/http/user.model";
import {getUserAvatar} from "../../../utils.functions";




@Component({
  selector: 'app-user-opinions-list',
    imports: [
        NgForOf,
        DatePipe,
        NgIf
    ],
  templateUrl: './user-opinions-list.component.html',
  styleUrl: './user-opinions-list.component.css'
})
export class UserOpinionsListComponent {

  @Input() opinions: ReviewUserDetailsResponse[] = []

  protected logged_id: number | null = null;

  constructor(private uct: UserContextService) {
      this.uct.getUserContext().subscribe(userContext => {
          this.logged_id = userContext.id;
      })
  }


  protected isMyOpinion(user: UserResponse){
      return this.logged_id == user.id;
  }

  rows(n: number): number[] {
    return Array(n).fill(0).map((_, i) => i);
  }

    protected readonly getUserAvatar = getUserAvatar;
}
