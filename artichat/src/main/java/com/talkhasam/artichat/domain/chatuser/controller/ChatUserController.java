package com.talkhasam.artichat.domain.chatuser.controller;

import com.talkhasam.artichat.domain.chatuser.dto.ChatUserDto;
import com.talkhasam.artichat.domain.chatuser.dto.ChatUserLoginRequestDto;
import com.talkhasam.artichat.domain.chatuser.dto.ChatUserLoginResponseDto;
import com.talkhasam.artichat.domain.chatuser.service.ChatUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "ChatUsers")
@RestController
@RequestMapping("/chatusers")
@RequiredArgsConstructor
public class ChatUserController {
    private final ChatUserService chatUserService;

    @Operation(summary = "새 채팅방에 로그인 또는 가입")
    @PostMapping("/login")
    public ResponseEntity<ChatUserLoginResponseDto> login(@RequestBody @Valid ChatUserLoginRequestDto requestDto) {
        return ResponseEntity.ok(new ChatUserLoginResponseDto(chatUserService.loginOrRegister(requestDto)));
    }

    @Operation(summary = "액세스토큰으로 내 정보 조회")
    @GetMapping("/self")
    public ResponseEntity<ChatUserDto> getSelf(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ChatUserDto.toEntity(chatUserService.getCurrentChatUser(userDetails)));
    }
}
