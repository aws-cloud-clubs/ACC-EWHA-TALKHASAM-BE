package com.talkhasam.artichat.domain.chatuser.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
    @Positive
    private long id; // TSID 형식

    @Getter(onMethod_=@DynamoDbSortKey)
    @Positive
    private long chatRoomId;

    @Getter(onMethod_=@DynamoDbAttribute("nickname"))
    @NotBlank
    private String nickname;

    @Getter(onMethod_=@DynamoDbAttribute("password"))
    @Size(min=8, max=16)
    private String password;

    @Getter(onMethod_=@DynamoDbAttribute("createdAt"))
    @NotNull
    private Instant createdAt;

    @Getter(onMethod_=@DynamoDbAttribute("isOwner"))
    private boolean isOwner;
}