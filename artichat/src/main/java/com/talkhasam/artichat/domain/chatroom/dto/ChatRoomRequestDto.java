package com.talkhasam.artichat.domain.chatroom.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public record ChatRoomRequestDto (
    String chatRoomName,
    @NotBlank String owner,
    @NotBlank @Size(min=8, max=16) String password,
    MultipartFile profileImg
){}