package com.talkhasam.artichat.global.security;

import com.talkhasam.artichat.domain.chatuser.entity.ChatUser;
import com.talkhasam.artichat.domain.chatuser.repository.ChatUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final ChatUserRepository chatUserRepository;

    public UserDetails loadUserById(long userId) {
        ChatUser user = chatUserRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(
                "사용자 없음: " + userId));
        return User.withUsername(String.valueOf(user.getId())) // principal
                .password(user.getPassword())
                .roles("USER")
                .build();
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        throw new UnsupportedOperationException("이 메서드는 사용되지 않습니다.");
    }
}
