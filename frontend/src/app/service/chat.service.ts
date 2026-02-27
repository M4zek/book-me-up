import {Injectable} from '@angular/core';
import {UserContextService} from "./user-context.service";
import {HttpClient, HttpParams} from "@angular/common/http";
import {ChatMessageResponse, RoomRequest, RoomResponse} from "../model/http/chat.model";
import {Page, Pagination} from "../model/search/search.model";

@Injectable({
  providedIn: 'root'
})
export class ChatService {

    user_id: number | null = null;

    constructor(private userContext: UserContextService, private http: HttpClient) {
        this.userContext.getUserContext().subscribe({
            next: (user) => {
                this.user_id = user.id;
            }
        });
    }


    getUserRooms(pagination: Pagination){
        let url = `/api/v1/user/${this.user_id}/rooms`;

        let httpParams = new HttpParams()
            .set('page', pagination.currentPage)
            .set('size', pagination.itemsPerPage);

        return this.http.get<Page<RoomResponse>>(url, {params: httpParams, observe: 'response'});
    }

    getRoomMessages(roomId:number, pagination: Pagination){
        let url = `/api/v1/chat/rooms/${roomId}/messages`;

        let httpParams = new HttpParams()
            .set('page', pagination.currentPage)
            .set('size', pagination.itemsPerPage);

        return this.http.get<Page<ChatMessageResponse>>(url, {params: httpParams, observe: 'response'});
    }


    createRoom(room: RoomRequest){
        const url = '/api/v1/chat/room';
        return this.http.post<RoomResponse>(url, room, { observe: 'response'});
    }




}
