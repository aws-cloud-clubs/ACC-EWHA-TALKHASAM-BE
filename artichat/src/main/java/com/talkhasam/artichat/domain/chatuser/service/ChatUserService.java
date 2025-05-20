package com.talkhasam.artichat.domain.chatuser.service;

import com.talkhasam.artichat.domain.chatuser.entity.ChatUser;
import com.talkhasam.artichat.domain.chatuser.repository.ChatUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

import static com.talkhasam.artichat.global.util.TsidGenerator.nextLong;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatUserService {

    private final ChatUserRepository chatUserRepository;
    private final PasswordEncoder encoder;

    public ChatUser loginOrRegister(Long chatRoomId, String nickname, String rawPassword) {
        Optional<ChatUser> existing = chatUserRepository.findByChatRoomIdAndNickname(chatRoomId, nickname);
        // 기존 유저가 있는 경우
        if (existing.isPresent()) {
            return existing.get();
        }
        // 기존 유저가 없는 경우
        String encodedPassword = (rawPassword != null && !rawPassword.isBlank())
                ? encoder.encode(rawPassword)
                : null;

        ChatUser newUser = ChatUser.builder()
                .id(nextLong())
                .chatRoomId(chatRoomId)
                .nickname(nickname)
                .password(encodedPassword)
                .createdAt(Instant.now())
                .build();
        return chatUserRepository.save(newUser);
    }
}