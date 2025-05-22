package com.talkhasam.artichat.global.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisStompBridge implements MessageListener {
    private final SimpMessagingTemplate template;
    private final ObjectMapper mapper;

    // 이 메서드에 쓰인 Message는 Redis Connection의 Message
    public void onMessage(Message message, byte[] pattern) {
        try {
            String destination = new String(message.getChannel(), StandardCharsets.UTF_8);
            String body = new String(message.getBody(), StandardCharsets.UTF_8);
            // JSON → DTO 로 역직렬화 후 STOMP 브로드캐스트
            Object payload = mapper.readValue(body, Object.class);
            template.convertAndSend(destination, payload);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
