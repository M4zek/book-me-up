import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges, ViewChild} from '@angular/core';
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {PaginatorComponent} from "../../paginator/paginator.component";
import {FormsModule, NgForm} from "@angular/forms";
import {UserNameAndAvatar} from "../../../model/gui/gui.model";
import {ToastService} from "../../../service/toast.service";
import {Pagination} from "../../../model/search/search.model";
import {UserService} from "../../../service/user.service";
import {UserContextService} from "../../../service/user-context.service";
import {RoomRequest, RoomType} from "../../../model/http/chat.model";
import {DoubleSpinnerComponent} from "../../double-spinner/double-spinner.component";
import {ChatService} from "../../../service/chat.service";


@Component({
  selector: 'app-create-chat-room-modal',
    imports: [
        NgIf,
        PaginatorComponent,
        NgForOf,
        FormsModule,
        NgClass,
        DoubleSpinnerComponent
    ],
  templateUrl: './create-chat-room-modal.component.html',
  styleUrl: './create-chat-room-modal.component.css'
})
export class CreateChatRoomModalComponent implements OnChanges {
  @ViewChild('roomNameForm') roomNameForm!: NgForm;
  @ViewChild('searchUserForm') searchUserForm!: NgForm;

  isUsersLoading = false;

  resultUserList: UserNameAndAvatar[] = []
  selectedUser: UserNameAndAvatar | null = null;
  selectedUserList: UserNameAndAvatar[] = []

  ownerId: number = 0;
  roomName: string = '';
  isSingleChat: boolean = true;

  paginator: Pagination = {
    totalItems: this.resultUserList.length,
    itemsPerPage: 5,
    currentPage: 0,
    itemsPerPageOptions: [5, 10, 15, 25, 50]
  }


  searchModel: {firstName: string, lastName: string} = {
      firstName: '',
      lastName: '',
  }

  @Input() isVisible: boolean = false;
  @Output() closeModal = new EventEmitter<void>();

  constructor(
      private userService: UserService,
      private ucs: UserContextService,
      private chatService: ChatService,
      private toast: ToastService) {

      this.ucs.getUserContext().subscribe(user => {
          this.ownerId = user.id;
      })
  }



  ngOnChanges(changes: SimpleChanges): void {
      if (changes['isVisible']) {
        if(this.isVisible) {
            this.callSearchUserFromService();
        } else {
            this.clearSearchForm();
            this.resultUserList = [];
            this.selectedUserList = [];
            this.selectedUser = null;
            this.searchModel = {
                firstName: '',
                lastName: '',
            }
        }
      }
  }



  close() {
    this.resetModal()
    this.closeModal.emit();
  }

  resetModal() {
    this.resultUserList = [];
    this.searchModel = {firstName: '', lastName: ''}
    this.selectedUserList = [];
    this.selectedUser = null;
  }

  confirm(){
    if(this.isSingleChat){
      // Single user chat
      if(this.selectedUser != null){

          const body: RoomRequest = {
              type: RoomType.PRIVATE,
              name: null,
              ownerId: this.ownerId,
              memberIds:[this.selectedUser.id]
          }
          this.callCreateRoom(body);
      }else {
        this.toast.show("You have not selected a user", "warning");
      }
    } else{
      // Group chat
      if(this.selectedUserList.length > 1){
        if(this.roomNameForm.invalid){
          this.roomNameForm.control.markAllAsTouched();
          return;
        } else {
            const body: RoomRequest = {
                type: RoomType.GROUP,
                name: this.roomName,
                ownerId: this.ownerId,
                memberIds: this.selectedUserList.map(user => user.id)
            }
            this.callCreateRoom(body);
        }
      } else {
        this.toast.show("You must select at least two users for a group chat", "warning");
      }
    }
  }


  // GUI Methods
  addUserToSelected(item: UserNameAndAvatar) {
      if(item.id != this.ownerId) {
          this.selectedUserList.push(item);
      }
  }

  removeUserFromSelected(item: UserNameAndAvatar) {
    this.selectedUserList = this.selectedUserList.filter(user => user.id !== item.id);
  }

  isUserSelected(item: UserNameAndAvatar) {
    return this.selectedUserList.some(user => user.id === item.id);
  }

  selectUser(item: UserNameAndAvatar) {
      if(item.id != this.ownerId)
      {
          this.selectedUser = item;
      }
  }

  protected searchUsers() {
    if(!this.searchUserForm.invalid){
        this.callSearchUserFromService();
    } else {
        this.searchUserForm.control.markAllAsTouched();
    }
  }

  protected clearSearchForm() {
      this.searchUserForm.reset();
      this.searchModel = {
          firstName: '',
          lastName: '',
      }
  }


  protected callSearchUserFromService(){
      this.resultUserList = [];
      this.isUsersLoading = true;
      this.userService.searchUsersByFirstNameAndLastNameToChat(this.searchModel, this.paginator)
          .subscribe({
              next: response => {
                  if(response.status === 200 && response.body){
                      response.body.content.forEach(user => {
                          this.resultUserList.push({
                              id: user.id,
                              name: `${user.firstName} ${user.lastName}`,
                              avatar: user.avatar != null ? `data:image/jpeg;base64,${user.avatar}` : 'images/user_default_avatar.png',
                          })
                      })
                      this.paginator.currentPage = response.body.page.number;
                      this.paginator.totalItems = response.body.page.totalElements;
                  }
              }, error: error => {
                  console.log(error);
              }, complete: () => {
                  this.isUsersLoading = false;
              }
          })
  }


  protected callCreateRoom(room: RoomRequest){
      this.chatService.createRoom(room).subscribe({
          next: response => {
              if(response.status === 200 && response.body){
                  this.toast.show("You have successfully created!", "info");
                  this.resetModal();
              }
          }, error: error => {
              console.log(error);
              this.toast.show(`Ups... Something wrong ${error.status}`, "error");
          }
      })
  }

  protected changeRoomType(isSingleChat: boolean) {
      this.isSingleChat = isSingleChat;
      this.selectedUserList = [];
  }

  protected onPaginatorChanged($event: void) {
      this.callSearchUserFromService();
  }
}

