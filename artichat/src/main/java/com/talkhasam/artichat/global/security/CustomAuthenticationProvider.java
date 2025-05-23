package com.talkhasam.artichat.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private final CustomUserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) {
        CustomAuthenticationToken token = (CustomAuthenticationToken) authentication;
        UserDetails user = userDetailsService.loadUserByUsername(token.getUsername());
        return new CustomAuthenticationToken(user,null, user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return CustomAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
