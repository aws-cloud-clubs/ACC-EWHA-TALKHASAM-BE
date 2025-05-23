package com.talkhasam.artichat.domain.message.entity;

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
public class Message {

    private long chatRoomId;    // Partition Key
    private long id;            // Sort Key (메시지 고유 ID)
    private long chatUserId;    // GSI (Partition Key)
    private String nickname;
    private boolean isOwner;
    private String content;
    private Instant createdAt;
    private Long ttlEpoch;

    @DynamoDbPartitionKey
    @Positive
    public long getChatRoomId() {
        return chatRoomId;
    }

    @DynamoDbSortKey
    @Positive
    public long getId() {
        return id;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = "chatUserId-index")
    @Positive
    public long getChatUserId() {
        return chatUserId;
    }

    @DynamoDbAttribute("nickname")
    @NotBlank
    public String getNickname() {
        return nickname;
    }

    @DynamoDbAttribute("isOwner")
    public boolean isOwner() {
        return isOwner;
    }

    @DynamoDbAttribute("content")
    @NotBlank
    @Size(max = 10000)
    public String getContent() {
        return content;
    }

    @DynamoDbAttribute("createdAt")
    @NotNull
    public Instant getCreatedAt() {
        return createdAt;
    }

    @DynamoDbAttribute("ttlEpoch")
    @NotNull
    public Long getTtlEpoch() {
        return ttlEpoch;
    }
}
