package com.m4zek.backend.controller;


import com.m4zek.backend.model.dto.read.ChatMessageResponse;
import com.m4zek.backend.model.dto.read.RoomResponse;
import com.m4zek.backend.model.dto.write.RoomRequest;
import com.m4zek.backend.service.facade.ChatFacade;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@Validated
public class ChatRestController {

    private final ChatFacade chatFacade;

    public ChatRestController(ChatFacade chatFacade) {
        this.chatFacade = chatFacade;
    }

    @PostMapping("/chat/room")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<RoomResponse> createRoom(@RequestBody @Valid RoomRequest roomRequest) {
        RoomResponse roomResponse = this.chatFacade.createRoom(roomRequest);
        return ResponseEntity.ok(roomResponse);
    }

    @GetMapping("/chat/rooms/{room_id}/messages")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Page<ChatMessageResponse>> getAllRoomMessages(@PathVariable int room_id, Pageable pageable) {
        Page<ChatMessageResponse> messages = this.chatFacade.readMessagesFromRoom(room_id,pageable);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/user/{user_id}/rooms")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Page<RoomResponse>> getAllRooms(@PathVariable int user_id, Pageable pageable) {
        Page<RoomResponse> rooms = this.chatFacade.readUserRoom(user_id, pageable);
        return ResponseEntity.ok(rooms);
    }

}
