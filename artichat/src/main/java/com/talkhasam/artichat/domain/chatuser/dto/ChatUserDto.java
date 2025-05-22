package com.talkhasam.artichat.domain.chatuser.dto;

import com.talkhasam.artichat.domain.chatuser.entity.ChatUser;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChatUserDto(
        @NotNull
        Long chatUserId,
        @NotBlank
        String nickname
) {
    public static ChatUserDto toEntity(ChatUser chatUser) {
        return new ChatUserDto(chatUser.getId(), chatUser.getNickname());
    }
}