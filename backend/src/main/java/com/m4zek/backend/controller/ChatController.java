package com.m4zek.backend.controller;


import com.m4zek.backend.model.dto.read.MessageReadReceipt;
import com.m4zek.backend.model.dto.write.ChatMessageRequest;
import com.m4zek.backend.service.facade.ChatFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatController {

    private final static Logger logger = LoggerFactory.getLogger(ChatController.class);

    private final ChatFacade chatFacade;

    public ChatController(ChatFacade chatFacade) {
        this.chatFacade = chatFacade;
    }

    @MessageMapping("/room.send")
    public void sendMessage(ChatMessageRequest message, Principal principal) {
        chatFacade.sendMessage(message, principal);
    }

    @MessageMapping("/room.read")
    public void readMessage(MessageReadReceipt message, Principal principal) {
        chatFacade.markLastMessageAsRead(message, principal);
    }

}
