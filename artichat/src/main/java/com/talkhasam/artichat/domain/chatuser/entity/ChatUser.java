package com.talkhasam.artichat.domain.chatuser.entity;

import com.talkhasam.artichat.global.util.InstantStringConverter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.time.Instant;

@DynamoDbBean
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatUser {
    private long id;
    private long chatRoomId;    // GSI (Partition Key)
    private String nickname;
    private String password;
    private Instant createdAt;
    private boolean isOwner;      // 비즈니스용 불리언 필드

    @DynamoDbPartitionKey
    @Positive
    public long getId() {
        return id;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = "chatRoomId-index")
    public long getChatRoomId() {
        return chatRoomId;
    }

    @DynamoDbAttribute("nickname")
    @NotBlank
    public String getNickname() {
        return nickname;
    }

    @DynamoDbAttribute("password")
    @Size(min = 8, max = 16)
    public String getPassword() {
        return password;
    }

    @DynamoDbAttribute("createdAt")
    @DynamoDbConvertedBy(InstantStringConverter.class)
    @NotNull
    public Instant getCreatedAt() {
        return createdAt;
    }

    // GSI Sort Key용으로 문자열 변환
    @DynamoDbAttribute("isOwner")
    @NotNull
    public boolean isOwner() {
        return isOwner;
    }
}
