package com.talkhasam.artichat.domain.chatroom.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateProfileImageRequestDto(
        @NotBlank
        String profileImg
) {}