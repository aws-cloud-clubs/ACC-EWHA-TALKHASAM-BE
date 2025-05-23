package com.talkhasam.artichat.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talkhasam.artichat.global.redis.RedisStompBridge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

import java.time.Duration;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        // 서버 정보
        RedisStandaloneConfiguration serverConfig =
                new RedisStandaloneConfiguration(redisHost, redisPort);

        // 클라이언트에 TLS 옵션 적용
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .useSsl()                             // SSL 켜기
                .startTls()                           // STARTTLS 모드
                .disablePeerVerification()            // (선택) 자체 서명 인증서용
                .and()                                // ↑ SSL 빌더에서 상위 빌더로 복귀
                .commandTimeout(Duration.ofSeconds(10))
                .build();

        return new LettuceConnectionFactory(serverConfig, clientConfig);
    }


    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            LettuceConnectionFactory connectionFactory,
            ObjectMapper mapper
    ) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(mapper);
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public RedisMessageListenerContainer redisListenerContainer(
            LettuceConnectionFactory connectionFactory,
            RedisStompBridge bridge
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(bridge, new PatternTopic("/topic/chatroom/*"));
        return container;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(RedisStompBridge redisStompBridge) {
        return new MessageListenerAdapter(redisStompBridge, "onMessage");
    }
}