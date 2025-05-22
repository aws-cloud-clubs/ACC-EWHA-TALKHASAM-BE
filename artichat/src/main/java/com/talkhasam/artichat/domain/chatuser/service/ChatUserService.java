package com.talkhasam.artichat.domain.chatuser.service;

import com.talkhasam.artichat.domain.chatroom.repository.ChatRoomRepository;
import com.talkhasam.artichat.domain.chatuser.dto.ChatUserLoginRequestDto;
import com.talkhasam.artichat.domain.chatuser.entity.ChatUser;
import com.talkhasam.artichat.domain.chatuser.repository.ChatUserRepository;
import com.talkhasam.artichat.global.exception.CustomException;
import com.talkhasam.artichat.global.exception.ErrorCode;
import com.talkhasam.artichat.global.security.CustomTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final CustomTokenService tokenService;

    // 로그인 또는 회원가입 후 JWT 토큰 반환
    public String loginOrRegister(ChatUserLoginRequestDto requestDto) {
        long chatRoomId = requestDto.chatRoomId();
        // 채팅방 존재 확인
        chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));
        // 로그인 또는 신규 회원가입 처리
        ChatUser chatUser = saveOrGet(chatRoomId, requestDto.nickname(), requestDto.password());
        // 토큰 생성
        return tokenService.generateToken(String.valueOf(chatUser.getId()));
    }

    // 기존 유저 조회 후 비밀번호 인증, 없으면 신규 생성
    private ChatUser saveOrGet(long chatRoomId, String nickname, String rawPassword) {
        // 채팅방 id와 닉네임으로 조회
        Optional<ChatUser> existingUser = chatUserRepository.findByChatRoomIdAndNickname(chatRoomId, nickname);

        if (existingUser.isPresent()) {
            ChatUser chatUser = existingUser.get();

            // 비밀번호 검사 (평문 vs 해시)
            if (encoder.matches(rawPassword, chatUser.getPassword())) {
                log.info("ChatUser login succeeded: id={}", chatUser.getId());
                return chatUser;
            } else {
                throw new CustomException(ErrorCode.PASSWORD_MISMATCH);
            }
        }

        // 신규 가입인 경우 방장
        boolean isOwner = chatUserRepository.countByChatRoomId(chatRoomId) == 0;

        String encodedPassword = encoder.encode(rawPassword);
        ChatUser newChatUser = ChatUser.builder()
                .id(nextLong())
                .chatRoomId(chatRoomId)
                .nickname(nickname)
                .password(encodedPassword)
                .createdAt(Instant.now())
                .isOwner(isOwner)
                .build();

        ChatUser savedChatUser = chatUserRepository.save(newChatUser);
        log.info("New chatUser created: id={} chatRoomId={} nickname={}", savedChatUser.getId(), chatRoomId, nickname);
        return savedChatUser;
    }

    public ChatUser getChatUserById(long id) {
        Optional<ChatUser> chatUser = chatUserRepository.findById(id);
        if (chatUser.isPresent()) {
            return chatUser.get();
        }
        throw new CustomException(ErrorCode.CHAT_USER_NOT_FOUND);
    }

    public ChatUser getCurrentChatUser(UserDetails userDetails) {
        return getChatUserById(Long.parseLong(userDetails.getUsername()));
    }
}
