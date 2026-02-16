package com.m4zek.backend.security;


import com.m4zek.backend.exception.AccessDeniedException;
import com.m4zek.backend.model.User;
import com.m4zek.backend.repository.RoomUserRepository;
import com.m4zek.backend.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
public class WebSocketChatGuard {

    private final RoomUserRepository roomUserRepository;
    private final UserRepository userRepository;


    public WebSocketChatGuard(RoomUserRepository roomUserRepositor, UserRepository userRepository) {
        this.roomUserRepository = roomUserRepositor;
        this.userRepository = userRepository;
    }


    public void loggedUserHasAccessToRoom(int room_id, Principal principal) throws AccessDeniedException {
        String email = principal.getName();

        User user = this.userRepository.findByAddressEmail(email)
                .orElseThrow(() -> new AccessDeniedException("Access denied - Sender not found  "));

        if(!this.roomUserRepository.existsByRoomIdAndUserId(room_id, user.getId())){
            throw new AccessDeniedException("Access denied " + email + " - You are not authorized to view this resource");
        }
    }



}
