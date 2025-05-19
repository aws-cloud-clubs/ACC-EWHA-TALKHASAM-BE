package com.talkhasam.artichat.domain.chatuser.dto;

import jakarta.validation.constraints.NotBlank;

public record ChatUserLoginRequestDto(
        @NotBlank
        String chatRoomId,
        @NotBlank
        String nickname,
        String password
) {}
