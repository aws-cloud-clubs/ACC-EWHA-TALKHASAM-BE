package com.talkhasam.artichat.domain.chatuser.service;

import com.talkhasam.artichat.domain.chatroom.repository.ChatRoomRepository;
import com.talkhasam.artichat.domain.chatuser.dto.ChatUserLoginRequestDto;
import com.talkhasam.artichat.domain.chatuser.entity.ChatUser;
import com.talkhasam.artichat.domain.chatuser.repository.ChatUserRepository;
import com.talkhasam.artichat.global.security.JwtTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

import static com.talkhasam.artichat.global.util.TsidGenerator.nextLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatUserService {
    private final ChatUserRepository chatUserRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final PasswordEncoder encoder;
    private final JwtTokenService tokenService;

    /**
     * 로그인 또는 회원가입 후 JWT 토큰 반환
     */
    public String loginOrRegister(ChatUserLoginRequestDto requestDto) {
        long chatRoomId = requestDto.chatRoomId();
        String nickname = requestDto.nickname();
        String rawPassword = requestDto.password();

        log.info("Start loginOrRegister for chatRoomId={}, nickname={}", chatRoomId, nickname);

        // 채팅방 존재 확인
        chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> {
                    log.error("ChatRoom not found: {}", chatRoomId);
                    return new IllegalArgumentException("ChatRoom not found: " + chatRoomId);
                });
        log.info("ChatRoom {} exists", chatRoomId);

        // 로그인 또는 신규 회원가입 처리
        ChatUser chatUser = saveOrGet(chatRoomId, nickname, rawPassword);
        if (chatUser == null) {
            log.warn("Login failed for nickname={} in chatRoomId={}", nickname, chatRoomId);
            return null;
        }

        // 토큰 생성
        String token = tokenService.generateToken(String.valueOf(chatUser.getId()));
        log.info("Generated token for user id={}", chatUser.getId());
        return token;
    }

    /**
     * 기존 유저 조회 후 비밀번호 인증, 없으면 신규 생성
     */
    private ChatUser saveOrGet(long chatRoomId, String nickname, String rawPassword) {
        log.info("Checking existing user for chatRoomId={}, nickname={}", chatRoomId, nickname);
        Optional<ChatUser> existingUser = chatUserRepository.findByChatRoomIdAndNickname(chatRoomId, nickname);
        if (existingUser.isPresent()) {
            ChatUser user = existingUser.get();
            if (encoder.matches(rawPassword, user.getPassword())) {
                log.info("Existing user login succeeded: id={}", user.getId());
                return user;
            } else {
                log.warn("Password mismatch for user id={}", user.getId());
                return null;
            }
        }

        boolean isOwner = chatUserRepository.countByChatRoomId(chatRoomId) == 0;
        log.info("No existing user found, creating new one (isOwner={})", isOwner);

        ChatUser newUser = ChatUser.builder()
                .id(nextLong())
                .chatRoomId(chatRoomId)
                .nickname(nickname)
                .password(encoder.encode(rawPassword))
                .createdAt(Instant.now())
                .isOwner(isOwner)
                .build();
        ChatUser saved = chatUserRepository.save(newUser);
        log.info("New user created: id={} chatRoomId={} nickname={}", saved.getId(), chatRoomId, nickname);
        return saved;
    }
}
