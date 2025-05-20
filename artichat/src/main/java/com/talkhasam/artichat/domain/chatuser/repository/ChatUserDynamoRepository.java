package com.talkhasam.artichat.domain.chatuser.repository;

import com.talkhasam.artichat.domain.chatuser.entity.ChatUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
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
    public Optional<ChatUser> findByChatRoomIdAndNicknameAndPassword(
            long chatRoomId,
            String nickname,
            String encodedPassword
    ) {
        // GSI 쿼리 실행
        SdkIterable<Page<ChatUser>> pages = table.index("chatRoomId-index")
                .query(r -> r
                        .queryConditional(QueryConditional.keyEqualTo(k ->
                                k.partitionValue(chatRoomId)))
                        .filterExpression(Expression.builder()
                                .expression("nickname = :nick AND password = :pwd")
                                .putExpressionValue(":nick",
                                        AttributeValue.builder().s(nickname).build())
                                .putExpressionValue(":pwd",
                                        AttributeValue.builder().s(encodedPassword).build())
                                .build())
                );

        // 페이지별 아이템을 flatMap으로 풀어서 첫 번째 결과 반환
        return StreamSupport.stream(pages.spliterator(), false)
                .flatMap(page -> page.items().stream())
                .findFirst();
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
    public Optional<ChatUser> findById(long userId) {
        ChatUser user = table.getItem(r -> r
                .key(k -> k.partitionValue(userId))
        );
        return Optional.ofNullable(user);
    }
}