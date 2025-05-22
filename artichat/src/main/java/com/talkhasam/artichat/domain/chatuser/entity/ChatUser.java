package com.talkhasam.artichat.domain.chatuser.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

import java.time.Instant;

@DynamoDbBean
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatUser {

    private long id;            // TSID 형식
    private long chatRoomId;    // Partition Key
    private String nickname;
    private String password;
    private Instant createdAt;
    private boolean isOwner;

    @DynamoDbPartitionKey
    @Positive
    public long getId() {
        return id;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = "chatRoomId-index")
    public long getChatRoomId() { return chatRoomId; }

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
    @NotNull
    public Instant getCreatedAt() {
        return createdAt;
    }

    @DynamoDbAttribute("isOwner")
    public boolean isOwner() {
        return isOwner;
    }
}
