package com.talkhasam.artichat.domain.chatroom.service;

import com.talkhasam.artichat.domain.chatroom.dto.ChatRoomPostResponseDto;
import com.talkhasam.artichat.domain.chatroom.dto.ChatRoomRequestDto;
import com.talkhasam.artichat.domain.chatroom.dto.ChatRoomResponseDto;
import com.talkhasam.artichat.domain.chatroom.dto.UpdateProfileImageRequestDto;
import com.talkhasam.artichat.domain.chatroom.entity.ChatRoom;
import com.talkhasam.artichat.domain.chatroom.repository.ChatRoomRepository;
import com.talkhasam.artichat.domain.chatuser.entity.ChatUser;
import com.talkhasam.artichat.domain.chatuser.repository.ChatUserRepository;
import com.talkhasam.artichat.global.s3.S3Uploader;
import com.talkhasam.artichat.global.exception.CustomException;
import com.talkhasam.artichat.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatUserRepository chatUserRepository;
    private final S3Uploader s3Uploader;

    /** 채팅방 생성 */
    public ChatRoomResponseDto createChatRoom(ChatRoomRequestDto dto) {
        Long chatRoomId = nextLong();

        String imageUrl = null;
        if (dto.getProfileImg() != null && !dto.getProfileImg().isEmpty()) {
            imageUrl = s3Uploader.upload(dto.getProfileImg(), "chatroom-profiles");
        }

        ChatRoom chatRoom = ChatRoom.builder()
                .id(chatRoomId)
                .chatRoomName(dto.getChatRoomName())
                .profileImg(imageUrl)
                .createdAt(Instant.now())
                .modifiedAt(Instant.now())
                .build();

        chatRoomRepository.save(chatRoom);

        ChatUser chatUser = ChatUser.builder()
                .chatRoomId(chatRoomId)
                .nickname(dto.getOwner())
                .password(dto.getPassword())
                .isOwner(true)
                .createdAt(Instant.now())
                .build();

        chatUserRepository.save(chatUser);

        return ChatRoomPostResponseDto.from(chatRoomId);
    }

    /** 채팅방 정보 조회 */
    public ChatRoomResponseDto getChatRoomInfo(Long chatRoomId) {
        // 1. 채팅방 정보 조회
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다. ID: " + chatRoomId));

        // 2. 해당 채팅방의 소유자 찾기 (isOwner = true인 사용자)
        String owner = chatUserRepository.findByChatRoomIdAndIsOwner(chatRoomId, true)
                .map(ChatUser::getNickname)
                .orElseThrow(() -> new RuntimeException("채팅방 소유자를 찾을 수 없습니다. 채팅방 ID: " + chatRoomId));

        // 3. DTO로 변환하여 반환
        return ChatRoomResponseDto.from(chatRoom, owner);
    }


    private Long nextLong() {
        return UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
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
    @Transactional
    public void updateChatRoomProfileImg(Long chatRoomId, UpdateProfileImageRequestDto dto) {
        // 1. 채팅방 존재 확인
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다. ID: " + chatRoomId));

        // 2. 기존 프로필 이미지가 있다면 S3에서 삭제
        String oldProfileImg = chatRoom.getProfileImg();
        if (oldProfileImg != null && !oldProfileImg.isEmpty()) {
            try {
                s3Uploader.deleteFile(oldProfileImg);
            } catch (Exception e) {
                // 기존 파일 삭제 실패는 로그만 남기고 계속 진행
                System.err.println("기존 프로필 이미지 삭제 실패: " + e.getMessage());
            }
        }

        // 3. 새 프로필 이미지 업로드
        String newImageUrl = null;
        if (dto.getProfileImg() != null && !dto.getProfileImg().isEmpty()) {
            newImageUrl = s3Uploader.upload(dto.getProfileImg(), "chatroom-profiles");
        }

        // 4. 채팅방 프로필 이미지 업데이트
        chatRoomRepository.updateProfileImg(chatRoomId, newImageUrl);
    }

}