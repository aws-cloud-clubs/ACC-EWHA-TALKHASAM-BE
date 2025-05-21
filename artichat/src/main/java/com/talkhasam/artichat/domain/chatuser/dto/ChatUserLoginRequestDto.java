package com.talkhasam.artichat.domain.chatuser.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChatUserLoginRequestDto(
        @NotNull
        Long chatRoomId,
        @NotBlank
        String nickname,
        @NotBlank
        String password
) {}
