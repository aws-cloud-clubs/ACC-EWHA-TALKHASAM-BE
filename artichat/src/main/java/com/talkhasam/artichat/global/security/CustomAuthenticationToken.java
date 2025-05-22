package com.talkhasam.artichat.global.security;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomAuthenticationToken extends AbstractAuthenticationToken {

    @Getter
    private final Long userId;
    private final Object credentials;  // password or null

    public CustomAuthenticationToken(long userId, Object credentials) {
        super(null);
        this.userId = userId;
        this.credentials = credentials;
        setAuthenticated(false);
    }

    // 인증 완료 후 사용될 생성자
    public CustomAuthenticationToken(UserDetails principal,
                                     Object credentials,
                                     Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.userId = null;
        this.credentials = credentials;
        setAuthenticated(true);
        super.setDetails(principal);
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return super.getDetails();
    }
}
