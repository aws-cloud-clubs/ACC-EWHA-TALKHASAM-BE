package com.talkhasam.artichat.domain.chatuser.repository;

import com.talkhasam.artichat.domain.chatuser.entity.ChatUser;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Iterator;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Repository
public class ChatUserDynamoRepository implements ChatUserRepository {

    private final DynamoDbTable<ChatUser> table;

    public ChatUserDynamoRepository(DynamoDbEnhancedClient enhancedClient) {
        this.table = enhancedClient.table("chat_user", TableSchema.fromBean(ChatUser.class));
    }

    @Override
    public Optional<ChatUser> findByChatRoomIdAndNickname(long chatRoomId, String nickname) {
        // 1) 파티션 키로 먼저 쿼리
        QueryConditional partitionKeyCondition =
                QueryConditional.keyEqualTo(Key.builder()
                        .partitionValue(chatRoomId)
                        .build());

        // 2) nickname 필터 추가
        Expression nicknameFilter = Expression.builder()
                .expression("nickname = :nick")
                .putExpressionValue(":nick", AttributeValue.builder().s(nickname).build())
                .build();

        // 3) 쿼리 실행 후 첫 번째 아이템 리턴
        Iterator<ChatUser> iterator = table.query(r -> r
                        .queryConditional(partitionKeyCondition)
                        .filterExpression(nicknameFilter))
                .items()
                .iterator();

        return iterator.hasNext()
                ? Optional.of(iterator.next())
                : Optional.empty();
    }

    @Override
    public ChatUser save(ChatUser chatUser) {
        table.putItem(chatUser);
        return chatUser;
    }

    @Override
    public int countByChatRoomId(long chatRoomId) {
        QueryConditional keyCondition = QueryConditional.keyEqualTo(
                Key.builder().partitionValue(chatRoomId).build()
        );
        // 전체 쿼리 결과 스트림으로 변환하여 카운트
        long count = StreamSupport.stream(
                table.query(r -> r.queryConditional(keyCondition)).items().spliterator(),
                false
        ).count();
        return (int) count;
    }

    @Override
    public boolean existsByChatRoomIdAndNickname(long chatRoomId, String nickname) {
        return findByChatRoomIdAndNickname(chatRoomId, nickname).isPresent();
    }
}