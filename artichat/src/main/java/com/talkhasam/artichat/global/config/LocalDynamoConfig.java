//package com.talkhasam.artichat.global.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
//import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
//import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
//import software.amazon.awssdk.regions.Region;
//import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
//
//import java.net.URI;
//
//@Configuration
//public class DynamoDbConfig {
//
//    @Bean
//    public DynamoDbClient dynamoDbClient() {
//        return DynamoDbClient.builder()
//                // 1) 로컬 DynamoDB 엔드포인트
//                .endpointOverride(URI.create("http://localhost:8000"))
//                // 2) 리전: 로컬에선 아무거나
//                .region(Region.of("ap-northeast-2"))
//                // 3) 더미 자격증명: 로컬은 검사하지 않음
//                .credentialsProvider(StaticCredentialsProvider.create(
//                        AwsBasicCredentials.create("dummy", "dummy")
//                ))
//                .build();
//    }
//
//    @Bean
//    public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient client) {
//        return DynamoDbEnhancedClient.builder()
//                .dynamoDbClient(client)
//                .build();
//    }
//}
