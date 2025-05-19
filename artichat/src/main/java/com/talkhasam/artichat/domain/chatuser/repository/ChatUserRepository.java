package com.talkhasam.artichat.domain.chatuser.repository;

import com.talkhasam.artichat.domain.chatuser.entity.ChatUser;

import java.util.Optional;

public interface ChatUserRepository {
    Optional<ChatUser> findByChatRoomIdAndNickname(String chatRoomId, String nickname);
    ChatUser save(ChatUser chatUser);
}
