package com.talkhasam.artichat.domain.chatroom.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.Instant;

@DynamoDbBean
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoom {

    @Getter(onMethod_=@DynamoDbPartitionKey)
    private long id; // TSID 형식

    @Getter(onMethod_=@DynamoDbAttribute("nickname"))
    private String nickname;

    @Getter(onMethod_=@DynamoDbAttribute("password"))
    private String password;

    @Getter(onMethod_=@DynamoDbAttribute("profileImg"))
    private String profileImg;

    @Getter(onMethod_=@DynamoDbAttribute("createdAt"))
    private Instant createdAt;

    @Getter(onMethod_=@DynamoDbAttribute("modifiedAt"))
    private Instant modifiedAt;

    @Getter(onMethod_=@DynamoDbAttribute("public_id"))
    private String publicId; // URL 생성 목적
}