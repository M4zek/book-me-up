package com.m4zek.backend.security.listener;

import com.m4zek.backend.model.LoginHistory;
import com.m4zek.backend.repository.LoginHistoryRepository;
import com.m4zek.backend.security.service.MyUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.ForwardedHeaderFilter;

@Component
public class AuthenticationListener {

    private final LoginHistoryRepository repository;
    private final HttpServletRequest request;


    public AuthenticationListener(LoginHistoryRepository repository, HttpServletRequest request) {
        this.repository = repository;
        this.request = request;
    }

    @Bean
    public FilterRegistrationBean<ForwardedHeaderFilter> forwardedHeaderFilter() {
        FilterRegistrationBean<ForwardedHeaderFilter> filter = new FilterRegistrationBean<>(new ForwardedHeaderFilter());
        filter.setOrder(0);
        return filter;
    }

    @EventListener
    public void onSuccessfulAuthentication(AuthenticationSuccessEvent event) {
        MyUserDetails userDetails = (MyUserDetails) event.getAuthentication().getPrincipal();
        String ipAddress = getAddressIp();
        String userAgent = getUserAgent();


        LoginHistory loginHistory = LoginHistory.builder()
                .userId(userDetails.getId())
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .email(userDetails.getEmail())
                .success(true)
                .build();

        repository.save(loginHistory);
    }


    @EventListener
    public void onFailureAuthentication(AbstractAuthenticationFailureEvent event) {
        String email = event.getAuthentication().getPrincipal().toString();
        String failureMessage = event.getException().getMessage();
        String ipAddress = getAddressIp();
        String userAgent = getUserAgent();

        LoginHistory loginHistory = LoginHistory.builder()
                .email(email)
                .success(false)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .failureReason(failureMessage)
                .build();

        repository.save(loginHistory);
    }


    private String getAddressIp(){
        String ip = request.getRemoteAddr();
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            ip = xff.split(",")[0].trim();
        }
        return ip;
    }

    private String getUserAgent(){
        return request.getHeader("User-Agent");
    }
}
