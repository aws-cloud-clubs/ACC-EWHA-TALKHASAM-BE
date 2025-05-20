package com.talkhasam.artichat.global.config;

import com.talkhasam.artichat.domain.chatroom.entity.ChatRoom;
import com.talkhasam.artichat.domain.chatuser.entity.ChatUser;
import com.talkhasam.artichat.domain.message.entity.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

@Configuration
public class DynamoDbConfig {

    @Value("${aws.region}")
    private String region;

    @Value("${aws.credentials.access-key}")
    private String accessKeyId;

    @Value("${aws.credentials.secret-key}")
    private String secretAccessKey;

    @Value("${aws.dynamodb.endpoint}")
    private String endpoint;

    @Bean
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKeyId, secretAccessKey)
                ))
                .build();
    }

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient client) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(client)
                .build();
    }

    // 테이블 등록

    @Bean
    public DynamoDbTable<ChatRoom> chatRoomTable(
            DynamoDbEnhancedClient enhancedClient,
            @Value("${aws.dynamodb.chatRoomTable}") String tableName
    ) {
        return enhancedClient.table(
                tableName,
                TableSchema.fromBean(ChatRoom.class)
        );
    }

    @Bean
    public DynamoDbTable<ChatUser> chatUserTable(
            DynamoDbEnhancedClient enhancedClient,
            @Value("${aws.dynamodb.chatUserTable}") String tableName
    ) {
        return enhancedClient.table(
                tableName,
                TableSchema.fromBean(ChatUser.class)
        );
    }

    @Bean
    public DynamoDbTable<Message> messageTable(
            DynamoDbEnhancedClient enhancedClient,
            @Value("${aws.dynamodb.messageTable}") String tableName
    ) {
        return enhancedClient.table(
                tableName,
                TableSchema.fromBean(Message.class)
        );
    }
}
