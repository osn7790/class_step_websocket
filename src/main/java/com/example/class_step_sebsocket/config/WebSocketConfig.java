package com.example.class_step_sebsocket.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@RequiredArgsConstructor
@Configuration
// @EnableWebSocket // WebSocket 기능 활성화
// STOMP
@EnableWebSocketMessageBroker // stomp 메시지 브로커 기능 활성화
// WebSocketMessageBrokerConfigurer 수정
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 메시지 브로커 설정
     * - 브로커는 메세지를 받아서 구독자들에게 배포하는 중간 관리자
     */

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        // /topic <-- 다수의 클라이언트에게 브로드캐스트하는 채널(임의로 변수명설정가능)
        // /user <-- 특정 사용자에게만 개별 메세지를 보내는 채널(임의로 변수명설정가능)
        registry.enableSimpleBroker("/topic", "/user");

        // 클라이언트가 우리쪽으로 보내는 형식 설정
        registry.setApplicationDestinationPrefixes("/app");

    }

    // STOP 엔드포인트 등록

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        /**
         * /ws - STOMP 연결을 위한 엔드 포인트
         * - withSockJS : 브라우저 호환성을 위해 설정 함
         * // 폴링, 이벤트소스, JSONP 등으로 대체
         */

        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
