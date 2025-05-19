package com.talkhasam.artichat.domain.chatuser.service;

import com.talkhasam.artichat.domain.chatuser.entity.ChatUser;
import com.talkhasam.artichat.domain.chatuser.repository.ChatUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatUserService {

    private final ChatUserRepository repo;
    private final PasswordEncoder encoder;

    public ChatUser loginOrRegister(String chatRoomId, String nickname, String rawPassword) {
        Optional<ChatUser> existing = repo.findByChatRoomIdAndNickname(chatRoomId, nickname);
        // 기존 유저가 있는 경우
        if (existing.isPresent()) {
            return existing.get();
        }
        // 기존 유저가 없는 경우
        String pwd = (rawPassword != null && !rawPassword.isBlank())
                ? encoder.encode(rawPassword)
                : null;

        ChatUser newUser = new ChatUser();
        newUser.setChatRoomId(chatRoomId);
        newUser.setChatUserId(UUID.randomUUID().toString());
        newUser.setNickname(nickname);
        newUser.setPassword(pwd);
        newUser.setCreatedAt(Instant.now());

        return repo.save(newUser);
    }
}