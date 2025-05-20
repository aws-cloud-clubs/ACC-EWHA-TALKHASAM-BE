package com.talkhasam.artichat.domain.chatuser.dto;

import jakarta.validation.constraints.NotBlank;

public record ChatUserLoginRequestDto(
        long chatRoomId,
        @NotBlank
        String nickname,
        String password
) {}
