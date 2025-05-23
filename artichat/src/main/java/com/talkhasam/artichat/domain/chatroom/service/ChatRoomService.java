package com.talkhasam.artichat.domain.chatroom.service;

import com.talkhasam.artichat.domain.chatroom.entity.ChatRoom;
import com.talkhasam.artichat.domain.chatroom.repository.ChatRoomRepository;
import com.talkhasam.artichat.global.exception.CustomException;
import com.talkhasam.artichat.global.exception.ErrorCode;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

import static com.talkhasam.artichat.global.util.TsidGenerator.nextLong;

@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    public ChatRoomService(ChatRoomRepository chatRoomRepository) {
        this.chatRoomRepository = chatRoomRepository;
    }

    /** 채팅방 생성 */
    public ChatRoom createChatRoom(ChatRoom chatRoom) {
        Long chatRoomId = nextLong();
        chatRoom.setId(chatRoomId);
        chatRoom.setCreatedAt(Instant.now());
        chatRoom.setModifiedAt(Instant.now());
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

    /** 프로필 수정 */
    public ChatRoom updateProfileImage(Long chatRoomId, String profileImg) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        chatRoom.setProfileImg(profileImg);
        chatRoom.setModifiedAt(Instant.now());

        return chatRoomRepository.save(chatRoom);
    }

}