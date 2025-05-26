package com.talkhasam.artichat.domain.chatuser.repository;

import com.talkhasam.artichat.domain.chatuser.entity.ChatUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Optional;
import java.util.stream.StreamSupport;

@Repository
@RequiredArgsConstructor
public class ChatUserDynamoRepository implements ChatUserRepository {

    private final DynamoDbTable<ChatUser> table;

    @Override
    public ChatUser save(ChatUser chatUser) {
        table.putItem(chatUser);
        return chatUser;
    }

    @Override
    public Optional<ChatUser> findById(long id) {
        ChatUser user = table.getItem(r -> r
                .key(k -> k.partitionValue(id))
        );
        return Optional.ofNullable(user);
    }

    @Override
    public int countByChatRoomId(long chatRoomId) {
        QueryConditional keyCondition = QueryConditional.keyEqualTo(
                Key.builder().partitionValue(chatRoomId).build()
        );
        long count = StreamSupport.stream(
                table.query(r -> r.queryConditional(keyCondition)).items().spliterator(),
                false
        ).count();
        return (int) count;
    }

    @Override
    public Optional<ChatUser> findByChatRoomIdAndNickname(
            long chatRoomId,
            String nickname
    ) {
        // PartitionKey 조회 + nickname 필터
        QueryEnhancedRequest queryReq = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(
                        Key.builder()
                                .partitionValue(chatRoomId)
                                .build()
                ))
                .filterExpression(Expression.builder()
                        .expression("nickname = :nick")
                        .putExpressionValue(":nick",
                                AttributeValue.builder().s(nickname).build())
                        .build())
                .limit(1)  // 첫 매칭만
                .build();

        return table
                .index("chatRoomId-index")
                .query(queryReq)
                .stream()
                .flatMap(page -> page.items().stream())
                .findFirst();
    }


    @Override
    public Optional<ChatUser> findByChatRoomIdAndIsOwner(long chatRoomId, boolean isOwner) {
        return table
                .index("chatRoomId-isOwner-index")
                .query(r -> r.queryConditional(
                                QueryConditional.keyEqualTo(Key.builder()
                                        .partitionValue(chatRoomId)
                                        .sortValue(Boolean.toString(isOwner))
                                        .build()
                                ))
                        .limit(1)
                )
                .stream()
                .flatMap(page -> page.items().stream())
                .findFirst();
    }
}