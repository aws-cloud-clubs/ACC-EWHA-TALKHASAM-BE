package com.talkhasam.artichat.domain.chatroom.controller;

import com.talkhasam.artichat.domain.chatroom.dto.ChatRoomPostResponseDto;
import com.talkhasam.artichat.domain.chatroom.dto.ChatRoomRequestDto;
import com.talkhasam.artichat.domain.chatroom.dto.ChatRoomResponseDto;
import com.talkhasam.artichat.domain.chatroom.dto.UpdateProfileImageRequestDto;
import com.talkhasam.artichat.domain.chatroom.service.ChatRoomService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "ChatRooms")
@RestController
@RequestMapping("/chatrooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService service;

    /** 생성 (201 Created + Location 헤더) */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChatRoomPostResponseDto> createChatRoom(@RequestBody ChatRoomRequestDto requestDto) {
        ChatRoomPostResponseDto response = service.createChatRoom(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    /** 채팅방 정보 조회 **/
    @GetMapping("/{chatRoomId}")
    public ResponseEntity<ChatRoomResponseDto> getChatRoomInfo(@PathVariable Long chatRoomId) {
        ChatRoomResponseDto response = service.getChatRoomInfo(chatRoomId);
        return ResponseEntity.ok(response);
    }

    /** 프로필 수정 (200 OK) */
    @PutMapping(value = "/{chatRoomId}/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateChatRoomProfileImg(
            @PathVariable Long chatRoomId,
            @ModelAttribute UpdateProfileImageRequestDto dto) {
        service.updateChatRoomProfileImg(chatRoomId, dto);
        return ResponseEntity.ok().build();
    }


//    /** 단건 조회 (200 OK) */
//    @GetMapping("/{id}")
//    public ResponseEntity<ChatRoomResponseDto> getChatRoomById(@PathVariable Long id) {
//        ChatRoom room = service.getChatRoom(id);
//        return ResponseEntity.ok(ChatRoomResponseDto.from(room));
//    }
//
//    /** 전체 조회 (200 OK) */
//    @GetMapping
//    public ResponseEntity<List<ChatRoomResponseDto>> getAllChatRooms() {
//        List<ChatRoom> rooms = service.getAllChatRooms();
//        List<ChatRoomResponseDto> result = rooms.stream()
//                .map(ChatRoomResponseDto::from)
//                .toList();
//        return ResponseEntity.ok(result);
//    }
}
