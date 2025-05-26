package com.talkhasam.artichat.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j

@Component
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends OncePerRequestFilter {

    private final CustomTokenService customTokenService;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            boolean valid = customTokenService.validateToken(token);

            if (valid) {
                String username = customTokenService.extractUsername(token);
                log.debug("Extracted username from token: {}", username);

                CustomUserDetails user = customUserDetailsService.loadUserByUsername(username);
                CustomAuthenticationToken auth = new CustomAuthenticationToken(user, null, user.getAuthorities(), user.isOwner());
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                log.warn("Invalid token detected");
            }
        } else {
            log.debug("No Bearer token found in Authorization header");
        }
        filterChain.doFilter(request, response);
    }
}
