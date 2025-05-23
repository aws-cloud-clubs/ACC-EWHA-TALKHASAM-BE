package com.talkhasam.artichat.domain.chatroom.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.Instant;

@Setter
@DynamoDbBean
public class ChatRoom {

    private long id;
    private String chatRoomName;
    private String profileImg;
    private Instant createdAt;
    private Instant modifiedAt;
    private String linkId;

    // 기본 생성자
    public ChatRoom() {}

    // 전체 필드 생성자
    public ChatRoom(long id,
                    String chatRoomName,
                    String profileImg,
                    Instant createdAt,
                    Instant modifiedAt,
                    String linkId) {
        this.id = id;
        this.chatRoomName = chatRoomName;
        this.profileImg = profileImg;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.linkId = linkId;
    }

    @DynamoDbPartitionKey
    @Positive
    public long getId() {
        return id;
    }

    @DynamoDbAttribute("chatRoomName")
    @NotBlank
    public String getChatRoomName() {
        return chatRoomName;
    }

    @DynamoDbAttribute("profileImg")
    public String getProfileImg() {
        return profileImg;
    }

    @DynamoDbAttribute("createdAt")
    @NotNull
    public Instant getCreatedAt() {
        return createdAt;
    }

    @DynamoDbAttribute("modifiedAt")
    @NotNull
    public Instant getModifiedAt() {
        return modifiedAt;
    }

    @DynamoDbAttribute("linkId")
    @NotBlank
    public String getLinkId() {
        return linkId;
    }

    public void setModifiedAt(Instant now) {

    }
}
