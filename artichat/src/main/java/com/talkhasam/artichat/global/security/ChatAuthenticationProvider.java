package com.talkhasam.artichat.global.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ChatAuthenticationProvider
        implements AuthenticationProvider {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public ChatAuthenticationProvider(CustomUserDetailsService uds,
                                      PasswordEncoder encoder) {
        this.userDetailsService = uds;
        this.passwordEncoder    = encoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        ChatAuthenticationToken token =
                (ChatAuthenticationToken) authentication;

        // ① loadUserByChatRoomAndNickname 메서드 구현
        UserDetails user = userDetailsService
                .loadUserByChatRoomAndNickname(
                        token.getChatRoomId(), token.getNickname());

        // ② password 검증 (null 허용 로직 포함)
        if (user.getPassword() != null) {
            if (!passwordEncoder.matches(
                    (String)token.getCredentials(), user.getPassword())) {
                throw new BadCredentialsException("비밀번호 불일치");
            }
        }

        // ③ 최종 AuthenticationToken 리턴
        return new ChatAuthenticationToken(
                user,
                null,
                user.getAuthorities()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ChatAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
