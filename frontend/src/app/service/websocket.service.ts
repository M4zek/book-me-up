import {Injectable} from '@angular/core';
import {BehaviorSubject} from "rxjs";
import {UserContextService} from "./user-context.service";
import {Client, IMessage, StompSubscription} from '@stomp/stompjs';
import SockJS from "sockjs-client/dist/sockjs";
import {
    ChatMessageReceipt,
    ChatMessageRequest,
    ChatMessageResponse,
    MessageType,
    NotificationType,
    RoomResponse,
    WebSocketChatData,
    WebSocketNotification
} from "../model/http/chat.model";
import {AuthService} from "./auth.service";
import {environment} from "../../environments/environment.development";


@Injectable({
  providedIn: 'root'
})
export class WebsocketService {

    private KEY_CHAT_NOTIFICATION: string = "chatNotification";
    private client!: Client;

    messageSubject$ = new BehaviorSubject<WebSocketChatData | null>(null);
    notificationSubject$ = new BehaviorSubject<WebSocketNotification | null>(null);
    connectStatus$ = new BehaviorSubject<boolean>(false);


    private ws_url: string = environment.wsUrl;
    private token: string = '';
    private loggedUserId: number | null = null;
    private subscriptions: Map<string, StompSubscription> = new Map();

    constructor(private ucs: UserContextService, private authService: AuthService) {
        // Handle log in user and connect to ws
        this.ucs.getAuthState().subscribe(state => {
            if (state.isLoggedIn && state.userContext) {
                this.token = state.userContext.token;
                this.loggedUserId = state.userContext.id;
                this.connect();
            } else {
                this.disconnect();
                this.loggedUserId = null
                this.token = '';
            }
        })

        this.ucs.getUserToken().subscribe(token => {
            this.token = token;
        })
    }


    // Connect to ws method
    connect(): void {
        const MAX_RECONNECTS = 10;
        let reconnectAttempts = 0;

        if(!this.client && this.token !== '') {
            this.client = new Client({

                webSocketFactory: () => new SockJS(`${this.ws_url}?token=${this.token}`, undefined, { transports: ['websocket'] }),

                // debug: (str: string) => {console.log(str);},

                beforeConnect: async () => {
                    const currentToken = this.token;

                    this.client.brokerURL = `${this.ws_url}?token=${currentToken}`;
                    this.client.connectHeaders = {
                        Authorization: currentToken
                    };

                },

                reconnectDelay: 1500 // Time between recon attempts - 5sec
            })

            // Try reconnect
            this.client.onWebSocketClose = () => {
                reconnectAttempts++;
                if(reconnectAttempts >= MAX_RECONNECTS) {
                    this.client.deactivate().then(r => {
                        console.log("Connection closed permanently")
                        this.connectStatus$.next(false);
                        this.ucs.deleteUserFromStorage();
                    });
                }
            }

            // Connect success handle
            this.client.onConnect = () => {
                reconnectAttempts = 0;
                this.connectStatus$.next(true);

                const keys = Array.from(this.subscriptions.keys()).filter((key) => key !== "chatNotification");
                this.subscriptions.clear();

                this.subscribeChatNotification();

                keys.forEach((key) => {
                    let room_id = parseInt(key);
                    if(!isNaN(room_id)) {
                        this.subscribeRoom(room_id);
                    }
                })
                console.info("Connected to Server");
            }


            // Error Handling
            this.client.onStompError = (frame) => {
                console.error("STOMP ERROR", frame);
                this.connectStatus$.next(false);
                this.refreshUserSession();
            };


            this.client.onWebSocketError = (evt) => {
                console.error("WS ERROR", evt);
            };

            this.client.activate();

        } else {
            console.error("Websocket not connected");
            this.connectStatus$.next(false);
        }
    }

    disconnect(): void {
        if(this.client) {
            this.subscriptions.forEach(subscription => {
                subscription.unsubscribe();
            })

            this.subscriptions.clear();

            this.client.deactivate()
                .then(r => console.log("Connection disconnected"))
                .catch(e => console.error(e));

            this.client = undefined as any;
            this.messageSubject$.next(null);
        }
    }

    // Send message
    sendMessage(text: string, roomId: number): void {
        const dest = '/app/room.send'
        const msg: ChatMessageRequest = {
            room_id: roomId,
            content: text,
            type: MessageType.MESSAGE
        }
        this.client.publish({
            destination: dest,
            headers: {"Authorization": this.token},
            body: JSON.stringify(msg)
        });
    }

    // Connect to the room and listen any massage
    subscribeRoom(roomId: number): void {
        if(this.client.connected) {
            let url = `/topic/room/${roomId}`;
            let sub = this.client.subscribe(url, (message: IMessage) => {
               const payload = JSON.parse(message.body);
               switch (payload.type) {
                   case MessageType.MESSAGE:
                       const msg: ChatMessageResponse = payload;
                       this.messageSubject$.next({type: MessageType.MESSAGE, message: msg});
                       this.sendReceiptForMessage(roomId, msg.id);
                       break;

                   case MessageType.RECEIPT:
                       const rc: ChatMessageReceipt = payload;
                       this.messageSubject$.next({type: MessageType.RECEIPT, receipt: rc});
                       break;
               }
            }, {"Authorization": this.token});

            this.subscriptions.set(roomId.toString(), sub);
        }
    }


    // Disconnect from the room by id
    unsubscribeRoom(roomId: number): void {
        let sub = this.subscriptions.get(roomId.toString());
        if (sub) {
            sub.unsubscribe({"destination": `/topic/room/${roomId}`});
            this.subscriptions.delete(roomId.toString());
        }
    }


    // Connect to chat notification
    subscribeChatNotification(): void {
        let url = '/user/queue/notification/chat';

        let sub = this.client.subscribe(url, (message: IMessage) => {
            const roomResponse: RoomResponse = JSON.parse(message.body);

            this.notificationSubject$.next({type: NotificationType.CHAT,room: roomResponse});

        }, {"Authorization": this.token});

        this.subscriptions.set(this.KEY_CHAT_NOTIFICATION, sub);
    }

    // Disconnect from chat notification
    unsubscribeChatNotification(): void {
        let sub = this.subscriptions.get(this.KEY_CHAT_NOTIFICATION);
        if (sub) {
            sub.unsubscribe();
            this.subscriptions.delete(this.KEY_CHAT_NOTIFICATION);
        }
    }

    // Send information about reading message jus arrive
    sendReceiptForMessage(roomId: number, messageId: number | null = null): void {
        let url = `/app/room.read`;
        if(this.client.connected) {
            this.client.publish({
                destination: url,
                headers: {"Authorization": this.token},
                body: JSON.stringify({
                    room_id: roomId,
                    message_id: messageId,
                    reader_id: this.loggedUserId,
                    type: MessageType.RECEIPT
                })
            })
        }
    }



    private refreshUserSession(): void {
        this.authService.refreshToken().subscribe(response => {
            if (response) {
                this.ucs.updateTokens(response.accessToken, response.refreshToken);
                this.token = response.accessToken;
            }
        })
    }

}
