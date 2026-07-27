import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {PaginatorComponent} from "../../components/paginator/paginator.component";
import {NgForOf, NgIf, SlicePipe} from "@angular/common";
import {Pagination} from "../../model/search/search.model";
import {
    CreateChatRoomModalComponent
} from "../../components/modals/create-chat-room-modal/create-chat-room-modal.component";
import {ChatService} from "../../service/chat.service";
import {
    ChatMessageReceipt,
    ChatMessageResponse,
    Member,
    MessageType,
    NotificationType,
    RoomResponse,
    RoomType,
    WebSocketChatData,
    WebSocketNotification
} from "../../model/http/chat.model";
import {UserContextService} from "../../service/user-context.service";
import {DoubleSpinnerComponent} from "../../components/double-spinner/double-spinner.component";
import {WebsocketService} from "../../service/websocket.service";
import {filter} from "rxjs";
import {FormsModule} from "@angular/forms";
import {CdkTextareaAutosize} from "@angular/cdk/text-field";
import {MyImgComponent} from "../../components/my-img/my-img.component";
import {FileType} from "../../model/http/company.model";


@Component({
  selector: 'app-messages-page',
    imports: [
        PaginatorComponent,
        NgForOf,
        CreateChatRoomModalComponent,
        NgIf,
        SlicePipe,
        DoubleSpinnerComponent,
        FormsModule,
        CdkTextareaAutosize,
        MyImgComponent
    ],
  templateUrl: './messages-page.component.html',
  styleUrl: './messages-page.component.css'
})
export class MessagesPageComponent implements OnInit {
  @ViewChild('htmlMessageList') htmlMessageList!: ElementRef;
  protected readonly FileType = FileType;

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
  isSocketConnected = false;
  messages: ChatMessageResponse[] = []

  messageModel: string = '';

  constructor(
      private webSocket: WebsocketService,
      private chatService: ChatService,
      private ucs: UserContextService) {}

  ngOnDestroy() {
      if(this.selectedRoom) {
          this.webSocket.unsubscribeRoom(this.selectedRoom.id);
          this.selectedRoom = null;
      }
  }

  ngOnInit() {
      this.ucs.getUserContext().subscribe(userContext => {
          this.logged_user_id = userContext.id;
      })
      this.readRooms();

      this.webSocket.messageSubject$
          .pipe(
              filter((message): message is WebSocketChatData => message !== null))
          .subscribe((payload: WebSocketChatData) => {
              switch (payload.type) {
                  case MessageType.MESSAGE:
                      const received_msg: ChatMessageResponse = payload.message;
                      this.messages.push(received_msg);
                      this.scrollToBottom();
                      break;

                  case MessageType.RECEIPT:
                      let receipt: ChatMessageReceipt = payload.receipt;
                      this.updateLastReadMessageByReceipt(receipt);
                      this.updateUnreadMessageInRoom(receipt);
                      this.scrollToBottom();
                      break;
              }
      })

      this.webSocket.connectStatus$.subscribe(status => {
            this.isSocketConnected = status;
      })

      this.webSocket.notificationSubject$
          .pipe(
              filter((notification): notification is WebSocketNotification => notification !== null)
          )
          .subscribe(notification => {
          switch (notification.type){
              case NotificationType.CHAT:
                  const room_data = notification.room;

                  this.updateRoomInList(room_data);

                  this.userRooms.map((room: RoomResponse) => {
                          if(room.id === room_data.id){
                              room.numOfUnreadMessages = room_data.numOfUnreadMessages;
                              room.lastMessage = room_data.lastMessage;
                          }
                  });

                  break;
          }
      })
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
              if(this.selectedRoom && this.messagePaginator.currentPage === 0)
                  this.webSocket.sendReceiptForMessage(this.selectedRoom.id);
          }
      })
  }

  protected sendMessage() {
      if(this.messageModel.length > 0 && this.selectedRoom){
          this.webSocket.sendMessage(this.messageModel, this.selectedRoom.id)
          this.messageModel = '';
      }
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
      }, 200);

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



  protected async selectRoom(room: RoomResponse) {

      if(this.selectedRoom === room) {
          this.webSocket.unsubscribeRoom(this.selectedRoom.id);
          this.selectedRoom = null;
          return;
      }

      if(this.selectedRoom != null){
          this.webSocket.unsubscribeRoom(this.selectedRoom.id);
      }

      this.selectedRoom = room;
      this.resetMessages();
      this.scrollToBottom();
      this.webSocket.subscribeRoom(this.selectedRoom.id);

      this.readMessages(this.selectedRoom.id);
  }


    protected resetMessages() {
      this.messages = [];
      this.messagePaginator.currentPage = 0;
    }


  formatMessageDate(msg_date: string): string {
      const date = new Date(msg_date.replace(' ', 'T'));
      const now = new Date();

      const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
      const targetDay = new Date(date.getFullYear(), date.getMonth(), date.getDate());

      const diffYear = today.getFullYear() - targetDay.getFullYear();
      const diffMs = today.getTime() - targetDay.getTime();
      const diffDays = diffMs / (1000 * 60 * 60 * 24);

      const time = date.toLocaleTimeString([], {
          hour: '2-digit',

          minute: '2-digit'
      });
      let datePart: string = '';

      if (diffDays === 0) {
          return time;
      }

      if (diffDays === 1) {
          return `Yesterday at ${time}`;
      }

      if(diffYear > 0){
          datePart = date.toLocaleDateString([], {
              day: 'numeric',
              month: 'short',
              year: 'numeric'
          });
      } else {
          datePart = date.toLocaleDateString([], {
              day: 'numeric',
              month: 'short',
          });
      }


      return `${datePart} at ${time}`;
  }

  isNextDay(index: number) {
      if (index >= this.messages.length - 1) return false;

      const current = new Date(this.messages[index].createdDate);
      const next = new Date(this.messages[index + 1].createdDate);

      return (
          current.getFullYear() !== next.getFullYear() ||
          current.getMonth() !== next.getMonth() ||
          current.getDate() !== next.getDate()
      );
  }

    isToday(dateStr: string): boolean {
        const date = this.parseDate(dateStr);
        if (!date) return false;

        const today = new Date();

        return (
            date.getFullYear() === today.getFullYear() &&
            date.getMonth() === today.getMonth() &&
            date.getDate() === today.getDate()
        );
    }

    private parseDate(dateStr: string): Date | null {
        const normalized = dateStr.replace(' ', 'T');
        const date = new Date(normalized);
        return isNaN(date.getTime()) ? null : date;
    }

    protected getMembersWhoReadTheMessageWithoutLoggedUser(message: ChatMessageResponse){
      return message.readBy.filter(item => item.id !== this.logged_user_id && item.id !== message.sender.id);
    }

    protected updateLastReadMessageByReceipt(receipt: ChatMessageReceipt){

      if(!this.selectedRoom || this.messages.length === 0 || !receipt){ return; }

      const lastMessage = this.messages[this.messages.length - 1];
      const memberId = receipt.reader_id;

      const member = this.selectedRoom.memberProjections.find(m => m.id === memberId);

      if(!member){ return; }

      this.messages = this.messages.filter(message => message.readBy = message.readBy.filter(item => item.id !== receipt.reader_id));
      this.messages = this.messages.filter(message => message.id !== lastMessage.id);

      lastMessage.readBy.push(member);
      this.messages.push(lastMessage);
    }

    protected updateUnreadMessageInRoom(receipt: ChatMessageReceipt){
        this.userRooms.map(item => {
            if(item.id == receipt.room_id){
                item.numOfUnreadMessages = 0;
            }
        })
    }

    protected showSenderAvatar(message: ChatMessageResponse){
      return message.sender.id !== this.logged_user_id;
    }

    private updateRoomInList(room_data: RoomResponse) {
        let room = this.userRooms.find(room => room.id === room_data.id);

        if(this.selectedRoom?.id === room_data.id) this.selectedRoom = room_data;

        if(room){
            room.numOfUnreadMessages = 0;
            this.userRooms = [
                room_data,
                ...this.userRooms.filter(item => item.id !== room_data.id)
            ]
        }
    }


    protected addUserRoom($event: RoomResponse) {
        this.userRooms.push($event);
    }
}
