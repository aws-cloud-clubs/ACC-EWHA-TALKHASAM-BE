package com.talkhasam.artichat.domain.chatroom.repository;


import com.talkhasam.artichat.domain.chatroom.entity.ChatRoom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;

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
}
