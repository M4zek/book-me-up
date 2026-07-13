import {Component, Input} from '@angular/core';
import {DatePipe, NgForOf, NgIf} from "@angular/common";
import {UserContextService} from "../../../service/user-context.service";
import {ReviewUserDetailsResponse} from "../../../model/http/review.model";
import {UserResponse} from "../../../model/http/user.model";
import {getUserAvatar} from "../../../utils.functions";
import {EditOpinionModalComponent} from "../../modals/edit-opinion-modal/edit-opinion-modal.component";
import {ConfirmService} from "../../../service/confirm.service";
import {MyImgComponent} from "../../my-img/my-img.component";
import {FileType} from "../../../model/http/company.model";


@Component({
  selector: 'app-user-opinions-list',
    imports: [
        NgForOf,
        NgIf,
        EditOpinionModalComponent,
        MyImgComponent,
        DatePipe
    ],
  templateUrl: './user-opinions-list.component.html',
  styleUrl: './user-opinions-list.component.css'
})
export class UserOpinionsListComponent {
    protected readonly FileType = FileType;

  @Input() opinions: ReviewUserDetailsResponse[] = []

  isModalToEditOpen: boolean = false;
  opinionToEdit!: ReviewUserDetailsResponse | undefined;

  protected logged_id: number | null = null;

  constructor(private uct: UserContextService, private confirm: ConfirmService) {
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

  protected onEditOpinion(opinion: ReviewUserDetailsResponse) {
      if(this.isMyOpinion(opinion.author)){
          this.isModalToEditOpen = true;
          this.opinionToEdit = opinion;
      }
  }

  protected onEditModalClose(){
      this.isModalToEditOpen = false;
      this.opinionToEdit = undefined;
  }

  protected async onDeleteClick() {
      let result = await this.confirm.open("Are you sure you want to delete this opinion?");
      if(result){
          // TODO Remove opinion
      }
  }

  protected onReportOpinion() {
      // TODO OPEN Report opinion modal
  }


    getTimeAgo(input: string): string {

        const [time, date] = input.split(' ');
        const [hours, minutes] = time.split(':').map(Number);
        const [day, month, year] = date.split('.').map(Number);

        const parsedDate = new Date(year, month - 1, day, hours, minutes);
        const now = new Date();

        const diffMs = now.getTime() - parsedDate.getTime();

        const diffMinutes = Math.floor(diffMs / (1000 * 60));
        const diffHours = Math.floor(diffMs / (1000 * 60 * 60));
        const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));
        const diffMonths = Math.floor(diffDays / 30);
        const diffYears = Math.floor(diffDays / 365);

        if (diffMinutes < 1) return 'just now';
        if (diffMinutes < 60) return `${diffMinutes} min ago`;
        if (diffHours < 24) return `${diffHours}h ago`;
        if (diffDays < 30) return `${diffDays} days ago`;
        if (diffMonths < 12) return `${diffMonths} month${diffMonths === 1 ? '' : 's'} ago`;
        return `${diffYears} year${diffYears === 1 ? '' : 's'} ago`;
    }
}
