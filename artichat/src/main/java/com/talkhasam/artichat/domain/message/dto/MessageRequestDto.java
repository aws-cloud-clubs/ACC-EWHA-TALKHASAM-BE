package com.talkhasam.artichat.domain.message.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// 클라이언트에서 서버로 들어오는 요청
public record MessageRequestDto(
        @NotBlank
        String nickname,
        @NotNull
        Boolean isOwner,
        @NotBlank
        @Size(max = 10000, message = "메시지 내용은 최대 10,000자까지 입력할 수 있습니다.")
        String content
) {}