import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges, ViewChild} from '@angular/core';
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {SearchAndSortBarComponent} from "../../search-bar/search-and-sort-bar.component";
import {PaginatorComponent} from "../../paginator/paginator.component";
import {FormsModule, NgForm} from "@angular/forms";
import {UserNameAndAvatar} from "../../../model/gui/gui.model";
import {ToastService} from "../../../service/toast.service";
import {Pagination} from "../../../model/search/search.model";


@Component({
  selector: 'app-create-chat-room-modal',
  imports: [
    NgIf,
    SearchAndSortBarComponent,
    PaginatorComponent,
    NgForOf,
    FormsModule,
    NgClass
  ],
  templateUrl: './create-chat-room-modal.component.html',
  styleUrl: './create-chat-room-modal.component.css'
})
export class CreateChatRoomModalComponent implements OnChanges {
  @ViewChild('roomNameForm') roomNameForm!: NgForm;

  resultUserList: UserNameAndAvatar[] = []
  selectedUser: UserNameAndAvatar | null = null;
  selectedUserList: UserNameAndAvatar[] = []

  roomName: string = '';
  isSingleChat: boolean = true;

  paginator: Pagination = {
    totalItems: 100,
    itemsPerPage: 5,
    currentPage: 1,
    itemsPerPageOptions: [5, 10, 15, 25, 50]
  }

  @Input() isVisible: boolean = false;
  @Output() closeModal = new EventEmitter<void>();

  constructor(private toast: ToastService) {
  }

  ngOnChanges(changes: SimpleChanges): void {
      if (changes['isVisible']) {
        if(this.isVisible) {
          this.createList(10)
        }
      }
  }

  // Tmp method
  createList(max: number){
    for (let i = 1; i < max; i++) {
      this.resultUserList.push({
        id: i,
        name: 'John Doe',
        avatar: 'images/user_default_avatar.png'
      })
    }
  }

  close() {
    this.resetModal()
    this.closeModal.emit();
  }

  resetModal() {
    this.resultUserList = [];
    this.selectedUserList = [];
    this.selectedUser = null;
  }

  confirm(){
    if(this.isSingleChat){
      // Single user chat
      if(this.selectedUser != null){
        // Send request to backend
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
          // Send request to backend
        }
      } else {
        this.toast.show("You must select at least two users for a group chat", "warning");
      }
    }
  }

  addUserToSelected(item: UserNameAndAvatar) {
    this.selectedUserList.push(item);
  }

  removeUserFromSelected(item: UserNameAndAvatar) {
    this.selectedUserList = this.selectedUserList.filter(user => user.id !== item.id);
  }

  isUserSelected(item: UserNameAndAvatar) {
    return this.selectedUserList.some(user => user.id === item.id);
  }

  selectUser(item: UserNameAndAvatar) {
    this.selectedUser = item;
  }

}

