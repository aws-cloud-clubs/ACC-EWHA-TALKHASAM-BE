package com.talkhasam.artichat.domain.chatuser.dto;

public record ChatUserLoginDataDto(
    String accessToken,
    Boolean isOwner
) {}