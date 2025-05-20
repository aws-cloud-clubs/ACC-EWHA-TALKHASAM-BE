package com.talkhasam.artichat.domain.message.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.time.Instant;

@DynamoDbBean
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    @Getter(onMethod_=@DynamoDbPartitionKey)
    private long messageId;

    @Getter(onMethod_=@DynamoDbSortKey)
    public long chatUserId;

    @Getter(onMethod_=@DynamoDbAttribute("createdAt"))
    private Instant createdAt;

}
