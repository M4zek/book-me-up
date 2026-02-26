package com.m4zek.backend.websocket;

import com.m4zek.backend.security.jwt.TokenManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements HandshakeInterceptor {


    private final static Logger log = LoggerFactory.getLogger(WebSocketAuthInterceptor.class);

    private final TokenManager tokenManager;


    // Method handling connect to ws. It should contain JSON Web Encryption.
    // If JWE is correct adding email to attributes
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        String token = null;

        try{
            if(request instanceof ServletServerHttpRequest servletRequest){
                HttpServletRequest httpServletRequest = servletRequest.getServletRequest();
                token = httpServletRequest.getParameter("token");
            }

            if(token !=null && tokenManager.validateToken(token)){
                String email = tokenManager.getEmailFromToken(token);
                attributes.put("principal", email);
                log.info("Session has been created for WebSocket for user " + email);
                return true;
            }
        } catch (Exception e){
            log.error(e.getMessage());
        }
        return false;
    }


    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }
}
