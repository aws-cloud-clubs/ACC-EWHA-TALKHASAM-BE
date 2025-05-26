package com.talkhasam.artichat.domain.message.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class EchoController {

    @MessageMapping("/echo")  // 클라이언트가 /app/echo 로 보낸 메시지 처리
    @SendTo("/topic/echo")    // 결과를 구독 중인 클라이언트에게 전달
    public String echo(String message) {
        return "Echo: " + message;
    }
}
