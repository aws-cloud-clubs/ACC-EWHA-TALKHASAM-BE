package com.talkhasam.artichat.domain.message.entity;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
    @Positive
    public long chatUserId;

    @Getter(onMethod_=@DynamoDbSortKey)
    @Positive
    private long id;

    @Getter(onMethod_=@DynamoDbAttribute("createdAt"))
    @NotNull
    private Instant createdAt;

    @Getter(onMethod_=@DynamoDbAttribute("ttlEpoch"))
    public Long ttlEpoch;
}
