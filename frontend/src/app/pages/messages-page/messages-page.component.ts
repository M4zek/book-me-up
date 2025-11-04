import {Component} from '@angular/core';
import {PaginatorComponent} from "../../components/paginator/paginator.component";
import {NgClass, NgForOf} from "@angular/common";
import {Pagination} from "../../model/search/search.model";
import {
    CreateChatRoomModalComponent
} from "../../components/modals/create-chat-room-modal/create-chat-room-modal.component";

// Temporary interface
export interface RoomMessage {
  id: string;
  name: string;
  type: string;
}


@Component({
  selector: 'app-messages-page',
    imports: [
        PaginatorComponent,
        NgForOf,
        NgClass,
        CreateChatRoomModalComponent
    ],
  templateUrl: './messages-page.component.html',
  styleUrl: './messages-page.component.css'
})
export class MessagesPageComponent {

  isCreateModalVisible = false;

  paginator: Pagination = {
    totalItems: 50,
    itemsPerPage: 10,
    currentPage: 1,
    itemsPerPageOptions: [5, 10, 15, 25, 50]
  }

  roomMessages: RoomMessage = {
    id: '1',
    name: 'John Doe',
    type: 'message',
  }


  createList(max: number){
    let list: number[] = [];
    for (let i = 0; i < max; i++) {
      list.push(i);
    }
    return list;
  }

  getMessageSide(id: number) {
    if (id % 2 == 0) {
      return 'left';
    } else {
      return 'right';
    }
  }


  onCreateModalOpen(){
    this.isCreateModalVisible = true;
  }

  onCreateModalClose() {
    this.isCreateModalVisible = false;
  }
}
