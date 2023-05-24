package com.maximilianwiegmann.backend.security.config;

import java.nio.file.AccessDeniedException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.maximilianwiegmann.backend.security.token.TokenRepository;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            return null;
        }
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            List<String> authorizationHeaders = accessor.getNativeHeader("Authorization");

            if (authorizationHeaders != null && !authorizationHeaders.isEmpty()) {
                String jwt = authorizationHeaders.get(0).replace("Bearer ", "");

                String username = jwtService.extractUsername(jwt);
                if (username != null && accessor.getUser() == null) {
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                    boolean isTokenValid = tokenRepository.findByToken(jwt)
                            .map(token -> !token.isExpired() && !token.isRevoked())
                            .orElse(false);
                    if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid && !jwtService.isTfaToken(jwt)) {
                        accessor.setUser(new UsernamePasswordAuthenticationToken(jwt, null));
                        return message;
                    }
                }
            }

            return null;
        }

        return message;
    }

}
