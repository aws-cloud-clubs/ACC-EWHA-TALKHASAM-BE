package com.talkhasam.artichat.domain.chatuser.repository;

import com.talkhasam.artichat.domain.chatuser.entity.ChatUser;

import java.util.Optional;

public interface ChatUserRepository {
    ChatUser save(ChatUser chatUser);
    Optional<ChatUser> findById(long userId);
    int countByChatRoomId(long chatRoomId);
    Optional<ChatUser> findByChatRoomIdAndNickname(long chatRoomId, String nickname);
}