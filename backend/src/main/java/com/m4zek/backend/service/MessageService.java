package com.m4zek.backend.service;


import com.m4zek.backend.exception.RoomNotFoundException;
import com.m4zek.backend.model.Message;
import com.m4zek.backend.model.Room;
import com.m4zek.backend.model.User;
import com.m4zek.backend.model.dto.write.ChatMessageRequest;
import com.m4zek.backend.repository.ChatMessageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    private final ChatMessageRepository chatRepository;

    public MessageService(ChatMessageRepository chatRepository) {
        this.chatRepository = chatRepository;
    }


    public Message createAndSaveMessage(ChatMessageRequest message, Room room, User sender){
        Message msg = new Message(
                message.getType(),
                message.getContent(),
                room,
                sender
        );
        room.addMessage(msg);

        msg = this.chatRepository.save(msg);

        return msg;
    }


    public Message save(Message message){
        return this.chatRepository.save(message);
    }

    public Message findLastMessageInRoom(int roomId) {
        return this.chatRepository.findLastMessageInRoom(roomId)
                .orElseThrow(() -> new RoomNotFoundException("Last message in room not found"));
    }

    public Page<Message> findMessagesInRoom(int roomId, Pageable pageable){
        return this.chatRepository.findAllByRoomId(roomId, pageable);
    }

}
