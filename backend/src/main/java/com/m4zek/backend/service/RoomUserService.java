package com.m4zek.backend.service;


import com.m4zek.backend.exception.AccessDeniedException;
import com.m4zek.backend.exception.RoomNotFoundException;
import com.m4zek.backend.model.Message;
import com.m4zek.backend.model.RoomType;
import com.m4zek.backend.model.RoomUser;
import com.m4zek.backend.model.User;
import com.m4zek.backend.model.dto.write.RoomRequest;
import com.m4zek.backend.repository.RoomUserRepository;
import jakarta.persistence.EntityExistsException;
import org.springframework.stereotype.Service;

@Service
public class RoomUserService {

    private final RoomUserRepository repository;

    public RoomUserService(RoomUserRepository repository) {
        this.repository = repository;
    }


    public RoomUser findByRoomIdAndUserId(int roomId, int userId){
        return this.repository.findByRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new RoomNotFoundException("Room with given user not found"));
    }

    public void checkPrivateRoomExists(RoomRequest request){
        if(request.getType().equals(RoomType.PRIVATE)){
            int memberId = request.getMemberIds().size() == 1 ? request.getMemberIds().getFirst() : 0;
            this.repository.findPrivateConversation(request.getOwnerId(), memberId).ifPresent(r -> {
                throw new EntityExistsException("Room already exists");
            });
        }
    }


    public void hasAccessToRoom(int room_id, User user) throws AccessDeniedException {
        int logged_user_id = user.getId();

        if(!this.repository.existsByRoomIdAndUserId(room_id, logged_user_id)) {
            throw new AccessDeniedException("Access denied - You are not authorized to view this resource");
        }
    }


    public RoomUser updateLastMessageReadByUser(Message lastMessage, RoomUser roomUser) {
        roomUser.updateReadLastMessage(lastMessage);
        return this.repository.save(roomUser);
    }
}
