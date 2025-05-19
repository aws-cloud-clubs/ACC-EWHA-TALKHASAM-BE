package com.talkhasam.artichat.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


public class CustomAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final AuthenticationManager authManager;

    public TokenAuthenticationFilter(TokenService tokenService,
                                     AuthenticationManager authManager) {
        this.tokenService = tokenService;
        this.authManager = authManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {
        String token = req.getHeader("X-AUTH-TOKEN");
        if (token != null && tokenService.validate(token)) {
            String username = tokenService.extractUsername(token);
            var authToken = new UsernamePasswordAuthenticationToken(username, null, null);
            authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(req)
            );
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        chain.doFilter(req, res);
    }
}
