package com.talkhasam.artichat.domain.chatuser.controller;

import com.talkhasam.artichat.domain.chatuser.dto.ChatUserLoginRequestDto;
import com.talkhasam.artichat.domain.chatuser.service.ChatUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chatusers")
@RequiredArgsConstructor
public class ChatUserController {

    private final ChatUserService chatUserService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody ChatUserLoginRequestDto chatUserLoginRequestDto) {
        String token = chatUserService.loginOrRegister(chatUserLoginRequestDto);
        return ResponseEntity.ok(token);
    }
}
