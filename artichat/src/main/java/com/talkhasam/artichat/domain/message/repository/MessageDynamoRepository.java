package com.talkhasam.artichat.domain.message.repository;

import com.talkhasam.artichat.domain.message.entity.Message;
import com.talkhasam.artichat.global.common.PageResult;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class MessageDynamoRepository implements MessageRepository {
    private final DynamoDbTable<Message> table;

    // 메시지 저장
    @Override
    public void save(Message msg) {
        table.putItem(msg);
    }

    // chatRoomId로 메시지 조회 (최신순, 페이징)
    @Override
    public PageResult<Message> findByChatRoomId(long chatRoomId, int limit, @Nullable Long exclusiveStartMessageId) {
        QueryEnhancedRequest.Builder req = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(chatRoomId)))
                .scanIndexForward(false)
                .limit(limit);

        return queryWithPaging(chatRoomId, exclusiveStartMessageId, req);
    }

    // chatRoomId로 isOwner=true인 메시지 조회 (최신순, 페이징)
    @Override
    public PageResult<Message> findByChatRoomIdAndIsOwnerTrue(long chatRoomId, int limit, Long exclusiveStartMessageId) {
        QueryEnhancedRequest.Builder req = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(chatRoomId)))
                .filterExpression(Expression.builder()
                        .expression("isOwner = :trueVal")
                        .putExpressionValue(":trueVal", AttributeValue.builder().bool(true).build())
                        .build())
                .scanIndexForward(false)
                .limit(limit);

        return queryWithPaging(chatRoomId, exclusiveStartMessageId, req);
    }

    // chatUserId 인덱스로 메시지 조회 (최신순, 페이징)
    @Override
    public PageResult<Message> findByChatUserId(long chatUserId, int limit, Long exclusiveStartMessageId) {
        QueryEnhancedRequest.Builder req = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(chatUserId)))
                .scanIndexForward(false)
                .limit(limit);

        // 페이징용 시작키 설정
        if (exclusiveStartMessageId != null) {
            req.exclusiveStartKey(buildExclusiveStartKey("chatUserId", chatUserId, exclusiveStartMessageId));
        }

        // chatUserId GSI 사용 쿼리
        SdkIterable<Page<Message>> pages = table.index("chatUserId-index").query(req.build());
        Page<Message> page = pages.stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("페이지를 찾을 수 없습니다."));

        return buildPageResult(page);
    }

    // 공통: DynamoDB 쿼리 및 페이징 처리
    private PageResult<Message> queryWithPaging(long partitionValue, Long exclusiveStartMessageId, QueryEnhancedRequest.Builder req) {
        if (exclusiveStartMessageId != null) {
            req.exclusiveStartKey(buildExclusiveStartKey("chatRoomId", partitionValue, exclusiveStartMessageId));
        }

        PageIterable<Message> pages = table.query(req.build());
        Page<Message> page = pages.stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("페이지를 찾을 수 없습니다."));

        return buildPageResult(page);
    }

    // exclusiveStartKey를 생성 (파티션키명, 파티션값, 정렬키값)
    private Map<String, AttributeValue> buildExclusiveStartKey(String partitionKeyName, long partitionValue, Long sortKeyValue) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put(partitionKeyName, AttributeValue.builder().n(String.valueOf(partitionValue)).build());
        key.put("id", AttributeValue.builder().n(String.valueOf(sortKeyValue)).build());
        return key;
    }

    // 페이지 결과를 PageResult 객체로 변환 (아이템과 다음 키 포함)
    private PageResult<Message> buildPageResult(Page<Message> page) {
        Map<String, AttributeValue> lastKey = page.lastEvaluatedKey();
        Long nextKey = null;
        if (lastKey != null && lastKey.containsKey("id")) {
            nextKey = Long.valueOf(lastKey.get("id").n());
        }
        return new PageResult<>(page.items(), nextKey);
    }
}
