package com.talkhasam.artichat.domain.chatroom.controller;

import com.talkhasam.artichat.domain.chatroom.entity.ChatRoom;
import com.talkhasam.artichat.domain.chatroom.service.ChatRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/chatrooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService service;

    /** 생성 (201 Created + Location 헤더) */
    @PostMapping
    public ResponseEntity<ChatRoomResponseDto> createChatRoom(@RequestBody @Valid ChatRoom chatRoom) {
        ChatRoom saved = service.createChatRoom(chatRoom);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
        return ResponseEntity.created(location).body(ChatRoomResponseDto.from(saved));
    }

    /** 단건 조회 (200 OK) */
    @GetMapping("/{id}")
    public ResponseEntity<ChatRoomResponseDto> getChatRoomById(@PathVariable Long id) {
        ChatRoom room = service.getChatRoom(id);
        return ResponseEntity.ok(ChatRoomResponseDto.from(room));
    }

    /** 전체 조회 (200 OK) */
    @GetMapping
    public ResponseEntity<List<ChatRoomResponseDto>> getAllChatRooms() {
        List<ChatRoom> rooms = service.getAllChatRooms();
        List<ChatRoomResponseDto> result = rooms.stream()
                .map(ChatRoomResponseDto::from)
                .toList();
        return ResponseEntity.ok(result);
    }

    /** 프로필 수정 (200 OK) */
    @PatchMapping("/{id}")
    public ResponseEntity<ChatRoomResponseDto> updateProfileImage(
            @PathVariable Long id,
            @RequestBody @Valid UpdateProfileImageRequest request) {

        ChatRoom updated = service.updateProfileImage(id, request.profileImg());
        return ResponseEntity.ok(ChatRoomResponseDto.from(updated));
    }


}
