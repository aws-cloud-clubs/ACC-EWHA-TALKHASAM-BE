package com.talkhasam.artichat.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talkhasam.artichat.domain.chatuser.dto.ChatUserLoginRequestDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authManager;
    private final TokenService tokenService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LoginFilter(AuthenticationManager authManager,
                       TokenService tokenService) {
        this.authManager   = authManager;
        this.tokenService  = tokenService;
        setFilterProcessesUrl("/chatuser/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) {
        try {
            ChatUserLoginRequestDto reqDto = objectMapper
                    .readValue(req.getInputStream(), ChatUserLoginRequestDto.class);

            ChatAuthenticationToken authToken = new ChatAuthenticationToken(
                    reqDto.chatRoomId(),
                    reqDto.nickname(),
                    reqDto.password()
            );

            return authManager.authenticate(authToken);
        } catch (IOException e) {
            throw new AuthenticationServiceException("로그인 요청 파싱 실패", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) {
        String token = tokenService.generate(auth.getName());
        res.setHeader("X-AUTH-TOKEN", token);
    }
}
