package com.talkhasam.artichat.domain.message.repository;

import com.talkhasam.artichat.domain.message.entity.Message;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

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
        private final Map<String, AttributeValue> nextKey;

        public PageResult(List<T> items, Map<String, AttributeValue> nextKey) {
            this.items = items;
            this.nextKey = nextKey;
        }

    }

    @Override
    public void save(Message msg) {
        table.putItem(msg);
    }

    // 방별 메시지 조회 (최신순 페이징)
    public PageResult<Message> findByChatRoomId(
            long chatRoomId,
            int limit,
            Map<String, AttributeValue> exclusiveStartKey
    ) {
        // 1) 요청 빌드: 올바른 queryConditional 사용
        QueryEnhancedRequest.Builder req = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(k ->
                        k.partitionValue(chatRoomId)
                ))
                .scanIndexForward(false)
                .limit(limit);

        if (exclusiveStartKey != null) {
            req.exclusiveStartKey(exclusiveStartKey);
        }

        // 2) 페이지 단위로 조회
        PageIterable<Message> pages = table.query(req.build());
        Page<Message> page = pages.stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("페이지를 찾을 수 없습니다."));

        // 3) 결과 반환
        return new PageResult<>(
                page.items(),
                page.lastEvaluatedKey()   // null 이면 다음 페이지 없음
        );
    }
}
