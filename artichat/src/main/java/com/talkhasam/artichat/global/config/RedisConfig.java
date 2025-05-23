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

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.ssl.enabled}")
    private boolean sslEnabled;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration standaloneConfig =
                new RedisStandaloneConfiguration(redisHost, redisPort);
        LettuceClientConfiguration.LettuceClientConfigurationBuilder builder = LettuceClientConfiguration.builder();
        if (sslEnabled) {
            builder.useSsl();
        }
        LettuceClientConfiguration clientConfig = builder.build();

        return new LettuceConnectionFactory(standaloneConfig, clientConfig);
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