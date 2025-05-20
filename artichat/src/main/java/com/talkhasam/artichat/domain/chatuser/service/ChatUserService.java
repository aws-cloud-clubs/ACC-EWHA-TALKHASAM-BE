package com.talkhasam.artichat.domain.chatuser.service;

import com.talkhasam.artichat.domain.chatroom.repository.ChatRoomRepository;
import com.talkhasam.artichat.domain.chatuser.dto.ChatUserLoginRequestDto;
import com.talkhasam.artichat.domain.chatuser.entity.ChatUser;
import com.talkhasam.artichat.domain.chatuser.repository.ChatUserRepository;
import com.talkhasam.artichat.global.security.JwtTokenService;
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
    private final ChatRoomRepository chatRoomRepository;
    private final PasswordEncoder encoder;
    private final JwtTokenService tokenService;

    // chatRoomId + nickname 으로 조회하고, 없으면 가입. 첫 가입자는 isOwner=true 로 표시합니다.
    public String loginOrRegister(ChatUserLoginRequestDto requestDto) {
        long chatRoomId = requestDto.chatRoomId();
        String nickname = requestDto.nickname();
        String encodedPassword = encoder.encode(requestDto.password());

        chatRoomRepository.findById(chatRoomId).get();
                //.orElseThrow(() -> new EntityNotFoundException("ChatRoom not found: " + chatRoomId))
        ChatUser chatUser = saveOrGet(chatRoomId, nickname, encodedPassword);
        String token = tokenService.generateToken(String.valueOf(chatUser.getId()));
        return token;
    }

    private ChatUser saveOrGet(long chatRoomId, String nickname, String encodedPassword) {
        // 기존 유저 조회
        Optional<ChatUser> existing = chatUserRepository.findByChatRoomIdAndNickname(chatRoomId, nickname);
        if (existing.isPresent()) {
            return existing.get();
        }

        // 첫 가입자 여부 계산
        boolean isOwner = chatUserRepository.countByChatRoomId(chatRoomId) == 0;

        // 새 사용자 생성
        ChatUser newUser = ChatUser.builder()
                .id(nextLong())
                .chatRoomId(chatRoomId)
                .nickname(nickname)
                .password(encodedPassword)
                .createdAt(Instant.now())
                .isOwner(isOwner)
                .build();
        return chatUserRepository.save(newUser);
    }
}