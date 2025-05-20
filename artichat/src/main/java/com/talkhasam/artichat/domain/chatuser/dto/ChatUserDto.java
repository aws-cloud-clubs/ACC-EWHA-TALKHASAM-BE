package com.talkhasam.artichat.domain.chatuser.dto;

import java.time.Instant;

public record ChatUserDto(
        long chatUserId,
        long chatRoomId,
        String nickname,
        Instant createdAt
) {
}