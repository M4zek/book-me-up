import {Component, Input} from '@angular/core';
import {DatePipe, NgForOf, NgIf} from "@angular/common";
import {UserContextService} from "../../../service/user-context.service";
import {ReviewUserDetailsResponse} from "../../../model/http/review.model";
import {UserResponse} from "../../../model/http/user.model";
import {getUserAvatar} from "../../../utils.functions";
import {EditOpinionModalComponent} from "../../modals/edit-opinion-modal/edit-opinion-modal.component";
import {ConfirmService} from "../../../service/confirm.service";


@Component({
  selector: 'app-user-opinions-list',
    imports: [
        NgForOf,
        DatePipe,
        NgIf,
        EditOpinionModalComponent
    ],
  templateUrl: './user-opinions-list.component.html',
  styleUrl: './user-opinions-list.component.css'
})
export class UserOpinionsListComponent {

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
}
