package com.talkhasam.artichat.domain.chatuser.repository;

import com.talkhasam.artichat.domain.chatuser.entity.ChatUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
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
    public Optional<ChatUser> findByChatRoomIdAndNickname(long chatRoomId, String nickname) {
        QueryConditional keyCondition = QueryConditional.keyEqualTo(
                Key.builder().partitionValue(chatRoomId).build()
        );
        Expression filter = Expression.builder()
                .expression("nickname = :nick")
                .putExpressionValue(
                        ":nick", AttributeValue.builder().s(nickname).build()
                )
                .build();

        return table.query(r -> r
                        .queryConditional(keyCondition)
                        .filterExpression(filter))
                .items()
                .stream()
                .findFirst();
    }

    @Override
    public int countByChatRoomId(long chatRoomId) {
        QueryConditional keyCondition = QueryConditional.keyEqualTo(
                Key.builder().partitionValue(chatRoomId).build()
        );
        // Scan items matching the partition key and count
        long count = StreamSupport.stream(
                table.query(r -> r.queryConditional(keyCondition)).items().spliterator(),
                false
        ).count();
        return (int) count;
    }

//    @Override
//    public Optional<ChatUser> findByChatRoomIdAndNicknameAndPassword(long chatRoomId, String nickname, String password) {
//        QueryConditional keyCondition = QueryConditional.keyEqualTo(
//                Key.builder().partitionValue(chatRoomId).build()
//        );
//        Expression filter = Expression.builder()
//                .expression("nickname = :nick AND password = :pwd")
//                .putExpressionValue(
//                        ":nick", AttributeValue.builder().s(nickname).build()
//                )
//                .putExpressionValue(
//                        ":pwd",  AttributeValue.builder().s(password).build()
//                )
//                .build();
//
//        return table.query(r -> r
//                        .queryConditional(keyCondition)
//                        .filterExpression(filter))
//                .items()
//                .stream()
//                .findAny();
//    }
}