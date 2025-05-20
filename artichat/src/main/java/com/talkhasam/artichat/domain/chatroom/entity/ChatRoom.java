package com.talkhasam.artichat.domain.chatroom.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
    @Positive
    private long id; // TSID 형식

    @Getter(onMethod_=@DynamoDbAttribute("chatRoomName"))
    @NotBlank
    private String chatRoomName;

    @Getter(onMethod_=@DynamoDbAttribute("profileImg"))
    private String profileImg;

    @Getter(onMethod_=@DynamoDbAttribute("createdAt"))
    @NotNull
    private Instant createdAt;

    @Getter(onMethod_=@DynamoDbAttribute("modifiedAt"))
    @NotNull
    private Instant modifiedAt;

    @Getter(onMethod_=@DynamoDbAttribute("linkId"))
    @NotBlank
    private String linkId; // URL 생성 목적
}