package com.m4zek.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.m4zek.backend.exception.AccessDeniedException;
import com.m4zek.backend.model.dto.write.ChatMessageRequest;
import com.m4zek.backend.security.jwt.TokenManager;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Objects;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final static Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

    private final WebSocketAuthInterceptor authInterceptor;
    private final TokenManager tokenManager;
    private final WebSocketChatGuard guard;
    private final ObjectMapper objectMapper;


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .addInterceptors(authInterceptor)
                .withSockJS();
    }


    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                try {


                    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                        String token = accessor.getFirstNativeHeader("Authorization");

                        if (token == null || !tokenManager.validateToken(token)) {
                            throw new JwtException("Invalid token");
                        }

                        accessor.setUser(() -> tokenManager.getEmailFromToken(token));
                    }


                    if(StompCommand.SEND.equals(accessor.getCommand())) {
                        try{
                            String token = accessor.getFirstNativeHeader("Authorization");

                            if (token == null || !tokenManager.validateToken(token)) {
                                throw new JwtException("Invalid token");
                            }

                            // Read message
                            byte[] payload = (byte[]) message.getPayload();
                            String json = new String(payload, StandardCharsets.UTF_8);

                            ChatMessageRequest chatMessageRequest = objectMapper.readValue(json, ChatMessageRequest.class);

                            // Get room if from message
                            int room_id = chatMessageRequest.getRoom_id();

                            accessor.setUser(() -> tokenManager.getEmailFromToken(token));

                            Principal principal = accessor.getUser();

                            // If email is empty throw access denied
                            if(principal.getName() == null){
                                throw new AccessDeniedException("Access denied - Sender not found");
                            }

                            // If logged user doesn't have access to the room throw access denied
                            try {
                                guard.loggedUserHasAccessToRoom(room_id, principal);
                            } catch (AccessDeniedException e) {
                                throw e;
                            }

                        } catch (Exception e) {
                            logger.error(e.getMessage());
                            throw new AccessDeniedException(e.getMessage());
                        }
                    }


                    if(StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                        String token = accessor.getFirstNativeHeader("Authorization");

                        if (token == null || !tokenManager.validateToken(token)) {
                            throw new JwtException("Invalid token");
                        }

                        int room_id = Integer.parseInt(Objects.requireNonNull(accessor.getDestination()).replace("/topic/room/", ""));
                        Principal principal = accessor.getUser();

                        accessor.setUser(() -> tokenManager.getEmailFromToken(token));

                        if(principal.getName() == null){
                            throw new AccessDeniedException("Access denied - Sender not found");
                        }

                        try {
                            guard.loggedUserHasAccessToRoom(room_id, principal);
                        } catch (AccessDeniedException e) {
                            logger.error(e.getMessage());
                            throw e;
                        }
                    }
                    return message;
                } catch (Exception e) {
                    throw e;
                }
            }
        });
    }
}
