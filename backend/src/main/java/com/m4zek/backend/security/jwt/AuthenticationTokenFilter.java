package com.m4zek.backend.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.m4zek.backend.advice.ErrorMessage;
import com.m4zek.backend.security.service.MyUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;


@Component
public class AuthenticationTokenFilter extends OncePerRequestFilter {
    public static final Logger logger = LoggerFactory.getLogger(AuthenticationTokenFilter.class);

    private final TokenManager tokenManager;

    private final MyUserDetailsService myUserDetailsService;

    public AuthenticationTokenFilter(TokenManager tokenManager, MyUserDetailsService myUserDetailsService) {
        this.tokenManager = tokenManager;
        this.myUserDetailsService = myUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = tokenManager.parseJwt(request);
            if(token != null && tokenManager.validateToken(token)){
                String email = tokenManager.getEmailFromToken(token);
                UserDetails userDetails = myUserDetailsService.loadUserByUsername(email);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e){
            logger.error("User cannot be authenticated: {}", e.getMessage());
            handleJwtExpiredException(request, response, e);
        } catch (JwtException ex){
            handleJwtException(request, response, ex);
        }
    }


    private void handleJwtExpiredException(HttpServletRequest request, HttpServletResponse response, ExpiredJwtException ex) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ErrorMessage errorMessage = new ErrorMessage(
                HttpStatus.UNAUTHORIZED.value(),
                new Date(),
                String.format("JWT has expired at [%s]", new Date(ex.getClaims().getExpiration().getTime())),
                "Refresh the token or log in again"
        );

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(errorMessage);

        response.setContentType("application/json");
        response.getWriter().write(jsonResponse);
    }

    private void handleJwtException(HttpServletRequest request, HttpServletResponse response, JwtException e) throws IOException{
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        ErrorMessage errorMessage = new ErrorMessage(
                HttpStatus.UNAUTHORIZED.value(),
                new Date(),
                "Wrong token format",
                "Please check the token"
        );
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(errorMessage);

        response.setContentType("application/json");
        response.getWriter().write(jsonResponse);
    }
}
