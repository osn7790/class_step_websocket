package com.example.class_step_sebsocket.config;

import com.example.class_step_sebsocket.chat.ChatWebsocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@RequiredArgsConstructor
@Configuration
@EnableWebSocket // WebSocket 기능 활성화
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatWebsocketHandler chatWebsocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatWebsocketHandler,"/websocket")
                .setAllowedOrigins("*"); // 모든 도메인에서 접속 허용
    }
}
