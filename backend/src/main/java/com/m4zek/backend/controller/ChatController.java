package com.m4zek.backend.controller;


import com.m4zek.backend.model.dto.write.ChatMessageRequest;
import com.m4zek.backend.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatController {

    private final static Logger logger = LoggerFactory.getLogger(ChatController.class);

    private final ChatService chatService;


    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @MessageMapping("/room.send")
    public void sendMessage(ChatMessageRequest message, Principal principal) {
        chatService.sendMessage(message, principal);
    }


}
