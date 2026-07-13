package com.m4zek.backend.service;

import com.m4zek.backend.exception.RoomNotFoundException;
import com.m4zek.backend.model.*;
import com.m4zek.backend.model.dto.write.RoomRequest;
import com.m4zek.backend.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class RoomService {

    private final static Logger logger = LoggerFactory.getLogger(RoomService.class);

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public Room findByIdOrThrow(int id){
        return this.roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException("Room not found"));
    }

    public Room createAndSaveRoom(User owner, Set<User> members, RoomRequest request){
        // Create room
        Room room = createRoom(request, owner, members);

        // Save room in db
        room = this.save(room);

        // Return room
        return room;
    }


    /*
        ==== PRIVATE METHODS ====
     */

    private Room createRoom(RoomRequest request, User owner, Set<User> members){
        // Create room entity
        Room room = new Room();
        room.setType(request.getType());
        room.setName(request.getType().equals(RoomType.GROUP) ? request.getName() : null);

        // Generate all members in room With roles
        List<RoomUser> roomUsers = new ArrayList<>();
        // Add owner to list
        roomUsers.add(new RoomUser(room, owner, RoomRole.OWNER));
        // Add all members
        members.forEach(member -> roomUsers.add(new RoomUser(room, member, RoomRole.MEMBER)));

        // Assign all members to room
        room.addMembers(roomUsers);

        return room;
    }

    public Room save(Room room){
        logger.info("Room [{}] has been saved", room.getId());
        return this.roomRepository.save(room);
    }


    public Page<Room> findUserRooms(int userId, Pageable pageable) {
        return this.roomRepository.findAllByUserIdAndSortByLastMessage(userId, pageable);
    }
}
