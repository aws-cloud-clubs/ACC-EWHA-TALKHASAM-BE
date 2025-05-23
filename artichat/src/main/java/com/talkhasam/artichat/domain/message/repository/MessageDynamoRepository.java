package com.talkhasam.artichat.domain.message.repository;

import com.talkhasam.artichat.domain.message.entity.Message;
import io.micrometer.common.lang.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class MessageDynamoRepository implements MessageRepository {
    private final DynamoDbTable<Message> table;

    // 페이지 결과 DTO
    @Getter
    public static class PageResult<T> {
        private final List<T> items;
        private final Long nextKey;

        public PageResult(List<T> items, Long nextKey) {
            this.items = items;
            this.nextKey = nextKey;
        }
    }

    @Override
    public void save(Message msg) {
        table.putItem(msg);
    }

    // 방별 메시지 조회 (최신순 페이징)
    @Override
    public PageResult<Message> findByChatRoomId(
            long chatRoomId,
            int limit,
            @Nullable Long startId
    ) {
        // 1) Query 조건 설정
        QueryEnhancedRequest.Builder req = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(k ->
                        k.partitionValue(chatRoomId)
                ))
                .scanIndexForward(false) // 최신순 정렬
                .limit(limit);

        // 2) startMessageId가 있으면 exclusiveStartKey 구성
        if (startId != null) {
            Map<String, AttributeValue> exclusiveStartKey = new HashMap<>();
            exclusiveStartKey.put("chatRoomId", AttributeValue.builder().n(String.valueOf(chatRoomId)).build());
            exclusiveStartKey.put("id", AttributeValue.builder().n(String.valueOf(startId)).build());
            req.exclusiveStartKey(exclusiveStartKey);
        }

        // 3) DynamoDB 쿼리 수행
        PageIterable<Message> pages = table.query(req.build());
        Page<Message> page = pages.stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("페이지를 찾을 수 없습니다."));

        // 4) 다음 페이지 키 추출
        Map<String, AttributeValue> lastKey = page.lastEvaluatedKey();
        Long nextKey = null;
        if (lastKey != null && lastKey.containsKey("id")) {
            nextKey = Long.valueOf(lastKey.get("id").n());
        }

        return new PageResult<>(page.items(), nextKey);
    }
}
