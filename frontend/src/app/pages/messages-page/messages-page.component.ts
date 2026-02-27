import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {PaginatorComponent} from "../../components/paginator/paginator.component";
import {NgForOf, NgIf, SlicePipe} from "@angular/common";
import {Pagination} from "../../model/search/search.model";
import {
    CreateChatRoomModalComponent
} from "../../components/modals/create-chat-room-modal/create-chat-room-modal.component";
import {ChatService} from "../../service/chat.service";
import {ChatMessageResponse, Member, RoomResponse, RoomType} from "../../model/http/chat.model";
import {UserContextService} from "../../service/user-context.service";
import {DoubleSpinnerComponent} from "../../components/double-spinner/double-spinner.component";


@Component({
  selector: 'app-messages-page',
    imports: [
        PaginatorComponent,
        NgForOf,
        CreateChatRoomModalComponent,
        NgIf,
        SlicePipe,
        DoubleSpinnerComponent
    ],
  templateUrl: './messages-page.component.html',
  styleUrl: './messages-page.component.css'
})
export class MessagesPageComponent implements OnInit {

  @ViewChild('htmlMessageList') htmlMessageList!: ElementRef;

  RoomType = RoomType;
  isCreateModalVisible = false;

  roomPaginator: Pagination = {
    totalItems: 0,
    itemsPerPage: 10,
    currentPage: 0,
    itemsPerPageOptions: [5, 10, 15, 25, 50]
  }

  messagePaginator: Pagination = {
      totalItems: 0,
      itemsPerPage: 20,
      currentPage: 0,
      itemsPerPageOptions: [5,10,20,30,50]
  }

  logged_user_id: number | null = null

  isRoomsLoading: boolean = false;
  userRooms: RoomResponse[] = []
  selectedRoom: RoomResponse | null = null


  isMessageLoading = false;
  messages: ChatMessageResponse[] = []


  constructor(private chatService: ChatService, private ucs: UserContextService) {
  }

  ngOnInit() {
      this.ucs.getUserContext().subscribe(userContext => {
          this.logged_user_id = userContext.id;
      })
      this.readRooms();
  }


  readRooms(){
      this.isRoomsLoading = true;
      this.chatService.getUserRooms(this.roomPaginator).subscribe({
          next: response => {
              if(response.status === 200 && response.body) {
                  this.userRooms = response.body.content;
                  this.roomPaginator.currentPage = response.body.page.number;
                  this.roomPaginator.totalItems = response.body.page.totalElements;
              }
          }, complete: () => {
              this.isRoomsLoading = false;
      }
      })
  }

  readMessages(room_id: number) {
      this.isMessageLoading = true;
      this.chatService.getRoomMessages(room_id, this.messagePaginator).subscribe({
          next: response => {
              if(response.status === 200 && response.body) {

                  this.addMessages(response.body.content);

                  this.messagePaginator.currentPage = response.body.page.number;
                  this.messagePaginator.totalItems = response.body.page.totalElements;

              }
          }, complete: () => {
              this.isMessageLoading = false;
          }
      })
  }


  protected addMessages(new_messages: ChatMessageResponse[]) {
      const container = this.htmlMessageList.nativeElement;
      const scrollBottom = container.scrollHeight - container.scrollTop;

      new_messages.forEach((message) => {
          this.messages.unshift(message);
          setTimeout(() => {
              container.scrollTop = container.scrollHeight - scrollBottom;
          },10);
      })
  }

  onScroll() {
      const el = this.htmlMessageList.nativeElement;

      if (el.scrollTop === 0 && !this.isMessageLoading) {
          this.messagePaginator.currentPage += 1;

          // If all message already read return.
          if(this.messagePaginator.totalItems <= this.messages.length) {
              return;
          }


          if(this.selectedRoom)
              this.readMessages(this.selectedRoom.id);
      }
  }

  scrollToBottom() {
      setTimeout(() => {
          const el = this.htmlMessageList.nativeElement;
          el.scrollTop = el.scrollHeight;
      }, 100);

  }

  onCreateModalOpen(){
    this.isCreateModalVisible = true;
  }

  onCreateModalClose() {
    this.isCreateModalVisible = false;
  }

  protected onRoomPaginatorChanged($event: void) {
      this.readRooms();
  }

  protected findChatPartner(members: Member[]) {
      return members.find(member => member.id !== this.logged_user_id);
  }



  protected selectRoom(room: RoomResponse) {

      if(this.selectedRoom === room) {
          return;
      }

      if(this.selectedRoom != null){
          // TODO Unsubscribe recent room
      }

      this.resetMessages();
      this.selectedRoom = room;
      this.readMessages(this.selectedRoom.id);
      this.scrollToBottom();

      // TODO Here connect to room via WebSocket
  }


    protected resetMessages() {
      this.messages = [];
      this.messagePaginator.currentPage = 0;
    }


  protected getUserAvatar(member: Member): string {
      let avatar = member.avatar;

      if (!avatar) {
          return 'images/user_default_avatar.png'
      }

      if(!avatar.includes('data:image/jpeg;base64,')){
          return `data:image/jpeg;base64,${avatar}`;
      }

      return avatar;
  }

  formatMessageDate(msg_date: string): string {
      const date = new Date(msg_date.replace(' ', 'T'));
      const now = new Date();

      const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
      const targetDay = new Date(date.getFullYear(), date.getMonth(), date.getDate());

      const diffMs = today.getTime() - targetDay.getTime();
      const diffDays = diffMs / (1000 * 60 * 60 * 24);

      const time = date.toLocaleTimeString([], {
          hour: '2-digit',
          minute: '2-digit'
      });

      if (diffDays === 0) {
          return time;
      }

      if (diffDays === 1) {
          return `Yesterday at ${time}`;
      }

      const datePart = date.toLocaleDateString([], {
          day: 'numeric',
          month: 'short'
      });

      return `${datePart} at ${time}`;
  }

}
