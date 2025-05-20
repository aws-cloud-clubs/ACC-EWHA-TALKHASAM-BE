package com.talkhasam.artichat.domain.chatroom.repository;

import com.talkhasam.artichat.domain.chatroom.entity.ChatRoom;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository {
    Optional<ChatRoom> findById(long chatRoomId);
    ChatRoom save(ChatRoom chatRoom);
    void deleteById(long chatRoomId);
    List<ChatRoom> findAll();
}