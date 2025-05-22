package com.talkhasam.artichat.global.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(String destination, Object payload) {
        redisTemplate.convertAndSend(destination, payload);
    }
}