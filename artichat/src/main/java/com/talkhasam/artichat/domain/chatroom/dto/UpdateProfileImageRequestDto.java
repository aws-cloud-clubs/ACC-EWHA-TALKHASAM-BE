package com.talkhasam.artichat.domain.chatroom.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UpdateProfileImageRequestDto {
        private MultipartFile profileImg;
}