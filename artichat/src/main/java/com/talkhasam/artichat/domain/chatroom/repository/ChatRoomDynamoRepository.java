package com.talkhasam.artichat.domain.chatroom.repository;


import com.talkhasam.artichat.domain.chatroom.entity.ChatRoom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ChatRoomDynamoRepository implements ChatRoomRepository {

    private final DynamoDbTable<ChatRoom> table;

    @Override
    public Optional<ChatRoom> findById(long chatRoomId) {
        ChatRoom item = table.getItem(r -> r.key(k -> k.partitionValue(chatRoomId)));
        log.info("ChatRoom found: {}", item);
        return Optional.ofNullable(item);
    }

    @Override
    public ChatRoom save(ChatRoom chatRoom) {
        table.putItem(chatRoom);
        return chatRoom;
    }

    @Override
    public void deleteById(long chatRoomId) {
        table.deleteItem(r -> r.key(k -> k.partitionValue(chatRoomId)));
    }

    @Override
    public List<ChatRoom> findAll() {
        return StreamSupport.stream(
                table.scan(ScanEnhancedRequest.builder().build()).items().spliterator(),
                false
        ).toList();
    }

    @Override
    public void updateProfileImg(long chatRoomId, String profileImg) {
        // 1. 기존 채팅방 조회
        Optional<ChatRoom> existingRoom = findById(chatRoomId);
        if (existingRoom.isEmpty()) {
            throw new RuntimeException("채팅방을 찾을 수 없습니다. ID: " + chatRoomId);
        }

        // 2. 수정된 채팅방 객체 생성
        ChatRoom chatRoom = existingRoom.get();
        ChatRoom updatedRoom = ChatRoom.builder()
                .id(chatRoom.getId())
                .chatRoomName(chatRoom.getChatRoomName())
                .profileImg(profileImg)  // 새로운 프로필 이미지
                .createdAt(chatRoom.getCreatedAt())
                .modifiedAt(Instant.now())  // 수정 시간 업데이트
                .build();

        // 3. 저장 (DynamoDB에서는 putItem이 upsert 역할)
        table.putItem(updatedRoom);
    }
}
