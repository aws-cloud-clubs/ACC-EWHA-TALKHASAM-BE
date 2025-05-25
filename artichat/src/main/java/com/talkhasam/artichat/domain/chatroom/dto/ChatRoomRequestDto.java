package com.talkhasam.artichat.domain.chatroom.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomRequestDto {
    private String chatRoomName;
    private String owner;
    private String password;
    private MultipartFile profileImg; // nullable
}

