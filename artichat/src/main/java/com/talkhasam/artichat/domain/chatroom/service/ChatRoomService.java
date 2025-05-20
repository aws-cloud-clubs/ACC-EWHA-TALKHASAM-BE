package com.talkhasam.artichat.domain.chatroom.service;

import com.talkhasam.artichat.domain.chatroom.entity.ChatRoom;
import com.talkhasam.artichat.domain.chatroom.repository.ChatRoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    public ChatRoomService(ChatRoomRepository chatRoomRepository) {
        this.chatRoomRepository = chatRoomRepository;
    }

    /** 채팅방 생성 */
    public ChatRoom createChatRoom(ChatRoom chatRoom) {
        return chatRoomRepository.save(chatRoom);
    }

    /** 단건 조회 (존재하지 않으면 예외) */
    public ChatRoom getChatRoom(Long id) {
        return chatRoomRepository.findById(id).orElse(null);
    }

    /** 전체 조회 */
    public List<ChatRoom> getAllChatRooms() {
        return chatRoomRepository.findAll();
    }

}