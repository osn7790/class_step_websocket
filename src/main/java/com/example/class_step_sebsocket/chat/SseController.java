package com.example.class_step_sebsocket.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class SseController {

    private final SseService sseService;

    // 클라이언트가 SSE 연결 요청하는 엔드 포인트
    // - MediaType.TEXT_EVENT_STREAM_VALUE : SSE  전용 Content-Type
    // - 클라이언트가 아래 주소로 연결 요청
    @GetMapping(value = "/sse/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect() {
        // 클라이언트 고유 ID 생성 (UUID 사용할 예정)
        String clientId = UUID.randomUUID().toString();

        return sseService.createConnection(clientId);
    }

}
