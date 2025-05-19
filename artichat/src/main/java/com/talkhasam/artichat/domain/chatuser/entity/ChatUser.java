package com.talkhasam.artichat.domain.chatuser.entity;

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
public class ChatUser {

    @Getter(onMethod_=@DynamoDbPartitionKey)
    private String chatRoomId;

    @Getter(onMethod_=@DynamoDbSortKey)
    private String chatUserId;

    @Getter(onMethod_=@DynamoDbAttribute("nickname"))
    private String nickname;

    @Getter(onMethod_=@DynamoDbAttribute("password"))
    private String password;

    @Getter(onMethod_=@DynamoDbAttribute("createdAt"))
    private Instant createdAt;
}