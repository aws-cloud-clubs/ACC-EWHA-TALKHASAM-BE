package com.talkhasam.artichat.global.security;

import com.talkhasam.artichat.domain.chatuser.entity.ChatUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {
    private final ChatUser user;

    public CustomUserDetails(ChatUser user) {
        this.user = user;
    }

    public boolean isOwner() {
        return user.isOwner();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return String.valueOf(user.getId());
    }

    // 기타 UserDetails 메서드 오버라이딩 생략
}
