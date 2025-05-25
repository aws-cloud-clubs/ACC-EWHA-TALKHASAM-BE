package com.talkhasam.artichat.domain.chatuser.repository;

import com.talkhasam.artichat.domain.chatuser.entity.ChatUser;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
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
        // chatRoomId + nickname 조건으로만 Scan
        ScanEnhancedRequest scanReq = ScanEnhancedRequest.builder()
                .filterExpression(Expression.builder()
                        .expression("chatRoomId = :rid AND nickname = :nick")
                        .putExpressionValue(":rid",
                                AttributeValue.builder().n(Long.toString(chatRoomId)).build())
                        .putExpressionValue(":nick",
                                AttributeValue.builder().s(nickname).build())
                        .build())
                .build();

        // 첫 매칭 유저 반환 (비밀번호 검증은 서비스 레이어에서)
        return table.scan(scanReq)
                .items()
                .stream()
                .findFirst();
    }

    @Override
    public Optional<ChatUser> findByChatRoomIdAndIsOwner(long chatRoomId, boolean isOwner) {
        // GSI를 사용하여 chatRoomId로 쿼리 후 isOwner 필터링
        QueryConditional keyCondition = QueryConditional.keyEqualTo(
                Key.builder().partitionValue(chatRoomId).build()
        );

        // GSI에서 쿼리 후 isOwner 조건으로 필터링
        return table.index("chatRoomId-index")
                .query(r -> r
                        .queryConditional(keyCondition)
                        .filterExpression(Expression.builder()
                                .expression("isOwner = :isOwner")
                                .putExpressionValue(":isOwner",
                                        AttributeValue.builder().bool(isOwner).build())
                                .build())
                )
                .stream()
                .flatMap(page -> page.items().stream())
                .findFirst();
    }
}