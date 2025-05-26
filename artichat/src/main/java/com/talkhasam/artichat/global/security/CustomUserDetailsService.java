package com.talkhasam.artichat.global.security;

import com.talkhasam.artichat.domain.chatuser.entity.ChatUser;
import com.talkhasam.artichat.domain.chatuser.repository.ChatUserRepository;
import com.talkhasam.artichat.global.exception.CustomException;
import com.talkhasam.artichat.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final ChatUserRepository chatUserRepository;

    public CustomUserDetails loadUserById(long userId) {
        ChatUser user = chatUserRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_ACCESS_TOKEN));
        return new CustomUserDetails(user);
    }
    @Override
    public CustomUserDetails loadUserByUsername(String username) {
        return loadUserById(Long.parseLong(username));
    }
}
