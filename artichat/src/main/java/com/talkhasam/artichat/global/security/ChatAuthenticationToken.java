package com.talkhasam.artichat.global.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class ChatAuthenticationToken extends AbstractAuthenticationToken {

    private final String chatRoomId;
    private final String nickname;
    private Object credentials;  // password or null

    public ChatAuthenticationToken(String chatRoomId,
                                   String nickname,
                                   Object credentials) {
        super(null);
        this.chatRoomId = chatRoomId;
        this.nickname   = nickname;
        this.credentials = credentials;
        setAuthenticated(false);
    }

    // 인증 완료 후 사용될 생성자
    public ChatAuthenticationToken(UserDetails principal,
                                   Object credentials,
                                   Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.chatRoomId   = null;  // principal 에 포함시켜도 무방
        this.nickname     = null;
        this.credentials  = credentials;
        setAuthenticated(true);
        super.setDetails(principal);
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return super.getDetails();  // 또는 UserDetails
    }

    // 추가 getter
    public String getChatRoomId() { return chatRoomId; }
    public String getNickname()   { return nickname; }
}
