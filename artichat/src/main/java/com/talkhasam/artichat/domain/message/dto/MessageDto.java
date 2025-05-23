package com.talkhasam.artichat.domain.message.dto;

import java.time.Instant;

public record MessageDto(
        long id,
        long chatUserId,
        String nickname,
        Boolean isOwner,
        String content,
        Instant createdAt
) {
}
