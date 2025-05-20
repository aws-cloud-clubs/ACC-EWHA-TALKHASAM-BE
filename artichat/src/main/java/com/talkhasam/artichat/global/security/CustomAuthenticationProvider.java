package com.talkhasam.artichat.global.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final CustomUserDetailsService userDetailsService;

    public CustomAuthenticationProvider(CustomUserDetailsService uds,
                                        PasswordEncoder encoder) {
        this.userDetailsService = uds;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        CustomAuthenticationToken token =
                (CustomAuthenticationToken) authentication;

        UserDetails user = userDetailsService.loadUserById(token.getUserId());

        return new CustomAuthenticationToken(user,null, user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return CustomAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
