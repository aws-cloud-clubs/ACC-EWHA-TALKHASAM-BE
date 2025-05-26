package com.talkhasam.artichat.global.security;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomAuthenticationToken extends AbstractAuthenticationToken {

    @Getter
    private final String username;
    private final Object credentials;  // password or null
    @Getter
    private final boolean isOwner;

    public CustomAuthenticationToken(String username, Object credentials) {
        super(null);
        this.username = username;
        this.credentials = credentials;
        this.isOwner = false; // 기본값
        setAuthenticated(false);
    }

    // 인증 완료 후 사용될 생성자
    public CustomAuthenticationToken(UserDetails principal,
                                     Object credentials,
                                     Collection<? extends GrantedAuthority> authorities,
                                     boolean isOwner) {
        super(authorities);
        this.username = null;
        this.credentials = credentials;
        this.isOwner = isOwner;
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
