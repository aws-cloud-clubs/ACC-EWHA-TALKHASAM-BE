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
    public ResponseEntity<ChatRoom> testCreateChatRoom(@RequestBody @Valid ChatRoom chatRoom) {
        ChatRoom saved = service.createChatRoom(chatRoom);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
        return ResponseEntity.created(location).body(saved);
    }

    /** 단건 조회 (200 OK) */
    @GetMapping("/{id}")
    public ResponseEntity<ChatRoom>  testGetById(@PathVariable Long id) {
        ChatRoom room = service.getChatRoom(id);
        return ResponseEntity.ok(room);
    }

    /** 전체 조회 (200 OK) */
    @GetMapping
    public ResponseEntity<List<ChatRoom>> testGetAll() {
        List<ChatRoom> rooms = service.getAllChatRooms();
        return ResponseEntity.ok(rooms);
    }

}
