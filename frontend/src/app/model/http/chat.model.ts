export enum MessageType{
    MESSAGE = 'MESSAGE',
    RECEIPT = 'RECEIPT',
}

export enum NotificationType {
    CHAT = 'CHAT',
}

export enum RoomType{
    PRIVATE = 'PRIVATE',
    GROUP = 'GROUP',
}

export enum RoomRole{
    OWNER = 'OWNER',
    MEMBER = 'MEMBER',
}

export interface Member{
    id: number,
    firstName: string,
    lastName: string,
    avatar: string | null,
    role: RoomRole,
}

export interface RoomRequest{
    type: RoomType,
    name: string | null,
    ownerId: number,
    memberIds: number[],
}

export interface RoomResponse{
    id: number,
    name: string,
    memberProjections: Member[],
    roomType: RoomType,
    lastMessage: ChatMessageResponse,
    numOfUnreadMessages: number,
}

export interface ChatMessageRequest{
    room_id: number,
    content: string,
    type:  MessageType,
}

export interface ChatMessageResponse{
    id: number,
    content: string,
    sender: Member,
    type: MessageType,
    createdDate: string,
    readBy: Member[],
}

export interface ChatMessageReceipt{
    room_id: number,
    message_id: number,
    reader_id: number,
    type: MessageType,
}

export type WebSocketChatData = | {
    type: MessageType.MESSAGE;
    message: ChatMessageResponse;
} | {
    type: MessageType.RECEIPT;
    receipt: ChatMessageReceipt;
};


export type WebSocketNotification = | {
    type: NotificationType,
    room: RoomResponse
};